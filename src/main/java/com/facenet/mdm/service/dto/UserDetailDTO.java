package com.facenet.mdm.service.dto;

import java.util.List;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class UserDetailDTO {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String employeeCode;
    private List<String> effectiveRole;
    private List<String> assignedRole;
    private List<String> availableRole;

    public UserDetailDTO() {}

    public UserDetailDTO(UserRepresentation user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public List<String> getEffectiveRole() {
        return effectiveRole;
    }

    public void setEffectiveRole(List<String> effectiveRole) {
        this.effectiveRole = effectiveRole;
    }

    public List<String> getAssignedRole() {
        return assignedRole;
    }

    public void setAssignedRole(List<String> assignedRole) {
        this.assignedRole = assignedRole;
    }

    public List<String> getAvailableRole() {
        return availableRole;
    }

    public void setAvailableRole(List<String> availableRole) {
        this.availableRole = availableRole;
    }
}
