package com.facenet.mdm.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "error_group")
public class ErrorGroupEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer errorGroupId;

    @Column(name = "error_group_code", length = 50, nullable = false)
    private String errorGroupCode;

    @Column(name = "error_group_name", nullable = false)
    private String errorGroupName;

    @Column(name = "error_group_description", nullable = false)
    private String errorGroupDesc;

    @Column(name = "error_group_type", length = 45, nullable = false)
    private String errorGroupType;

    @Column(name = "status", nullable = false)
    private Integer errorGroupStatus;

    @Column(name = "is_active", nullable = false)
    private Integer isActive;

    @ManyToMany(mappedBy = "errorGroupEntities", fetch = FetchType.EAGER)
    private Set<ErrorEntity> errorEntities = new HashSet<>();

    public ErrorGroupEntity() {}

    public ErrorGroupEntity(ErrorGroupEntity that) {
        this.errorGroupCode = that.errorGroupCode;
        this.errorGroupName = that.errorGroupName;
        this.errorGroupDesc = that.errorGroupDesc;
        this.errorGroupType = that.errorGroupType;
        this.errorGroupStatus = that.errorGroupStatus;
        this.errorEntities = that.errorEntities;
    }

    public Integer getErrorGroupId() {
        return errorGroupId;
    }

    public void setErrorGroupId(Integer errorGroupId) {
        this.errorGroupId = errorGroupId;
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

    public String getErrorGroupType() {
        return errorGroupType;
    }

    public void setErrorGroupType(String errorGroupType) {
        this.errorGroupType = errorGroupType;
    }

    public Integer getErrorGroupStatus() {
        return errorGroupStatus;
    }

    public void setErrorGroupStatus(Integer errorGroupStatus) {
        this.errorGroupStatus = errorGroupStatus;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Set<ErrorEntity> getErrorEntities() {
        return errorEntities;
    }

    public void setErrorEntities(Set<ErrorEntity> errorEntities) {
        this.errorEntities = errorEntities;
    }

    @Override
    public Integer getId() {
        return this.errorGroupId;
    }
}
