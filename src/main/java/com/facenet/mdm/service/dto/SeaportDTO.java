package com.facenet.mdm.service.dto;

import com.facenet.mdm.anotation.SecuredField;
import java.io.Serializable;
import java.time.Instant;

public class SeaportDTO extends BaseDynamicDTO implements Serializable {

    private Integer id;

    @SecuredField("VMS_seaportCode::view")
    private String seaportCode;

    @SecuredField("VMS_seaportName::view")
    private String seaportName;

    private String seaportNation;

    private String seaportAddress;

    private String latitude;

    private String longitude;

    private String note;

    private Integer status;

    private Instant updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeaportCode() {
        return seaportCode;
    }

    public void setSeaportCode(String seaportCode) {
        this.seaportCode = seaportCode;
    }

    public String getSeaportName() {
        return seaportName;
    }

    public void setSeaportName(String seaportName) {
        this.seaportName = seaportName;
    }

    public String getSeaportNation() {
        return seaportNation;
    }

    public void setSeaportNation(String seaportNation) {
        this.seaportNation = seaportNation;
    }

    public String getSeaportAddress() {
        return seaportAddress;
    }

    public void setSeaportAddress(String seaportAddress) {
        this.seaportAddress = seaportAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
