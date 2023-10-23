package com.facenet.mdm.service.dto;

import java.time.Instant;
import java.util.Date;

public class ErrorQMSDTO {

    private Integer id;

    private String errorCode;

    private String errorName;

    private String errorDesc;
    private String createdBy;
    private Instant createdAt;
    private Integer errorStatus;

    public ErrorQMSDTO() {
    }

    public ErrorQMSDTO(Integer id, String errorCode, String errorName, String errorDesc, String createdBy, Instant createdAt, Integer errorStatus) {
        this.id = id;
        this.errorCode = errorCode;
        this.errorName = errorName;
        this.errorDesc = errorDesc;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.errorStatus = errorStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(Integer errorStatus) {
        this.errorStatus = errorStatus;
    }
}
