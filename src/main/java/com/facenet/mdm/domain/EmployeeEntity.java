package com.facenet.mdm.domain;

import javax.persistence.*;

@Entity
@Table(name = "employee")
public class EmployeeEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;

    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "username")
    private String username;

    @Column(name = "employee_name", length = 50)
    private String employeeName;

    @Column(name = "employee_phone", length = 20)
    private String employeePhone;

    @Column(name = "employee_email")
    private String employeeEmail;

    @Column(name = "employee_note")
    private String employeeNote;

    @Column(name = "employee_status")
    private Integer employeeStatus;

    @ManyToOne
    @JoinColumn(name = "team_group_id")
    private TeamGroupEntity teamGroup;

    @Column(name = "is_active")
    private Boolean isActive;

    public EmployeeEntity() {}

    public EmployeeEntity(EmployeeEntity that) {
        this.employeeCode = that.employeeCode;
        this.employeeName = that.employeeName;
        this.employeeNote = that.employeeNote;
        this.employeeEmail = that.employeeEmail;
        this.employeePhone = that.employeePhone;
        this.employeeStatus = that.employeeStatus;
        this.teamGroup = that.teamGroup;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getEmployeeNote() {
        return employeeNote;
    }

    public void setEmployeeNote(String employeeNote) {
        this.employeeNote = employeeNote;
    }

    public Integer getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(Integer employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public TeamGroupEntity getTeamGroup() {
        return teamGroup;
    }

    public void setTeamGroup(TeamGroupEntity teamGroup) {
        this.teamGroup = teamGroup;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Override
    public Integer getId() {
        return this.employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
