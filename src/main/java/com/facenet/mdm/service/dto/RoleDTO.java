package com.facenet.mdm.service.dto;

import java.util.List;
import org.keycloak.representations.idm.RoleRepresentation;

public class RoleDTO {

    private List<String> effectiveRole;
    private List<String> assignedRole;
    private List<String> availableRole;

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
