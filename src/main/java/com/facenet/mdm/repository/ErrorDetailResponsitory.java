package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ErrorDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ErrorDetailResponsitory extends JpaRepository<ErrorDetailEntity, Integer> {

    @Query(value = "SELECT er FROM ErrorDetailEntity er WHERE er.errorId = :errorId and er.errorGroupId = :errorGroupId")
    ErrorDetailEntity getErrorDetailEntitiesByIdErrorG(@Param("errorId") Integer errorId, @Param("errorGroupId") Integer errorGroupId);

    @Query(value = "SELECT * FROM error_detail WHERE error_id = ?1", nativeQuery = true)
    List<ErrorDetailEntity> getErrorDetailEntitiesByErrorID(Integer errorId);

}
