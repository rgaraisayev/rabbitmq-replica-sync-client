package com.termitpos.replicasyncclient.model;

import java.io.Serializable;

public class SyncableEntityProperties implements Serializable {
    private String schema;
    private String name;
    private String rowIdentifiers;
    private String logSelectRowIdentifiers;

    public SyncableEntityProperties() {
    }

    public SyncableEntityProperties(String schema, String name, String rowIdentifiers) {
        this.schema = schema;
        this.name = name;
        this.rowIdentifiers = rowIdentifiers;
        this.logSelectRowIdentifiers = "('id', old_data ->> 'id')";
    }


    public SyncableEntityProperties(String schema, String name, String rowIdentifiers, String logSelectRowIdentifiers) {
        this.schema = schema;
        this.name = name;
        this.rowIdentifiers = rowIdentifiers;
        this.logSelectRowIdentifiers = logSelectRowIdentifiers;
    }

    public String getFullname() {
        return schema + "." + name;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRowIdentifiers() {
        return rowIdentifiers;
    }

    public void setRowIdentifiers(String rowIdentifiers) {
        this.rowIdentifiers = rowIdentifiers;
    }

    public String getLogSelectRowIdentifiers() {
        return logSelectRowIdentifiers;
    }

    public void setLogSelectRowIdentifiers(String logSelectRowIdentifiers) {
        this.logSelectRowIdentifiers = logSelectRowIdentifiers;
    }
}
