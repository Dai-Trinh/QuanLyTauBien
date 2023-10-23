package com.facenet.mdm.repository;

import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.repository.custom.JobCustomRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Integer>, JobCustomRepository {
    @Query(
        "select p from JobEntity p " +
        "where p.isActive = true and " +
        "(:jobCode is null or p.jobCode like %:jobCode%) and " +
        "(:stageCode is null or p.productionStageCode like %:stageCode%) and " +
        "(:jobName is null or p.jobName like %:jobName%) and " +
        "(:status is null or p.status = :status)"
    )
    Page<JobEntity> getAll(
        @Param("jobCode") String jobCode,
        @Param("stageCode") String stageCode,
        @Param("jobName") String jobName,
        @Param("status") Integer status,
        Pageable pageable
    );

    @Query("select j from JobEntity j where j.jobCode = :jobCode and j.isActive = :isActive")
    JobEntity findByJobCodeIgnoreCaseAndIsActive(@Param("jobCode") String jobCode, @Param("isActive") Boolean isActive);

    @Query("select j from JobEntity j where j.jobCode in :jobCodes and j.isActive = :isActive order by j.createdAt desc ")
    List<JobEntity> findListByJobCodeIgnoreCaseAndIsActive(@Param("jobCodes") List<String> jobCodes, @Param("isActive") Boolean isActive);

    @Query("select j from JobEntity j where j.productionStageCode = :productionStageCode and j.isActive = true ")
    List<JobEntity> getJobEntitiesByProductionStageCode(@Param("productionStageCode") String productionStageCode);
}
