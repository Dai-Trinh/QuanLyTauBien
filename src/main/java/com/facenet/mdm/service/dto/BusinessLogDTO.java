package com.facenet.mdm.service.dto;

import java.time.Instant;

public class BusinessLogDTO {

    private String userName;
    private String actionName;
    private String functionName;
    private Instant startCreatedAt;
    private Instant endCreatedAt;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Instant getStartCreatedAt() {
        return startCreatedAt;
    }

    public void setStartCreatedAt(Instant startCreatedAt) {
        this.startCreatedAt = startCreatedAt;
    }

    public Instant getEndCreatedAt() {
        return endCreatedAt;
    }

    public void setEndCreatedAt(Instant endCreatedAt) {
        this.endCreatedAt = endCreatedAt;
    }
}
