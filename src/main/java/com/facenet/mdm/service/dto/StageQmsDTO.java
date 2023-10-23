package com.facenet.mdm.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StageQmsDTO implements Serializable {
    private Integer id;
    private String productionStageCode;
    private String productionStageName;
    private String description;
    private String createdBy;
    private Date createdAt;
    private Integer status;
    private List<JobQmsDTO> jobList = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductionStageCode() {
        return productionStageCode;
    }

    public void setProductionStageCode(String productionStageCode) {
        this.productionStageCode = productionStageCode;
    }

    public String getProductionStageName() {
        return productionStageName;
    }

    public void setProductionStageName(String productionStageName) {
        this.productionStageName = productionStageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<JobQmsDTO> getJobList() {
        return jobList;
    }

    public void setJobList(List<JobQmsDTO> jobList) {
        this.jobList = jobList;
    }
}
