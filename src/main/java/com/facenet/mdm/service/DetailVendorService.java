package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.model.MqqPriceExcelModel;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DetailVendorService {

    private static final Logger logger = LoggerFactory.getLogger(DetailVendorService.class);

    @Autowired
    VendorItemRepository vendorItemRepository;

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    LeadTimeRepository leadTimeRepository;

    @Autowired
    MqqPriceRepository mqqPriceRepository;

    @Autowired
    RateExchangeService rateExchangeService;

    @Autowired
    CoittRepository coittRepository;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    DTOMapper dtoMapper;

    public CommonResponse addItemForVendor(String vendorCode, String itemCode) {
        if (!vendorRepository.existsAllByVendorCodeIgnoreCaseAndIsActive(vendorCode, true)) throw new CustomException(
            HttpStatus.NOT_FOUND,
            "not.found",
            vendorCode
        );
        if (!coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(itemCode, true)) throw new CustomException(
            HttpStatus.NOT_FOUND,
            "not.found",
            itemCode
        );
        if (!vendorItemRepository.existsAllByVendorCodeAndItemCode(vendorCode, itemCode)) {
            VendorItemEntity vendorItemEntity = new VendorItemEntity();
            vendorItemEntity.setVendorCode(vendorCode);
            vendorItemEntity.setItemCode(itemCode);
            vendorItemRepository.save(vendorItemEntity);
        } else throw new CustomException(HttpStatus.CONFLICT, "duplicate.product.material.code", itemCode, vendorCode);

        return new CommonResponse().success();
    }

    public CommonResponse removeItem(String vendorCode, String itemCode) {
        if (!vendorRepository.existsAllByVendorCodeIgnoreCaseAndIsActive(vendorCode, true)) throw new CustomException(
            HttpStatus.NOT_FOUND,
            "not.found" + vendorCode
        );
        if (!coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(itemCode, true)) throw new CustomException(
            HttpStatus.NOT_FOUND,
            "not.found" + itemCode
        );
        if (vendorItemRepository.existsAllByVendorCodeAndItemCode(vendorCode, itemCode)) {
            VendorItemEntity vendorItemEntity = vendorItemRepository.findByVendorCodeAndItemCode(vendorCode, itemCode);
            vendorItemRepository.delete(vendorItemEntity);
        } else throw new CustomException(HttpStatus.NOT_FOUND, "not.found", itemCode + "---" + vendorCode);
        return new CommonResponse().success();
    }

    //hàm lấy thông tin leadtime và mqq price theo item
    public PageResponse<List<DataItemInVendor>> getAllData(PageFilterInput<DataItemInVendor> input, String vendorCode)
        throws JsonProcessingException {
        long startTime = System.currentTimeMillis();

        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }

        List<DataItemInVendor> listData = new ArrayList<>();
        PageResponse<List<DataItemInVendor>> listItem = coittRepository.getAllItemAlongVendor(input, vendorCode, pageable);
        List<DataItemInVendor> list = listItem.getData();
        Map<String, Float> rateExchange = rateExchangeService.getRateExchange();
        for (DataItemInVendor item : list) {
            //            DataItemInVendor dataItemInVendor = new DataItemInVendor();
            //            CoittDTO coittDTO = getAllProperties(item.getProductCode());
            //            for (LeadTimeEntity leadTime : leadTimeEntities) {
            //                System.out.println(item.getProductCode() + "---" + vendorCode);
            //                if (item.getProductCode().equals(leadTime.getItemCode())) {
            //                    MqqLeadTimeDTO mqqLeadTimeDTO = mqqPriceRepository.findMqqPriceAndLeadTime(vendorCode, item.getItemCode());
            //                    dataItemInVendor.setPriceMQQ(leadTime.getMqqPriceMin());
            //                    dataItemInVendor.setDueDate(mqqLeadTimeDTO.getDueDate());
            //                    dataItemInVendor.setCurrency(leadTime.getCurrency());
            //                    dataItemInVendor.setNote(mqqLeadTimeDTO.getNote());
            List<MoqDTO> moqDTOList = mqqPriceRepository.findMoqMinAndLeadTimeByItemCodeAndVendorCodeV2(vendorCode, item.getProductCode());

            LeadTimeEntity leadTimeEntity = leadTimeRepository.findByVendorCodeIgnoreCaseAndItemCodeIgnoreCaseAndIsActive(
                vendorCode,
                item.getProductCode(),
                (byte) 1
            );
            // xét giá trị ban đầu cho dataItemInVendor.priceMQQ để so sánh tìm exchange
            if (moqDTOList.size() > 0) {
                item.setLeadTime(moqDTOList.get(0).getLeadTime());
                item.setPriceMQQ(moqDTOList.get(0).getPrice());
                item.setCurrency(moqDTOList.get(0).getCurrency());
                item.setLeadTimeNote(moqDTOList.get(0).getNote());
                item.setDueDate(moqDTOList.get(0).getDueDate());
                if (moqDTOList.get(0).getDueDate() == null) {
                    for (MoqDTO m : moqDTOList) {
                        double exchange;
                        exchange = rateExchange.get(m.getCurrency()) * m.getPrice();
                        if (exchange < rateExchange.get(item.getCurrency()) * item.getPriceMQQ()) {
                            item.setLeadTime(m.getLeadTime());
                            item.setPriceMQQ(m.getPrice());
                            item.setCurrency(m.getCurrency());
                            item.setLeadTimeNote(m.getNote());
                            item.setDueDate(m.getDueDate());
                        }
                    }
                }
            }

            //                }
            //            }
            if (leadTimeEntity != null) {
                leadTimeEntity.setMoqPriceMin(item.getPriceMQQ());
                leadTimeEntity.setLeadTime(item.getLeadTime());
                leadTimeEntity.setNote(item.getLeadTimeNote());
                leadTimeEntity.setCurrency(item.getCurrency());
                leadTimeRepository.save(leadTimeEntity);
            }
            //            dataItemInVendor.setProductCode(item.getProductCode());
            //            dataItemInVendor.setProName(coittDTO.getProName());
            //            dataItemInVendor.setTechName(coittDTO.getTechName());
            //            dataItemInVendor.setItemGroupCode(coittDTO.getItemGroupCode());
            //            dataItemInVendor.setUnit(coittDTO.getUnit());
            //            dataItemInVendor.setVersion(coittDTO.getVersion());
            //            dataItemInVendor.setNote(coittDTO.getNote());
            //            dataItemInVendor.setNotice(coittDTO.getNotice());
            //            dataItemInVendor.setTemplate(coittDTO.getTemplate());
            //            dataItemInVendor.setQuantity(coittDTO.getQuantity());
            //            dataItemInVendor.setParent(coittDTO.getParent());
            //            dataItemInVendor.setStatus(coittDTO.getStatus());
            //            dataItemInVendor.setKind(coittDTO.getKind());
            //for (String s : coittDTO.getPropertiesMap().keySet()) dataItemInVendor.setPropertiesMap(s, coittDTO.getPropertiesMap().get(s));
            //                    dataItemInVendor.setGroupName(item.getGroupName());
            if (item.getItemGroupCode() == Contants.ItemGroup.BTP) {
                item.setGroupName("BTP");
            } else if (item.getItemGroupCode() == Contants.ItemGroup.NVL) {
                item.setGroupName("NVL");
            } else {
                item.setGroupName("TP");
            }
            //                    dataItemInVendor.setProductType("item.getType()");
            listData.add(item);
        }

        long endTime = System.currentTimeMillis();

        System.err.println("Thời gian thực thi: " + (endTime - startTime) + " ms");
        return new PageResponse<List<DataItemInVendor>>()
            .errorCode("00")
            .message("Thành công")
            .isOk(true)
            .dataCount(listItem.getDataCount())
            .data(listData);
    }

    public CoittDTO getAllProperties(String itemCode) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(itemCode, true);
        List<KeyValueEntityV2> properties = new ArrayList<>();
        List<KeyValueEntityV2> tpProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.TP,
            Collections.singleton(coittEntity.getId())
        );
        List<KeyValueEntityV2> btpProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.BTP,
            Collections.singleton(coittEntity.getId())
        );
        List<KeyValueEntityV2> nvlProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.NVL,
            Collections.singleton(coittEntity.getId())
        );
        for (KeyValueEntityV2 k : tpProperties) {
            properties.add(k);
        }
        for (KeyValueEntityV2 k : btpProperties) {
            properties.add(k);
        }
        for (KeyValueEntityV2 k : nvlProperties) {
            properties.add(k);
        }
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));
        CoittDTO coittDTO = dtoMapper.toCoittDTO(coittEntity, propertyMap.get(coittEntity.getId()));
        return coittDTO;
    }

    @Transactional
    public CommonResponse importItemList(MultipartFile file) throws IOException, ParseException {
        List<VendorItemDTO> list = xlsxExcelHandle.readVendorItem(file.getInputStream());
        for (VendorItemDTO v : list) {
            VendorItemEntity vendorItemEntity = vendorItemRepository.findByVendorCodeAndItemCode(v.getVendorCode(), v.getItemCode());
            if (vendorItemEntity == null) {
                VendorItemEntity vendorItem = new VendorItemEntity();
                vendorItem.setVendorCode(v.getVendorCode());
                vendorItem.setItemCode(v.getItemCode());
                vendorItemRepository.save(vendorItem);
            }
        }
        return new CommonResponse().success();
    }

    @Transactional
    public CommonResponse importPrice(MultipartFile file) throws IOException, ParseException {
        MqqPriceExcelModel model = xlsxExcelHandle.readPriceExcel(file.getInputStream());

        List<VendorItemEntity> vendorItemEntities = vendorItemRepository.findAllItemOfVendorList(model.getVendorCodes());
        validateItemVendor(model, buildItemVendorMap(vendorItemEntities));
        leadTimeRepository.saveAll(model.getLeadTimeEntities());
        mqqPriceRepository.saveAll(model.getMqqPriceEntities());

        return new CommonResponse().success();
    }

    private void validateItemVendor(MqqPriceExcelModel model, MultiValuedMap<String, String> itemVendorMap) {
        List<VendorItemEntity> newVendorItemMapping = new ArrayList<>();
        for (MqqPriceEntity entity : model.getMqqPriceEntities()) {
            if (!vendorRepository.existsAllByVendorCodeIgnoreCaseAndIsActive(entity.getVendorCode(), true)) {
                throw new CustomException(HttpStatus.NOT_FOUND, "not.found", entity.getVendorCode());
            }
            if (!coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(entity.getItemCode(), true)) {
                throw new CustomException(HttpStatus.NOT_FOUND, "not.found", entity.getItemCode());
            }
            if (!itemVendorMap.get(entity.getItemCode()).contains(entity.getVendorCode())) {
                //                throw new CustomException(HttpStatus.BAD_REQUEST, "item.does.not.belong.to.vendor", entity.getItemCode(), entity.getVendorCode());
                newVendorItemMapping.add(new VendorItemEntity(entity.getVendorCode(), entity.getItemCode()));
            }
        }
        if (!newVendorItemMapping.isEmpty()) vendorItemRepository.saveAll(newVendorItemMapping);
    }

    private MultiValuedMap<String, String> buildItemVendorMap(List<VendorItemEntity> vendorItemEntities) {
        MultiValuedMap<String, String> itemVendorMap = new HashSetValuedHashMap<>();
        for (VendorItemEntity entity : vendorItemEntities) {
            itemVendorMap.put(entity.getItemCode(), entity.getVendorCode());
        }
        return itemVendorMap;
    }
}
