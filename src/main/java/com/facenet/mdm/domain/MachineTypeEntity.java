package com.facenet.mdm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "machine_type")
public class MachineTypeEntity extends AbstractAuditingEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_type_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @NotEmpty(message = "{machine.type.not.empty}")
    @Column(name = "machine_type_name", nullable = false)
    private String machineTypeName;

    @Column(name = "is_active")
    @JsonIgnore
    private Boolean isActive = true;

    @Override
    public String toString() {
        return machineTypeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMachineTypeName() {
        return machineTypeName;
    }

    public void setMachineTypeName(String machineTypeName) {
        this.machineTypeName = machineTypeName;
    }

    @JsonIgnore
    public Boolean getActive() {
        return isActive;
    }

    @JsonIgnore
    public void setActive(Boolean active) {
        isActive = active;
    }

    public MachineTypeEntity() {}

    public MachineTypeEntity(String machineTypeName) {
        this.machineTypeName = machineTypeName;
    }
}
