package com.facenet.mdm.repository;

import com.facenet.mdm.domain.CoittEntity;
import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.repository.custom.CoittCustomRepository;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.DataToFillBom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CoittRepository extends JpaRepository<CoittEntity, Integer>, CoittCustomRepository {
    CoittEntity findByProductCodeIgnoreCaseAndIsActive(String productCode, Boolean isActive);
    Boolean existsAllByProductCodeIgnoreCaseAndIsActive(String productCode, Boolean isActive);
    List<CoittEntity> getAllByProductCodeIn(Collection<String> itemList);

    default Map<String, CoittEntity> getAllByItemCodeInMap(Collection<String> itemList) {
        return getAllByProductCodeIn(itemList).stream().collect(Collectors.toMap(CoittEntity::getProductCode, Function.identity()));
    }

    @Query(
        "select p from CoittEntity p where p.isActive = true and p.merchandiseGroupEntity.merchandiseGroupCode = :merchandiseGroupCode order by p.createdAt"
    )
    List<CoittEntity> getCoittEntitiesByMerchandiseGroupCode(@Param("merchandiseGroupCode") String merchandiseGroupCode);

    @Query(
        "select new com.facenet.mdm.service.dto.CoittDTO(c.productCode, c.proName, c.techName, c.unit, c.version, c.note) from CoittEntity c where c.merchandiseGroupEntity.merchandiseGroupCode = :merchandiseGroupCode and c.isActive = true "
    )
    List<CoittDTO> getCoittDTOByMerchadiseGroup(@Param("merchandiseGroupCode") String merchandiseGroupCode);

    @Query("select c from CoittEntity c where c.isActive = true and c.productCode in :productCode")
    List<CoittEntity> getCoittEntitiesInProductCode(@Param("productCode") List<String> productCode);

    @Query(
        "select new com.facenet.mdm.service.dto.DataToFillBom(c.productCode, c.proName, c.techName, c.version, c.itemGroupCode" +
        " ,c.unit, c.wareHouse) from CoittEntity c where c.isActive = true" +
        "" +
        " order by c.createdAt desc"
    )
    List<DataToFillBom> getCoittEntitiesByCodeToFill();

    @Query(
        "select new com.facenet.mdm.service.dto.DataToFillBom(c.productCode, c.proName, c.techName, c.version, c.itemGroupCode" +
        " ,c.unit, c.wareHouse) from CoittEntity c where c.isActive = true and (c.productCode in (:productCodes))" +
        "" +
        " order by c.createdAt desc "
    )
    List<DataToFillBom> getCoittEntitiesByCodeToFillByProductCodes(@Param("productCodes") List<String> productCodes);

    @Query(
        "select new com.facenet.mdm.service.dto.CoittDTO(c.productCode, c.proName, c.techName, c.itemGroupCode, c.unit, c.version, c.note, c.notice, c.isTemplate, c.quantity, c.status, c.kind, c.wareHouse) from CoittEntity c where c.isActive = true group by c.productCode"
    )
    List<CoittDTO> getAllGroupBy();
}
