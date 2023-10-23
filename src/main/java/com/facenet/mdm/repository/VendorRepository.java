package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.domain.VendorEntity;
import com.facenet.mdm.repository.custom.VendorCustomRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Integer>, VendorCustomRepository {
    @Query(
        "select v from VendorEntity v " +
        "where v.isActive = true and " +
        "(:vendorCode is null or v.vendorCode like %:vendorCode%) and " +
        "(:vendorName is null or v.vendorName like %:vendorName%) and " +
        "(:otherName is null or v.otherName like %:otherName%) and " +
        "(:email is null or v.email like %:email%) and " +
        "(:address is null or v.address like %:address%) and " +
        "(:status is null or v.status = :status)"
    )
    Page<VendorEntity> getAll(
        @Param("vendorCode") String vendorCode,
        @Param("vendorName") String vendorName,
        @Param("otherName") String otherName,
        @Param("email") String email,
        @Param("address") String address,
        @Param("status") Integer status,
        Pageable pageable
    );

    VendorEntity findByVendorCodeIgnoreCaseAndIsActive(String vendorCode, Boolean isActive);
    Boolean existsAllByVendorCodeIgnoreCaseAndIsActive(String vendorCode, Boolean isActive);
    List<VendorEntity> getAllByVendorCodeIn(Collection<String> itemList);

    default Map<String, VendorEntity> getAllByVendorCodeInMap(Collection<String> itemList) {
        return getAllByVendorCodeIn(itemList).stream().collect(Collectors.toMap(VendorEntity::getVendorCode, Function.identity()));
    }

    @Query(
        "select v.vendorName from VendorEntity v inner join VendorItemEntity it on v.vendorCode = it.vendorCode where it.itemCode = :productCode"
    )
    List<String> getVendorNameByProductCode(@Param("productCode") String productCode);
}
