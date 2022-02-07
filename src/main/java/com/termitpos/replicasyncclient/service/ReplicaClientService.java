package com.termitpos.replicasyncclient.service;

import com.termitpos.replicasyncclient.component.MessagePublisher;
import com.termitpos.replicasyncclient.entity.Section;
import com.termitpos.replicasyncclient.entity.SyncableEntity;
import com.termitpos.replicasyncclient.model.SelectAction;
import com.termitpos.replicasyncclient.model.SyncActionEvent;
import com.termitpos.replicasyncclient.model.SyncableEntityProperties;
import com.termitpos.replicasyncclient.repository.GeneralRepository;
import com.termitpos.replicasyncclient.util.EntityUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.List;

@Service
public class ReplicaClientService {

    private final GeneralRepository generalRepository;
    private final MessagePublisher messagePublisher;

    public ReplicaClientService(GeneralRepository generalRepository, MessagePublisher messagePublisher) {
        this.generalRepository = generalRepository;
        this.messagePublisher = messagePublisher;
    }


    public void sendUpstreamData(SyncableEntityProperties[] entityNamesToSync,
                                 String exchangeEx1,
                                 String routingKeyR1) {
        Section section = generalRepository.getSection();
        SyncActionEvent syncActionEvent = new SyncActionEvent(section);
        HashMap<SelectAction, List<SyncableEntity>> syncableEntityPropertiesListHashMap = new HashMap<>();
        for (SyncableEntityProperties entityProperties : entityNamesToSync) {
            List<SyncableEntity> deletedEntitiesPendingSyncByLimit = generalRepository
                    .getDeletedEntitiesPendingSyncByLimit(entityProperties, 200);

            List<SyncableEntity> entitiesPendingSyncByLimit = generalRepository
                    .getEntitiesPendingSyncByLimit(entityProperties, 500);

            if (!deletedEntitiesPendingSyncByLimit.isEmpty()) {
                syncActionEvent.add(deletedEntitiesPendingSyncByLimit);
                syncableEntityPropertiesListHashMap.put(new SelectAction("audit.logged_actions",
                        true), deletedEntitiesPendingSyncByLimit);
            }
            if (!entitiesPendingSyncByLimit.isEmpty()) {
                syncActionEvent.add(entitiesPendingSyncByLimit);
                syncableEntityPropertiesListHashMap.put(new SelectAction(entityProperties.getFullname(),
                        false), entitiesPendingSyncByLimit);
            }
        }
        if (syncActionEvent.getEntitiesToSync().isEmpty())
            return;

        ListenableFuture<SyncActionEvent> syncActionEventListenableFuture =
                messagePublisher.publishAndReceive(exchangeEx1, routingKeyR1, syncActionEvent);

        for (SelectAction selectAction : syncableEntityPropertiesListHashMap.keySet()) {
            List<SyncableEntity> entitiesToSync = syncableEntityPropertiesListHashMap.get(selectAction);
            String whereClauseFromRowIdents;
            if (selectAction.isDelete()) {
                whereClauseFromRowIdents = EntityUtil.generateWhereClauseFromRowIdentsFromLog(entitiesToSync);
            } else {
                whereClauseFromRowIdents = EntityUtil.generateWhereClauseFromRowIdents(entitiesToSync);
            }
            generalRepository.updateEntitiesFromStateToState(selectAction.getEntityName(),
                    "SYNC_IN_PROGRESS",
                    "PENDING_SYNC",
                    whereClauseFromRowIdents);
        }
        syncActionEventListenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

            @Override
            public void onSuccess(SyncActionEvent result) {
                HashMap<String, List<SyncableEntity>> syncedEntitiesGroupedByEntityName =
                        EntityUtil.getSyncedEntitiesGroupedByEntityName(result.getDeletedEntities());
                for (String key : syncedEntitiesGroupedByEntityName.keySet()) {
                    String whereClauseFromRowIdents = EntityUtil
                            .generateWhereClauseFromRowIdentsFromLog(syncedEntitiesGroupedByEntityName.get(key));
                    generalRepository.updateLogFromStateToState(
                            key, "SYNCED", "SYNC_IN_PROGRESS",
                            whereClauseFromRowIdents);
                }

                syncedEntitiesGroupedByEntityName =
                        EntityUtil.getSyncedEntitiesGroupedByEntityName(result.getSyncedEntities());
                for (String key : syncedEntitiesGroupedByEntityName.keySet()) {
                    String whereClauseFromRowIdents =
                            EntityUtil.generateWhereClauseFromRowIdents(syncedEntitiesGroupedByEntityName.get(key));
                    generalRepository.updateEntitiesFromStateToState(
                            key, "SYNCED", "SYNC_IN_PROGRESS",
                            whereClauseFromRowIdents);
                }
            }
        }, ex -> {
            System.out.println("ERROR: " + ex.getMessage());
        });
    }

}
