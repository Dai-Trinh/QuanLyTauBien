package com.facenet.mdm.service.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.querydsl.core.types.Order;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ErrorFilter implements Serializable {

    private String errorCode;
    private String errorName;

    private String errorGroup;
    private String errorDesc;
    private String errorType;
    private String errorStatus;

    private Map<String, String> errorMap = new HashMap<>();

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorGroup() {
        return errorGroup;
    }

    public void setErrorGroup(String errorGroup) {
        this.errorGroup = errorGroup;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    @JsonAnyGetter
    public Map<String, String> getErrorMap() {
        return errorMap;
    }

    @JsonAnySetter
    public void setErrorMap(String key, String value) {
        this.errorMap.put(key, value);
    }
}
