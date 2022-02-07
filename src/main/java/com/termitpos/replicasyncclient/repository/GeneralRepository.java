package com.termitpos.replicasyncclient.repository;

import com.termitpos.replicasyncclient.entity.Section;
import com.termitpos.replicasyncclient.entity.SyncableEntity;
import com.termitpos.replicasyncclient.model.SyncableEntityProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class GeneralRepository {
    private final JdbcTemplate jdbcTemplate;

    public GeneralRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<SyncableEntity> getEntitiesPendingSyncByLimit(SyncableEntityProperties entityProperties,
                                                              int limit) {
        return jdbcTemplate.query(
                "select jsonb_build_object" + entityProperties.getRowIdentifiers() +
                        " as rowIdentifiers, to_json(o.*) as entity from " +
                        entityProperties.getFullname() +
                        " o where remote_sync_state = ? or remote_sync_state is null limit ? ",
                (rs, rowNum) ->
                        new SyncableEntity(rs.getString("rowIdentifiers"),
                                rs.getString("entity"),
                                entityProperties.getName()),
                "PENDING_SYNC", limit);
    }

    @Transactional(readOnly = true)
    public List<SyncableEntity> getDeletedEntitiesPendingSyncByLimit(SyncableEntityProperties entityProperties,
                                                                     int limit) {
        return jdbcTemplate.query(
                "select jsonb_build_object('id', id)" +
                        " as logRowIdentifiers, jsonb_build_object" + entityProperties.getLogSelectRowIdentifiers() +
                        " as rowIdentifiers, o.old_data as entity from audit.logged_actions o " +
                        "where (remote_sync_state = ? or remote_sync_state is null) and o.action='D' and o.table_name=?  limit ? ",
                (rs, rowNum) ->
                        new SyncableEntity(rs.getString("rowIdentifiers"),
                                rs.getString("logRowIdentifiers"),
                                rs.getString("entity"),
                                entityProperties.getName()),
                "PENDING_SYNC", entityProperties.getName(), limit);
    }

    @Transactional(readOnly = true)
    public Section getSection() {
        return jdbcTemplate.queryForObject(
                "select * from public.sections where is_active = true",
                (rs, rowNum) ->
                        new Section(rs.getLong("id"), rs.getString("uuid"))
        );
    }

    @Transactional
    public void updateEntitiesFromStateToState(String name,
                                               String toState,
                                               String fromState,
                                               String whereClause) {
        jdbcTemplate.update(
                "update " + name + " set remote_sync_state=?, remote_sync_date=now() " +
                        " where (remote_sync_state = ? or remote_sync_state is null) and " + whereClause,
                toState, fromState);
    }

    @Transactional
    public void updateLogFromStateToState(String name,
                                          String toState,
                                          String fromState,
                                          String whereClause) {
        jdbcTemplate.update(
                "update audit.logged_actions set remote_sync_state=?, remote_sync_date=now() " +
                        " where (remote_sync_state = ? or remote_sync_state is null) and  action='D' " +
                        "and table_name=? and " + whereClause,
                toState, fromState, name);
    }
}
