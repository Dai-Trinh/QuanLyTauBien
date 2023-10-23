package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.repository.custom.ProductionStageCustomRepository;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionStageRepository extends JpaRepository<ProductionStageEntity, Integer>, ProductionStageCustomRepository {
    @Query(
        "select p from ProductionStageEntity p " +
        "where p.isActive = true and " +
        "(:stageCode is null or p.productionStageCode like %:stageCode%) and " +
        "(:stageName is null or p.productionStageName like %:stageName%) and " +
        "(:status is null or p.status = :status)"
    )
    Page<ProductionStageEntity> getAll(
        @Param("stageCode") String stageCode,
        @Param("stageName") String stageName,
        @Param("status") Integer status,
        Pageable pageable
    );

    ProductionStageEntity findByProductionStageCodeIgnoreCaseAndIsActive(String productionStageCode, Boolean isActive);
    Boolean existsAllByProductionStageCodeAndIsActive(String productionStageCode, Boolean isActive);

    @Query("select p from ProductionStageEntity p where p.productionStageCode = :productionStageCode and p.isActive = true ")
    ProductionStageEntity findProductionStageEntitiesByCode(@Param("productionStageCode") String productionStageCode);

    @Query("select p from ProductionStageEntity p where p.isActive = true order by p.createdAt desc")
    List<ProductionStageEntity> getAllProductionStage();
}
