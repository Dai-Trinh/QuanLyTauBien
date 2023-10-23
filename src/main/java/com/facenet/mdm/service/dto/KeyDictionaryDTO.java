package com.facenet.mdm.service.dto;

import java.io.Serializable;

public class KeyDictionaryDTO implements Serializable {

    private Integer id;
    private String keyName;
    private String keyTitle;
    private String width = "200px";
    private Boolean check;
    private Integer dataType;
    private Integer entityType;
    private Integer entryIndex;
    private Boolean isRequired;
    private Integer isFixed;

    public KeyDictionaryDTO() {}

    public KeyDictionaryDTO(
        Integer id,
        String keyName,
        String keyTitle,
        String width,
        Boolean check,
        Integer dataType,
        Integer entityType,
        Integer entryIndex,
        Integer isFixed
    ) {
        this.id = id;
        this.keyName = keyName;
        this.keyTitle = keyTitle;
        this.width = width;
        this.check = check;
        this.dataType = dataType;
        this.entityType = entityType;
        this.entryIndex = entryIndex;
        this.isFixed = isFixed;
    }

    public Integer getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Integer isFixed) {
        this.isFixed = isFixed;
    }

    public KeyDictionaryDTO(String keyName, String keyTitle, String width, Boolean check, Integer dataType, Integer entityType) {
        this.keyName = keyName;
        this.keyTitle = keyTitle;
        this.width = width;
        this.check = check;
        this.dataType = dataType;
        this.entityType = entityType;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyTitle() {
        return keyTitle;
    }

    public void setKeyTitle(String keyTitle) {
        this.keyTitle = keyTitle;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public Integer getEntryIndex() {
        return entryIndex;
    }

    public void setEntryIndex(Integer entryIndex) {
        this.entryIndex = entryIndex;
    }

    public Boolean getIsRequired() {
        return this.isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
