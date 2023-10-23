package com.facenet.mdm.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "error")
public class ErrorEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer errorId;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_name", nullable = false)
    private String errorName;

    @Column(name = "error_description", nullable = false)
    private String errorDesc;

    @Column(name = "status", nullable = false)
    private int errorStatus;

    @Column(name = "error_type", length = 45, nullable = false)
    private String errorType;

    @Column(name = "is_active", nullable = false)
    private int isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "link_error_error_group",
        joinColumns = @JoinColumn(name = "error_id"),
        inverseJoinColumns = @JoinColumn(name = "error_group_id")
    )
    private Set<ErrorGroupEntity> errorGroupEntities = new HashSet<>();

    public ErrorEntity() {}

    public ErrorEntity(ErrorEntity that) {
        this.errorCode = that.errorCode;
        this.errorName = that.errorName;
        this.errorDesc = that.errorDesc;
        this.errorStatus = that.errorStatus;
        this.errorType = that.errorType;
        this.errorGroupEntities = that.getErrorGroupEntities();
    }

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
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

    public int getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(int errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Set<ErrorGroupEntity> getErrorGroupEntities() {
        return errorGroupEntities;
    }

    public void setErrorGroupEntities(Set<ErrorGroupEntity> errorGroupEntities) {
        this.errorGroupEntities = errorGroupEntities;
    }

    @Override
    public Integer getId() {
        return this.errorId;
    }
}
