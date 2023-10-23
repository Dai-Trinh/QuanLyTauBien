package com.facenet.mdm.repository;

import com.facenet.mdm.domain.Citt1Entity;
import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.repository.custom.Citt1CustomRepository;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.DataToFillBom;
import java.util.List;
import java.util.StringTokenizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Citt1Repository extends JpaRepository<Citt1Entity, Integer>, Citt1CustomRepository {
    Citt1Entity findByProductCodeIgnoreCaseAndMaterialCodeIgnoreCaseAndIsActive(String productCode, String materialCode, Boolean isActive);
    Citt1Entity findByProductCodeIgnoreCaseAndIsActive(String productCode, Boolean isActive);

    @Query(
        "select new com.facenet.mdm.service.dto.CoittDTO(c.materialCode, c.proName, c.techName, c.itemGroupCode, c.unit, c.version, c.note, c.notice, c.isTemplate, c.quantity, c.status, c.kind, c.wareHouse) from Citt1Entity c where c.isActive = true group by c.materialCode"
    )
    List<CoittDTO> getAllCittGroupBy();

    @Query("select c from Citt1Entity c where c.isActive = true and c.materialCode = :materialCode group by c.materialCode")
    Citt1Entity findCitt1EntitiesByCode(@Param("materialCode") String materialCode);

    @Query("select c from Citt1Entity c where c.isActive = true and c.merchandiseGroupEntity.merchandiseGroupCode = :merchandiseGroupCode")
    List<Citt1Entity> findCitt1EntitiesByMerchandiseGroupCode(@Param("merchandiseGroupCode") String merchandiseGroupCode);

    @Query("select c.materialCode from Citt1Entity c where c.isActive = true and c.coittEntity.id = :id")
    List<String> getMaterialCodeById(@Param("id") Integer id);

    @Query("select c from Citt1Entity c where c.isActive = true and c.materialCode in :materialCode and c.coittEntity is null")
    List<Citt1Entity> getCitt1EntitiesByCode(@Param("materialCode") List<String> materialCode);

    @Query(
        "select c from Citt1Entity c where c.isActive = true and c.materialCode = :materialCode and c.coittEntity.productCode = :productCode and c.version = :version"
    )
    Citt1Entity getCitt1EntitiesByBom(
        @Param("materialCode") String materialCode,
        @Param("productCode") String productCode,
        @Param("version") String version
    );

    @Query("select c from Citt1Entity c where c.isActive = true and c.materialCode = :materialCode and c.coittEntity is null")
    Citt1Entity getCitt1EntitiesByCodeBOM(@Param("materialCode") String materialCode);

    @Query(
        "select new com.facenet.mdm.service.dto.DataToFillBom(c.materialCode, c.proName, c.techName, c.version, c.itemGroupCode" +
        " ,c.productionNorm, c.unit, c.wareHouse, c.materialReplaceCode) from Citt1Entity c where c.isActive = true and c.coittEntity is null " +
        "" +
        " order by c.createdAt desc"
    )
    List<DataToFillBom> getCitt1EntitiesByProductCodesToFillBom();

    @Query(
        "select new com.facenet.mdm.service.dto.DataToFillBom(c.materialCode, c.proName, c.techName, c.version, c.itemGroupCode" +
        " ,c.productionNorm, c.unit, c.wareHouse, c.materialReplaceCode) from Citt1Entity c where c.isActive = true and c.coittEntity is null and (c.materialCode in (:productCodes)) " +
        "" +
        " order by c.createdAt desc"
    )
    List<DataToFillBom> getCitt1EntitiesByProductCodesToFillBomByProductCodes(@Param("productCodes") List<String> productCodes);

    @Query(
        "select new com.facenet.mdm.service.dto.DataToFillBom(c.materialCode, c.proName, c.techName, c.version, c.itemGroupCode" +
        " ,c.quantity, c.unit, c.wareHouse, c.materialReplaceCode, c.vendor) from Citt1Entity c where c.isActive = true and c.coittEntity.productCode = :productCodes " +
        "" +
        " order by c.createdAt desc"
    )
    List<DataToFillBom> getCitt1EntitiesByBomParentCode(@Param("productCodes") String productCodes);

    @Query("select c.proName from Citt1Entity c where c.coittEntity is null and c.materialCode in :materialCode")
    List<String> getMaterialName(@Param("materialCode") List<String> materialCode);

    @Query("select c from Citt1Entity c where c.isActive = true and c.coittEntity.productCode = :productCode")
    List<Citt1Entity> getAllByCitt1ByParent(@Param("productCode") String productCode);
}
