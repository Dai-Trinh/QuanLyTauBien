package com.facenet.mdm.repository;

import com.facenet.mdm.domain.MachineEntity;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MachineRepository extends JpaRepository<MachineEntity, Integer> {
    MachineEntity findByMachineCodeIgnoreCaseAndIsActiveTrue(String machineCode);

    @Query(
        "select m from MachineEntity m " + "where m.isActive = true "
        //        "(:machineCode is null or m.machineCode like %:machineCode%) and " +
        //        "(:machineName is null or m.machineName like %:machineName%)  "
        //        "(:machineType is null or m.machineType = :machineType)"
    )
    Page<MachineEntity> getAll(
        //        @Param("machineCode") String machineCode,
        //        @Param("machineName") String machineName,
        //        @Param("machineType") Integer machineType,
        Pageable pageable
    );

    @Query("select m.machineCode from MachineEntity m where m.isActive = true and m.machineCode in :machineCode")
    Set<String> getAllMachineCodeIn(@Param("machineCode") Collection<String> machineCode);
}
