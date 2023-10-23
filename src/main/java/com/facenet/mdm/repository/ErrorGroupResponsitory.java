package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ErrorGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ErrorGroupResponsitory extends JpaRepository<ErrorGroupEntity, Integer> {

    @Query(value = "select eg from ErrorGroupEntity eg " +
        "where eg.isActive = 1 " +
        "and (:errorGroupCode is null or eg.errorGroupCode like %:errorGroupCode%) " +
        "and (:errorGroupName is null or eg.errorGroupName like %:errorGroupName%) " +
        "and (:errorGroupDesc is null or eg.errorGroupDesc like %:errorGroupDesc%) " +
        "and (:errorGroupType is null or eg.errorGroupType like %:errorGroupType%) " +
        "and (:errorGroupStatus is null or eg.errorGroupStatus = :errorGroupStatus)"
    )
    Page<ErrorGroupEntity> getErrorGroupEntitiesWithPaging(@Param("errorGroupCode") String errorGroupCode,
                                                      @Param("errorGroupName") String errorGroupName,
                                                      @Param("errorGroupDesc") String errorGroupDesc,
                                                      @Param("errorGroupType") String errorGroupType,
                                                      @Param("errorGroupStatus") Integer errorGroupStatus,
                                                      Pageable pageable);

    @Query(value = "select eg from ErrorGroupEntity eg " +
        "where eg.isActive = 1 and (" +
        "(eg.errorGroupCode like %:errorGroupCode%) " +
        "or (eg.errorGroupName like %:errorGroupName%) " +
        "or (eg.errorGroupDesc like %:errorGroupDesc%) " +
        "or (eg.errorGroupType like %:errorGroupType%) " +
        "or (eg.errorGroupStatus = :errorGroupStatus))"
    )
    Page<ErrorGroupEntity> getErrorGroupEntitiesWithPagingAndSearch(@Param("errorGroupCode") String errorGroupCode,
                                                           @Param("errorGroupName") String errorGroupName,
                                                           @Param("errorGroupDesc") String errorGroupDesc,
                                                           @Param("errorGroupType") String errorGroupType,
                                                           @Param("errorGroupStatus") Integer errorGroupStatus,
                                                           Pageable pageable);


    @Query(value = "SELECT * FROM error_group WHERE is_active = 1 and error_group_code = ?1", nativeQuery = true)
    ErrorGroupEntity getErrorGroupEntitiesByCode(String errorCode);


}
