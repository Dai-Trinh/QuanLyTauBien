package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.querydsl.core.annotations.QueryProjection;
import java.io.Serializable;
import java.util.*;
import javax.persistence.Column;

public class ProductionStageDTO implements Serializable {

    private String productionStageCode;
    private String productionStageName;
    private String description;
    private Integer status;

    private int level;
    private Map<String, String> stageMap = new HashMap<>();

    private List<JobDTO> jobDTOList;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMap(Map<String, String> stageMap) {
        this.stageMap = stageMap;
    }

    @QueryProjection
    public ProductionStageDTO(String productionStageCode, String productionStageName, String description, Integer status) {
        this.productionStageCode = productionStageCode;
        this.productionStageName = productionStageName;
        this.description = description;
        this.status = status;
    }

    public List<JobDTO> getJobDTOList() {
        return jobDTOList;
    }

    public void setJobDTOList(List<JobDTO> jobDTOList) {
        this.jobDTOList = jobDTOList;
    }

    public ProductionStageDTO() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionStageDTO entity = (ProductionStageDTO) o;
        return (
            Objects.equals(this.productionStageCode, entity.productionStageCode) &&
            Objects.equals(this.productionStageName, entity.productionStageName) &&
            Objects.equals(this.description, entity.description) &&
            Objects.equals(this.status, entity.status)
        );
    }

    @JsonAnyGetter
    public Map<String, String> getStageMap() {
        return stageMap;
    }

    @JsonAnySetter
    public void setStageMap(String key, String value) {
        stageMap.put(key, value);
    }
}
