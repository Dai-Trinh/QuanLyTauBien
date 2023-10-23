package com.facenet.mdm.repository;

import com.facenet.mdm.domain.VendorItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VendorItemRepository extends JpaRepository<VendorItemEntity, String> {
    Boolean existsAllByVendorCodeAndItemCode(String vendorCode, String itemCode);
    VendorItemEntity findByVendorCodeAndItemCode(String vendorCode, String itemCode);
    @Query("select i.itemCode from VendorItemEntity i where i.vendorCode = :vendorCode")
    List<String> getAllItemByVendorCode(@Param("vendorCode") String vendorCode);
    @Query("select v from VendorItemEntity v where v.vendorCode in :vendors")
    List<VendorItemEntity> findAllItemOfVendorList(@Param("vendors") Set<String> vendors);
}
