package com.facenet.mdm.repository;

import com.facenet.mdm.domain.EmployeeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    @Query("select em from EmployeeEntity em where em.isActive = true and em.employeeCode = :employeeCode")
    EmployeeEntity getEmployeeEntitieByCode(@Param("employeeCode") String employeeCode);

    @Query("select em from EmployeeEntity em where em.isActive = true and  em.teamGroup.teamGroupCode = :teamGroupCode")
    List<EmployeeEntity> getEmployeeEntityByTeamGroupCode(@Param("teamGroupCode") String teamGroupCode);

    EmployeeEntity findByUsernameEqualsIgnoreCaseAndIsActiveTrue(String username);
}
