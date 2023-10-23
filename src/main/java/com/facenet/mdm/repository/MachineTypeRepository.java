package com.facenet.mdm.repository;

import com.facenet.mdm.domain.MachineTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineTypeRepository extends JpaRepository<MachineTypeEntity, Integer> {
    MachineTypeEntity findByMachineTypeNameIgnoreCase(String machineTypeName);
}
