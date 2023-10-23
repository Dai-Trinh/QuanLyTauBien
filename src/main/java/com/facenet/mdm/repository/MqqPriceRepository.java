package com.facenet.mdm.repository;

import com.facenet.mdm.domain.LeadTimeEntity;
import com.facenet.mdm.domain.MqqPriceEntity;
import com.facenet.mdm.repository.custom.MqqPriceCustomRepository;
import com.facenet.mdm.service.dto.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MqqPriceRepository extends PagingAndSortingRepository<MqqPriceEntity, Integer>, MqqPriceCustomRepository {
    @Query(
        "select new com.facenet.mdm.service.dto.MinMqqPriceLeadTimeDTO(lt.leadTime, min(mq.price), mq.rangeStart, mq.rangeEnd, mq.timeEnd, mq.currency) from LeadTimeEntity lt, MqqPriceEntity mq " +
        "where lt.vendorCode = mq.vendorCode " +
        "and lt.itemCode = mq.itemCode " +
        "and mq.vendorCode = :vendorCode " +
        "and mq.itemCode = :itemCode " +
        "and mq.isPromotion = false " +
        "and lt.isActive = 1 and mq.isActive = 1 "
    )
    MinMqqPriceLeadTimeDTO findMqqAndLeadTimeWithMinPrice(@Param("vendorCode") String vendorCode, @Param("itemCode") String itemCode);

    @Query(
        "select distinct new com.facenet.mdm.service.dto.PromotionDTO(p.timeStart, p.timeEnd, p.note) " +
        "from MqqPriceEntity p " +
        "where p.vendorCode = :vendorCode " +
        "and p.itemCode = :itemCode " +
        "and p.isPromotion = true " +
        "and p.isActive = 1 " +
        "and p.timeEnd >= current_date " +
        "and p.timeStart <= current_date " +
        "order by p.createdAt desc "
    )
    List<PromotionDTO> getAllTimeInPromotion(@Param("vendorCode") String vendorCode, @Param("itemCode") String itemCode);

    @Query(
        "select m.itemCode from MqqPriceEntity m " +
        "where m.itemCode in :items " +
        "and m.isPromotion = true " +
        "and m.isActive = 1 " +
        "and current_date <= m.timeStart and m.timeEnd <= current_date "
    )
    Set<String> getInPromotionItemsOfList(@Param("items") List<String> items);

    @Query(
        "select distinct new com.facenet.mdm.service.dto.PriceListDTO(p.rangeStart, p.rangeEnd, p.price, p.currency) " +
        "from MqqPriceEntity p " +
        "where p.vendorCode = :vendorCode " +
        "and p.itemCode = :itemCode " +
        "and p.isActive = 1 " +
        "and p.isPromotion = true " +
        "and p.timeEnd >= current_date " +
        "and p.timeStart = :timeStart " +
        "and p.timeEnd = :timeEnd " +
        "order by p.createdAt asc "
    )
    List<PriceListDTO> getAllPriceList(
        @Param("vendorCode") String vendorCode,
        @Param("itemCode") String itemCode,
        @Param("timeStart") Date timeStart,
        @Param("timeEnd") Date timeEnd
    );

    MqqPriceEntity findById(String id);

    //    @Query("select new com.facenet.mrp.service.dto.MoqDTO(mq.itemPriceId,mq.vendorCode,mq.itemCode, min(mq.price),lt.leadTime, mq.currency) from LeadTimeEntity lt join MqqPriceEntity mq on lt.itemCode = mq.itemCode " +
    //        "where mq.itemCode = :itemCode and lt.isActive = 1 and mq.isActive = 1 ")
    //    MoqDTO findMoqMin(@Param("itemCode")String itemCode);

    @Query(
        "select new com.facenet.mdm.service.dto.MoqDTO(mq.id,mq.vendorCode,mq.itemCode, mq.rangeStart, mq.rangeEnd,mq.price,lt.leadTime, mq.currency) " +
        "from LeadTimeEntity lt join MqqPriceEntity mq on lt.itemCode = mq.itemCode " +
        "where lt.isActive = 1 and mq.isActive = 1 " +
        "and (mq.timeEnd >= current_date or mq.timeEnd is null) " +
        "and (mq.timeStart is null or mq.timeStart <= current_date) "
    )
    List<MoqDTO> findMoqMinAndLeadTime();

    @Query(
        "select new com.facenet.mdm.service.dto.MoqDTO(mq.id,mq.vendorCode,mq.itemCode, mq.rangeStart, mq.rangeEnd,mq.price,lt.leadTime, mq.currency) " +
        "from LeadTimeEntity lt join MqqPriceEntity mq on lt.itemCode = mq.itemCode " +
        "where lt.isActive = 1 and mq.isActive = 1 " +
        "and mq.itemCode in :listItem " +
        "and (mq.timeEnd >= current_date or mq.timeEnd is null) " +
        "and (mq.timeStart is null or mq.timeStart <= current_date) "
    )
    List<MoqDTO> findMoqMinAndLeadTimeByItemCode(@Param("listItem") List<String> listItem);

    @Transactional
    @Modifying
    @Query(
        "update MqqPriceEntity q set q.checkNew = false where q.itemCode = :itemCode and q.checkNew = true and q.vendorCode = :vendorCode"
    )
    Integer updateNewestMoq(@Param("itemCode") String itemCode, @Param("vendorCode") String vendorCode);

    @Query(
        value = "select new com.facenet.mdm.service.dto.InventorySupplierDTO(m.vendorCode, m.price, m.currency) from MqqPriceEntity m where m.itemCode = :itemCode and m.isActive = 1 and m.isPromotion = false "
    )
    List<InventorySupplierDTO> getAllVendorAndPriceByItemCode(@Param("itemCode") String itemCode);

    //    @Query("select distinct new com.facenet.mdm.service.dto.MqqPriceDTO(m.id, m.rangeStart, m.rangeEnd, m.price, m.currency, m.timeEnd, m.note)" +
    //        " from MqqPriceEntity m " +
    //        "where m.vendorCode = :vendorCode " +
    //        "and m.itemCode = :itemCode " +
    //        "and m.isActive = 1 " +
    //        "and m.isPromotion is false")
    //    List<MqqPriceDTO> findMqqDTOByVendorCodeAndItemCode(@Param("vendorCode")String vendorCode, @Param("itemCode")String itemCode);

    @Query("select l from LeadTimeEntity l " + "where l.vendorCode = :vendorCode " + "and l.itemCode = :itemCode " + "and l.isActive = 1")
    LeadTimeEntity findLeadtimeByVendorCodeAndItemCode(@Param("vendorCode") String vendorCode, @Param("itemCode") String itemCode);

    @Query(
        "select new com.facenet.mdm.service.dto.MoqDTO(mq.id,mq.vendorCode,mq.itemCode, mq.rangeStart, mq.rangeEnd,mq.price,lt.leadTime, mq.currency,mq.timeEnd,mq.note) " +
        "from LeadTimeEntity lt join MqqPriceEntity mq on lt.itemCode = mq.itemCode and lt.vendorCode = mq.vendorCode " +
        "where lt.isActive = 1 and mq.isActive = 1 " +
        "and mq.itemCode = ?2 " +
        "and mq.vendorCode = ?1 " +
        "and (mq.timeEnd >= current_date or mq.timeEnd is null) " +
        "and ((mq.timeStart = (SELECT MAX(timeStart) FROM MqqPriceEntity WHERE CAST(timeStart as date)  <= CURDATE() and vendorCode = mq.vendorCode and itemCode = mq.itemCode)))"
    )
    List<MoqDTO> findMoqMinAndLeadTimeByItemCodeAndVendorCode(String vendorCode, String itemCode);

    @Query(
        "select new com.facenet.mdm.service.dto.MoqDTO(mq.id,mq.vendorCode,mq.itemCode, mq.rangeStart, mq.rangeEnd,mq.price,lt.leadTime, mq.currency,mq.timeStart,mq.note) " +
        "from LeadTimeEntity lt join MqqPriceEntity mq on lt.itemCode = mq.itemCode and lt.vendorCode = mq.vendorCode " +
        "where lt.isActive = 1 and mq.isActive = 1 " +
        "and mq.itemCode = ?2 " +
        "and mq.vendorCode = ?1 " +
        "and (mq.timeEnd >= now() or mq.timeEnd is null) " +
        "and (mq.timeStart <= now() or mq.timeStart is null)" +
        "order by mq.timeStart desc "
    )
    List<MoqDTO> findMoqMinAndLeadTimeByItemCodeAndVendorCodeV2(String vendorCode, String itemCode);
}
