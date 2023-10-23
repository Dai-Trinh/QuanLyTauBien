package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.VendorDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.mapper.VendorEntityMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import com.querydsl.jpa.impl.JPAQuery;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VendorService {

    private static final Logger logger = LoggerFactory.getLogger(VendorService.class);

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    VendorEntityMapper vendorEntityMapper;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    DTOMapper DTOMapper;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    BusinessLogService businessLogService;

    @Autowired
    ParamRepository paramRepository;

    @Autowired
    Citt1Repository citt1Repository;

    @Autowired
    CoittRepository coittRepository;

    public CommonResponse getVendorList(PageFilterInput<VendorDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        PageResponse<List<VendorEntity>> vendors = vendorRepository.getAllVendor(input, pageable);
        List<VendorEntity> list = vendors.getData();
        Map<Integer, VendorEntity> vendorMap = list.stream().collect(Collectors.toMap(VendorEntity::getId, Function.identity()));
        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.VENDOR,
            vendorMap.keySet()
        );
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));

        List<VendorDTO> resultList = new ArrayList<>();
        for (VendorEntity vendorEntity : vendors.getData()) {
            resultList.add(DTOMapper.toVendorDTO(vendorEntity, propertyMap.get(vendorEntity.getId())));
        }

        return new PageResponse<List<VendorDTO>>(vendors.getDataCount()).success().data(resultList);
    }

    public CommonResponse createVendor(VendorDTO dto) {
        VendorEntity vendorEntity = vendorRepository.findByVendorCodeIgnoreCaseAndIsActive(dto.getVendorCode(), true);
        if (vendorEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", dto.getVendorCode());
        vendorEntity = vendorEntityMapper.toEntity(dto);
        vendorRepository.save(vendorEntity);
        businessLogService.insertInsertionLog(
            vendorEntity.getId(),
            Contants.EntityType.VENDOR,
            DTOMapper.toLogDetail(vendorEntity, dto.getVendorMap())
        );
        if (dto.getVendorMap().isEmpty()) return new CommonResponse<>().success("Thêm mới thành công");
        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
            vendorEntity.getId(),
            dto.getVendorMap(),
            Contants.EntityType.VENDOR
        );
        keyValueV2Repository.saveAll(keyValueEntities);
        return new CommonResponse<>().success("Thêm mới thành công");
    }

    public CommonResponse updateVendor(VendorDTO vendorDTO, String vendorCode) {
        VendorEntity vendorEntity = vendorRepository.findByVendorCodeIgnoreCaseAndIsActive(vendorCode, true);
        if (vendorEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        VendorEntity oldValue = new VendorEntity(vendorEntity);
        vendorEntityMapper.updateFromDTO(vendorEntity, vendorDTO);
        VendorEntity savedEntity = vendorRepository.save(vendorEntity);
        BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
            savedEntity.getId(),
            Contants.EntityType.VENDOR,
            DTOMapper.toUpdateLogDetail(oldValue, vendorEntity)
        );

        if (vendorDTO.getVendorMap().isEmpty()) return new CommonResponse<>().success("Cập nhật thành công");
        keyValueService.createUpdateKeyValueOfEntityWithLog(
            vendorEntity.getId(),
            vendorDTO.getVendorMap(),
            Contants.EntityType.VENDOR,
            true,
            logEntity
        );
        return new CommonResponse<>().success("Cập nhật thành công: " + vendorCode);
    }

    public CommonResponse deleteVendor(String vendorCode) {
        VendorEntity vendorEntity = vendorRepository.findByVendorCodeIgnoreCaseAndIsActive(vendorCode, true);
        if (vendorEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        vendorEntity.setActive(false);
        vendorRepository.save(vendorEntity);
        businessLogService.insertDeleteLog(vendorEntity.getId(), Contants.EntityType.VENDOR, DTOMapper.toDeletionLogDetail(vendorEntity));
        return new CommonResponse<>().success("Đã xóa NCC: " + vendorCode);
    }

    @Transactional
    public CommonResponse importInfo(MultipartFile file) throws IOException, ParseException {
        List<VendorDTO> list = xlsxExcelHandle.readVendorInfo(file.getInputStream());
        for (VendorDTO v : list) {
            VendorEntity vendorEntity = vendorRepository.findByVendorCodeIgnoreCaseAndIsActive(v.getVendorCode(), true);
            if (vendorEntity != null) {
                throw new CustomException(HttpStatus.CONFLICT, "duplicate", vendorEntity.getVendorCode());
            } else {
                if (v.getVendorCode() == null || v.getVendorCode().isEmpty()) continue;

                vendorEntity = vendorEntityMapper.toEntity(v);
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    vendorEntity.getId(),
                    v.getVendorMap(),
                    Contants.EntityType.VENDOR
                );
            }
        }
        Map<Integer, List<BusinessLogDetailEntity>> businessLogMap = new HashMap<>();
        for (VendorDTO v : list) {
            VendorEntity vendorEntity = vendorRepository.findByVendorCodeIgnoreCaseAndIsActive(v.getVendorCode(), true);
            if (vendorEntity == null) {
                if (v.getVendorCode() == null || v.getVendorCode().isEmpty()) continue;

                vendorEntity = vendorEntityMapper.toEntity(v);
                if (!paramRepository.existsAllByParamCodeAndParamValue("DVTT", v.getCurrency())) {
                    Param param = new Param();
                    param.setParamCode("DVTT");
                    param.setParamValue(v.getCurrency());
                    param.setParamDesc(v.getCurrency());
                    paramRepository.save(param);
                }
                vendorRepository.save(vendorEntity);
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    vendorEntity.getId(),
                    v.getVendorMap(),
                    Contants.EntityType.VENDOR
                );
                businessLogMap.put(vendorEntity.getId(), DTOMapper.toLogDetail(vendorEntity, keyValueEntities));
                keyValueV2Repository.saveAll(keyValueEntities);
            }
        }
        businessLogService.insertInsertionLogByBatch(Contants.EntityType.VENDOR, businessLogMap);
        return new CommonResponse().success();
    }

    public List<String> autocompleteCommon(PageFilterInput<VendorDTO> input) {
        Set<String> listAuto = new LinkedHashSet<>();
        String common = input.getCommon();
        Pageable pageable = Pageable.unpaged();
        //Get list object that it had the search.
        List<VendorEntity> vendorEntities = vendorRepository.getAllVendor(input, pageable).getData();
        //Compare to filter to get the list result
        if (vendorEntities != null && !vendorEntities.isEmpty()) {
            vendorEntities.forEach(item -> {
                List<KeyValueEntityV2> list = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    Contants.EntityType.VENDOR,
                    new ArrayList<>(Arrays.asList(item.getId()))
                );
                if (
                    item.getVendorCode() != null &&
                    !item.getVendorCode().isEmpty() &&
                    item.getVendorCode().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getVendorCode());
                if (
                    item.getVendorName() != null &&
                    !item.getVendorName().isEmpty() &&
                    item.getVendorName().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getVendorName());
                if (
                    item.getOtherName() != null &&
                    !item.getOtherName().isEmpty() &&
                    item.getOtherName().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getOtherName());
                if (
                    item.getAddress() != null &&
                    !item.getAddress().isEmpty() &&
                    item.getAddress().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getAddress());
                if (
                    item.getEmail() != null && !item.getEmail().isEmpty() && item.getEmail().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getEmail());
                if (
                    item.getPhone() != null && !item.getPhone().isEmpty() && item.getPhone().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getPhone());
                if (
                    item.getCurrency() != null &&
                    !item.getCurrency().isEmpty() &&
                    item.getCurrency().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getCurrency());
                if (
                    item.getFaxCode() != null &&
                    !item.getFaxCode().isEmpty() &&
                    item.getFaxCode().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getFaxCode());
                if (
                    item.getTaxCode() != null &&
                    !item.getTaxCode().isEmpty() &&
                    item.getTaxCode().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getTaxCode());
                if (
                    item.getContactId() != null &&
                    !item.getContactId().isEmpty() &&
                    item.getContactId().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactId());
                if (
                    item.getContactName() != null &&
                    !item.getContactName().isEmpty() &&
                    item.getContactName().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactName());
                if (
                    item.getContactAddress() != null &&
                    !item.getContactAddress().isEmpty() &&
                    item.getContactAddress().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactAddress());
                if (
                    item.getContactPhone() != null &&
                    !item.getContactPhone().isEmpty() &&
                    item.getContactPhone().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactPhone());
                if (
                    item.getContactEmail() != null &&
                    !item.getContactEmail().isEmpty() &&
                    item.getContactEmail().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactEmail());
                if (
                    item.getContactTitle() != null &&
                    !item.getContactTitle().isEmpty() &&
                    item.getContactTitle().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactTitle());
                if (
                    item.getContactPosition() != null &&
                    !item.getContactPosition().isEmpty() &&
                    item.getContactPosition().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactPosition());
                if (
                    item.getContactGender() != null &&
                    !item.getContactGender().isEmpty() &&
                    item.getContactGender().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactGender());
                if (
                    item.getContactBirthDate() != null &&
                    !item.getContactBirthDate().toString().isEmpty() &&
                    item.getContactBirthDate().toString().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getContactBirthDate().toString());
                for (KeyValueEntityV2 k : list) {
                    if (k.getCommonValue().toLowerCase().contains(common.toLowerCase())) listAuto.add(k.getCommonValue());
                }
            });
        }
        List<String> resultList = new ArrayList<>(listAuto);
        resultList = resultList.subList(0, Math.min(input.getPageSize(), resultList.size()));
        return resultList;
    }

    public PageResponse<List<CoittDTO>> getAllItemForVendor() {
        List<CoittDTO> citt1EntityList = citt1Repository.getAllCittGroupBy();
        List<CoittDTO> coittEntityList = coittRepository.getAllGroupBy();
        List<CoittDTO> result = new ArrayList<>(coittEntityList);

        for (CoittDTO coittDTO : citt1EntityList) {
            result.add(coittDTO);
        }
        //        for (CoittEntity coittEntity : coittEntityList){
        //            result.add(DTOMapper.toCoittDTO(coittEntity, null));
        //        }

        return new PageResponse<List<CoittDTO>>().success().data(result).dataCount(result.size());
    }
}
