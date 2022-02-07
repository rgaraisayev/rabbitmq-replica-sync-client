package com.termitpos.replicasyncclient.model;


import java.io.Serializable;

public class SelectAction implements Serializable {


    private String entityName;
    private boolean delete;

    public SelectAction() {

    }

    public SelectAction(String entityName, boolean delete) {
        this.entityName = entityName;
        this.delete = delete;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "SelectAction{" +
                "entityName='" + entityName + '\'' +
                ", delete=" + delete +
                '}';
    }
}
