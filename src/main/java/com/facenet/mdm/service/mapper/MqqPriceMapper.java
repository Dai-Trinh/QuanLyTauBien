package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.MqqPriceEntity;
import com.facenet.mdm.service.dto.MqqPriceDTO;
import com.facenet.mdm.service.dto.PriceListDTO;
import com.facenet.mdm.service.dto.PromotionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MqqPriceMapper {

    @Mapping(target = "minQuantity", source = "entity.rangeStart")
    @Mapping(target = "maxQuantity", source = "entity.rangeEnd")
    @Mapping(target = "priceMQQ", source = "entity.price")
    @Mapping(target = "note", source = "entity.note")
    @Mapping(target = "dueDate", source = "entity.timeEnd")
    @Mapping(target = "transactionMoney", source = "entity.currency")
    @Mapping(target = "mqqPriceId", source = "entity.id")
    MqqPriceDTO EntityToDTO (MqqPriceEntity entity);

    @Mapping(target = "id", source = "dto.mqqPriceId")
    @Mapping(target = "rangeStart", source = "dto.minQuantity")
    @Mapping(target = "rangeEnd", source = "dto.maxQuantity")
    @Mapping(target = "price", source = "dto.priceMQQ")
    @Mapping(target = "note", source = "dto.note")
    @Mapping(target = "timeEnd", source = "dueDate")
    @Mapping(target = "currency", source = "dto.transactionMoney")
    @Mapping(target = "vendorCode", source = "vendorCode")
    @Mapping(target = "itemCode", source = "itemCode")
    @Mapping(target = "timeStart", source = "dto.startDate")
    MqqPriceEntity DTOToEntity (MqqPriceDTO dto, String vendorCode, String itemCode, Date dueDate);

    @Mapping(target = "vendorCode", source = "vendorCode")
    @Mapping(target = "itemCode", source = "itemCode")
    @Mapping(target = "rangeStart", source = "priceListDTO.minQuantity")
    @Mapping(target = "rangeEnd", source = "priceListDTO.maxQuantity")
    @Mapping(target = "timeStart", source = "dto.startDate")
    @Mapping(target = "timeEnd", source = "dto.endDate")
    @Mapping(target = "note", source = "dto.note")
    @Mapping(target = "price", source = "priceListDTO.unitPrice")
    @Mapping(target = "currency", source = "priceListDTO.currency")
    MqqPriceEntity dtoToEntity(String vendorCode, String itemCode, PromotionDTO dto, PriceListDTO priceListDTO);
}
