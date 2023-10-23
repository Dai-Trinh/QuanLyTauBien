package com.facenet.mdm.service.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class ErrorGroupFilter {

    private String errorGroupCode;
    private String errorGroupName;
    private String errorGroupDesc;
    private String errorGroupType;
    private String errorGroupStatus;

    private Map<String, String> errorGroupMap = new HashMap<>();

    public String getErrorGroupCode() {
        return errorGroupCode;
    }

    public void setErrorGroupCode(String errorGroupCode) {
        this.errorGroupCode = errorGroupCode;
    }

    public String getErrorGroupName() {
        return errorGroupName;
    }

    public void setErrorGroupName(String errorGroupName) {
        this.errorGroupName = errorGroupName;
    }

    public String getErrorGroupDesc() {
        return errorGroupDesc;
    }

    public void setErrorGroupDesc(String errorGroupDesc) {
        this.errorGroupDesc = errorGroupDesc;
    }

    public String getErrorGroupType() {
        return errorGroupType;
    }

    public void setErrorGroupType(String errorGroupType) {
        this.errorGroupType = errorGroupType;
    }

    public String getErrorGroupStatus() {
        return errorGroupStatus;
    }

    public void setErrorGroupStatus(String errorGroupStatus) {
        this.errorGroupStatus = errorGroupStatus;
    }

    @JsonAnyGetter
    public Map<String, String> getErrorGroupMap() {
        return errorGroupMap;
    }

    @JsonAnySetter
    public void setErrorGroupMap(String key, String value) {
        this.errorGroupMap.put(key, value);
    }
}
