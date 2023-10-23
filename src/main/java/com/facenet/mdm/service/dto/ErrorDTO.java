package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorDTO implements Serializable {

    private String errorCode;

    private String errorName;

    private List<String> errorGroup = new ArrayList<>();

    private String errorDesc;

    private String errorType;

    private String errorStatus;

    public ErrorDTO() {
    }

    public ErrorDTO(String errorCode, String errorName, String errorDesc, String errorType, String errorStatus) {
        this.errorCode = errorCode;
        this.errorName = errorName;
        this.errorDesc = errorDesc;
        this.errorType = errorType;
        this.errorStatus = errorStatus;
    }

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

    public void setErrorGroup(List<String> errorGroup) {
        this.errorGroup = errorGroup;
    }

    public List<String> getErrorGroup() {
        return errorGroup;
    }

    @JsonAnyGetter
    public Map<String, String> getErrorMap() {
        return errorMap;
    }

    @JsonAnySetter
    public void setErrorMap(String key, String vale) {
        this.errorMap.put(key, vale);
    }
}
