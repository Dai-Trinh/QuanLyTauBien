package com.facenet.mdm.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "team_group")
public class TeamGroupEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamGroupId;

    @Column(name = "team_group_code", length = 50)
    private String teamGroupCode;

    @Column(name = "team_group_name")
    private String teamGroupName;

    @Column(name = "team_group_quota")
    private Integer teamGroupQuota;

    @Column(name = "team_group_note")
    private String teamGroupNote;

    @Column(name = "team_group_status")
    private Integer teamGroupStatus;

    @OneToMany(mappedBy = "teamGroup", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<EmployeeEntity> employeeEntitySet = new HashSet<>();

    @Column(name = "is_active")
    private Boolean isActive;

    public TeamGroupEntity() {}

    public TeamGroupEntity(TeamGroupEntity that) {
        this.teamGroupCode = that.teamGroupCode;
        this.teamGroupName = that.teamGroupName;
        this.teamGroupNote = that.teamGroupNote;
        this.teamGroupQuota = that.teamGroupQuota;
        this.teamGroupStatus = that.teamGroupStatus;
        this.employeeEntitySet = that.employeeEntitySet;
    }

    public Integer getTeamGroupId() {
        return teamGroupId;
    }

    public void setTeamGroupId(Integer teamGroupId) {
        this.teamGroupId = teamGroupId;
    }

    public String getTeamGroupCode() {
        return teamGroupCode;
    }

    public void setTeamGroupCode(String teamGroupCode) {
        this.teamGroupCode = teamGroupCode;
    }

    public String getTeamGroupName() {
        return teamGroupName;
    }

    public void setTeamGroupName(String teamGroupName) {
        this.teamGroupName = teamGroupName;
    }

    public Integer getTeamGroupQuota() {
        return teamGroupQuota;
    }

    public void setTeamGroupQuota(Integer teamGroupQuota) {
        this.teamGroupQuota = teamGroupQuota;
    }

    public String getTeamGroupNote() {
        return teamGroupNote;
    }

    public void setTeamGroupNote(String teamGroupNote) {
        this.teamGroupNote = teamGroupNote;
    }

    public Integer getTeamGroupStatus() {
        return teamGroupStatus;
    }

    public void setTeamGroupStatus(Integer teamGroupStatus) {
        this.teamGroupStatus = teamGroupStatus;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Set<EmployeeEntity> getEmployeeEntitySet() {
        return employeeEntitySet;
    }

    public void setEmployeeEntitySet(Set<EmployeeEntity> employeeEntitySet) {
        this.employeeEntitySet = employeeEntitySet;
    }

    @Override
    public Integer getId() {
        return this.teamGroupId;
    }
}
