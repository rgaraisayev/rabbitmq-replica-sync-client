package com.termitpos.replicasyncclient.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.termitpos.replicasyncclient.entity.SyncableEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EntityUtil {
    private static final Gson gson = new Gson();

    public static String generateWhereClauseFromRowIdents(List<SyncableEntity> syncableEntities) {
        HashMap<String, List<Object>> primaryKeyAndValues = new HashMap<>();
        for (SyncableEntity syncableEntity : syncableEntities) {
            JsonObject ri = gson.fromJson(syncableEntity.getRowIdentifiers(), JsonObject.class);
            for (String s : ri.keySet()) {
                if (!primaryKeyAndValues.containsKey(s)) {
                    primaryKeyAndValues.put(s, new ArrayList<>());
                }
                primaryKeyAndValues.get(s).add(ri.get(s));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : primaryKeyAndValues.keySet()) {
            sb.append(s).append(" in (");
            sb.append(primaryKeyAndValues.get(s).stream().map(Object::toString)
                    .collect(Collectors.joining(",")));
            sb.append(")").append(" and ");
        }
        sb.append(" 1=1 ");
        return sb.toString();
    }

    public static String generateWhereClauseFromRowIdentsFromLog(List<SyncableEntity> syncableEntities) {
        HashMap<String, List<Object>> primaryKeyAndValues = new HashMap<>();
        for (SyncableEntity syncableEntity : syncableEntities) {
            JsonObject ri = gson.fromJson(syncableEntity.getLogRowIdentifiers(), JsonObject.class);
            for (String s : ri.keySet()) {
                if (!primaryKeyAndValues.containsKey(s)) {
                    primaryKeyAndValues.put(s, new ArrayList<>());
                }
                primaryKeyAndValues.get(s).add(ri.get(s));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : primaryKeyAndValues.keySet()) {
            sb.append(s).append(" in (");
            sb.append(primaryKeyAndValues.get(s).stream().map(Object::toString)
                    .collect(Collectors.joining(",")));
            sb.append(")").append(" and ");
        }
        sb.append(" 1=1 ");
        return sb.toString();
    }

    public static HashMap<String, List<SyncableEntity>> getSyncedEntitiesGroupedByEntityName(
            List<SyncableEntity> syncedEntities) {
        HashMap<String, List<SyncableEntity>> syncedEntitiesGroupedByEntityName = new HashMap<>();
        for (SyncableEntity syncedEntity : syncedEntities) {
            if (!syncedEntitiesGroupedByEntityName.containsKey(syncedEntity.getEntityName())) {
                List<SyncableEntity> entities = new ArrayList<>();
                entities.add(syncedEntity);
                syncedEntitiesGroupedByEntityName.put(syncedEntity.getEntityName(),
                        entities);
            } else syncedEntitiesGroupedByEntityName.get(syncedEntity.getEntityName()).add(syncedEntity);
        }
        return syncedEntitiesGroupedByEntityName;
    }
}
