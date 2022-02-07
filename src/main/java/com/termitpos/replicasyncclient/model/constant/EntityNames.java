package com.termitpos.replicasyncclient.model.constant;

import com.termitpos.replicasyncclient.model.SyncableEntityProperties;

public class EntityNames {


    public static final SyncableEntityProperties[] otherEntitiesPart1 = new SyncableEntityProperties[]{
            new SyncableEntityProperties("public", "privileges", "('id', id)")
    };

    public static final SyncableEntityProperties[] otherEntitiesPart2 = new SyncableEntityProperties[]{
            new SyncableEntityProperties("public", "productgroups", "('id', id)"),
            new SyncableEntityProperties("public", "roles_privileges", "('role_id', role_id, 'privilege_id', privilege_id)",
                    "('role_id', old_data ->> 'role_id', 'privilege_id', old_data ->> 'privilege_id')"),
            new SyncableEntityProperties("public", "sections_cards", "('section_id',section_id, 'card_id', card_id)",
                    "('section_id', old_data ->> 'section_id', 'card_id', old_data ->> 'card_id')"),
            new SyncableEntityProperties("public", "sections_notes", "('section_id',section_id, 'note_id', note_id)",
                    "('section_id', old_data ->> 'section_id', 'note_id', old_data ->> 'note_id')"),
    };
    public static final SyncableEntityProperties[] saleRelatedEntitiesPart1 = new SyncableEntityProperties[]{
            new SyncableEntityProperties("public", "orders", "('id', id)"),
    };

    public static final SyncableEntityProperties[] saleRelatedEntitiesPart2 = new SyncableEntityProperties[]{
            new SyncableEntityProperties("public", "menugroups", "('id', id)"),
    };
}
