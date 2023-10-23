package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ProductionLineTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionLineTypeRepository extends JpaRepository<ProductionLineTypeEntity, Integer> {
    ProductionLineTypeEntity findByProductionLineTypeNameIgnoreCase(String productionLineTypeName);
}
