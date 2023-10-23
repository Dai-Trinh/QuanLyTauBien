package com.facenet.mdm.service.dto;

import com.querydsl.core.annotations.QueryProjection;

public class BusinessLogDetailDTO {

    private String keyTitle;
    private String lastValue;
    private String newValue;

    public BusinessLogDetailDTO() {}

    @QueryProjection
    public BusinessLogDetailDTO(String keyTitle, String lastValue, String newValue) {
        this.keyTitle = keyTitle;
        this.lastValue = lastValue;
        this.newValue = newValue;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
