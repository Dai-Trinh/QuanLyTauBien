package com.facenet.mdm.repository;

import com.facenet.mdm.domain.MaterialReplacementEntity;
import com.facenet.mdm.domain.VendorItemEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialReplacementRepository extends JpaRepository<MaterialReplacementEntity, String> {
    MaterialReplacementEntity findByMaterialCodeAndMaterialReplacementCode(String materialCode, String materialReplacementCode);
    Page<MaterialReplacementEntity> findByMaterialCodeIgnoreCase(String materialCode, Pageable pageable);

    @Query("select m.materialReplacementCode from MaterialReplacementEntity m where m.materialCode = :materialCode")
    List<String> getMaterialReplacementEntitiesByCode(@Param("materialCode") String materialCode);
}
