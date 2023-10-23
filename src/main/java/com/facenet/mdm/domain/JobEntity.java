package com.facenet.mdm.domain;

import javax.persistence.*;

@Entity
@Table(name = "job")
public class JobEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id", nullable = false)
    private Integer id;

    @Column(name = "job_code")
    private String jobCode;

    @Column(name = "production_stage_code")
    private String productionStageCode;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Integer status;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JobEntity() {}

    public JobEntity(JobEntity that) {
        this.id = that.id;
        this.jobCode = that.jobCode;
        this.productionStageCode = that.productionStageCode;
        this.jobName = that.jobName;
        this.description = that.description;
        this.status = that.status;
    }
}
