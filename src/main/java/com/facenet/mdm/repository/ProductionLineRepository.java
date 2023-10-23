package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ProductionLineEntity;
import com.facenet.mdm.service.dto.ProductionLineDTO;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionLineRepository extends JpaRepository<ProductionLineEntity, Long> {
    @Query(
        "select p from ProductionLineEntity p " + "where p.isActive = true  "
        //        "(:productionLineCode is null or p.productionLineCode like %:productionLineCode%) and " +
        //        "(:productionLineName is null or p.productionLineName like %:productionLineName%) and " +
        //        "(:productionLineType is null or p.productionLineType like %:productionLineType%)"
    )
    Page<ProductionLineEntity> getAll(
        //        @Param("productionLineCode") String productionLineCode,
        //        @Param("productionLineName") String productionLineName,
        //        @Param("productionLineType") String productionLineType,
        Pageable pageable
    );

    ProductionLineEntity findByProductionLineCodeAndIsActiveTrue(String productionLineCode);

    @Query(
        "select p.productionLineCode from ProductionLineEntity p where p.isActive = true and p.productionLineCode in :productionLineCode"
    )
    Set<String> getAllProductionLineCodeIn(@Param("productionLineCode") Collection<String> productionLineCode);
}
