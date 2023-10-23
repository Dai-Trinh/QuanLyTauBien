package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ErrorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ErrorResponesitory extends JpaRepository<ErrorEntity, Integer> {


    @Query(value = "select er from ErrorEntity er " +
        "where er.isActive = 1 " +
        "and (:errorCode is null or er.errorCode like %:errorCode%) " +
        "and (:errorName is null or er.errorName like %:errorName%) " +
        "and (:errorDesc is null or er.errorDesc like %:errorDesc%) " +
        "and (:errorType is null or er.errorType like %:errorType%) " +
        "and (:errorStatus is null or er.errorStatus = :errorStatus)"
    )
    Page<ErrorEntity> getErrorEntitiesByPaging(@Param("errorCode") String errorCode,
                                               @Param("errorName") String errorName,
                                               @Param("errorDesc") String errorDesc,
                                               @Param("errorType") String errorType,
                                               @Param("errorStatus") Integer errorStatus,
                                               Pageable pageable);

    @Query(value = "select er from ErrorEntity er " +
        "where er.isActive = 1 and(" +
        "(er.errorCode like %:errorCode%) " +
        "or (er.errorName like %:errorName%) " +
        "or (er.errorDesc like %:errorDesc%) " +
        "or (er.errorType like %:errorType%) " +
        "or (er.errorStatus = :errorStatus))"
    )
    Page<ErrorEntity> getErrorEntitiesByPagingAndSearch(@Param("errorCode") String errorCode,
                                               @Param("errorName") String errorName,
                                               @Param("errorDesc") String errorDesc,
                                               @Param("errorType") String errorType,
                                               @Param("errorStatus") Integer errorStatus,
                                               Pageable pageable);

    @Query(value = "SELECT * FROM error WHERE is_active = 1 and error_code = ?1", nativeQuery = true)
    ErrorEntity getErrorEntitiesByCode(String errorCode);

}
