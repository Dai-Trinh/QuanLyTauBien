package com.facenet.mdm.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class JobDTO implements Serializable {

    private Integer id;
    private String jobCode;
    private String productionStageCode;

    private String jobDescription;
    private String jobName;
    private Integer status;

    private int level;
    private final Map<String, String> jobMap = new HashMap<>();

    private List<JobDTO> jobDTOList;

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getProductionStageCode() {
        return productionStageCode;
    }

    public void setProductionStageCode(String productionStageCode) {
        this.productionStageCode = productionStageCode;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public JobDTO() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobDTO(String jobCode, String productionStageCode, String jobName, Integer status) {
        this.jobCode = jobCode;
        this.productionStageCode = productionStageCode;
        this.jobName = jobName;
        this.status = status;
    }

    public List<JobDTO> getJobDTOList() {
        return jobDTOList;
    }

    public void setJobDTOList(List<JobDTO> jobDTOList) {
        this.jobDTOList = jobDTOList;
    }

    @JsonAnyGetter
    public Map<String, String> getJobMap() {
        return jobMap;
    }

    @JsonAnySetter
    public void setJobMap(String key, String value) {
        jobMap.put(key, value);
    }
}
