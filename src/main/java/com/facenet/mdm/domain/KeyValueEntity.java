package com.facenet.mdm.domain;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "key_value")
public class KeyValueEntity {

    @EmbeddedId
    private KeyValueEntityId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("keyValueId")
    @JoinColumn(name = "key_id")
    private KeyDictionaryEntity keyDictionaryEntity;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "int_value")
    private Integer intValue;

    @Column(name = "double_value")
    private Double doubleValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    @Column(name = "json_value")
    private String jsonValue;

    @Column(name = "date_value")
    private LocalDate dateValue;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public KeyValueEntityId getId() {
        return id;
    }

    public void setId(KeyValueEntityId id) {
        this.id = id;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public KeyDictionaryEntity getKeyDictionaryEntity() {
        return keyDictionaryEntity;
    }

    public void setKeyDictionaryEntity(KeyDictionaryEntity keyDictionaryEntity) {
        this.keyDictionaryEntity = keyDictionaryEntity;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(LocalDate dateValue) {
        this.dateValue = dateValue;
    }
}
