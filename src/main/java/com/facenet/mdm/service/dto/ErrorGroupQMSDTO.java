package com.facenet.mdm.service.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorGroupQMSDTO {

    private Integer id;
    private String errorGroupCode;

    private String errorGroupName;
    private String errorGroupDesc;

    private String createdBy;

    private Instant createdAt;

    private Integer errorGroupStatus;

    private List<ErrorQMSDTO> errorList = new ArrayList<>();

    public ErrorGroupQMSDTO() {
    }

    public ErrorGroupQMSDTO(Integer id, String errorGroupCode, String errorGroupName, String errorGroupDesc, String createdBy, Instant createdAt, Integer errorGroupStatus, List<ErrorQMSDTO> errorList) {
        this.id = id;
        this.errorGroupCode = errorGroupCode;
        this.errorGroupName = errorGroupName;
        this.errorGroupDesc = errorGroupDesc;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.errorGroupStatus = errorGroupStatus;
        this.errorList = errorList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getErrorGroupStatus() {
        return errorGroupStatus;
    }

    public void setErrorGroupStatus(Integer errorGroupStatus) {
        this.errorGroupStatus = errorGroupStatus;
    }

    public List<ErrorQMSDTO> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorQMSDTO> errorList) {
        this.errorList = errorList;
    }
}
