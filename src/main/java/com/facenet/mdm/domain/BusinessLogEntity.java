package com.facenet.mdm.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "business_log")
@EntityListeners(AuditingEntityListener.class)
public class BusinessLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blSeqGen")
    @GenericGenerator(name = "blSeqGen", strategy = "increment")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Integer entityId;

    @NotNull
    @Column(name = "entity_type", nullable = false)
    private Integer entityType;

    @Size(max = 250)
    @NotNull
    @Column(name = "action_name", nullable = false, length = 250)
    private String actionName;

    @Size(max = 100)
    @NotNull
    @Column(name = "function_name", nullable = false, length = 100)
    private String functionName;

    @Size(max = 50)
    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Size(max = 1024)
    @Column(name = "error_description", length = 1024)
    private String errorDescription;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Size(max = 250)
    @Column(name = "user_name", length = 250)
    @CreatedBy
    private String userName;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "created_at")
    @CreatedDate
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "businessLog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BusinessLogDetailEntity> businessLogDetails = new ArrayList<>();

    public List<BusinessLogDetailEntity> getBusinessLogDetails() {
        return businessLogDetails;
    }

    public void setBusinessLogDetails(List<BusinessLogDetailEntity> businessLogDetails) {
        this.businessLogDetails = businessLogDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }
}
