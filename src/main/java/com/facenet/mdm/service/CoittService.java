package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.repository.CoittRepository;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.KeyValueRepository;
import com.facenet.mdm.repository.KeyValueV2Repository;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.VendorDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.CoittEntityMapper;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CoittService {

    @Autowired
    CoittRepository coittRepository;

    @Autowired
    CoittEntityMapper coittEntityMapper;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    DTOMapper DTOMapper;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    MerchandiseGroupRepository merchandiseGroupRepository;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    ParamService paramService;

    @Autowired
    BusinessLogService businessLogService;

    @Autowired
    Citt1Repository citt1Repository;

    @Autowired
    MaterialReplacementRepository materialReplacementRepository;

    public CommonResponse createProduct(CoittDTO coittDTO) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittDTO.getProductCode(), true);
        if (coittEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", coittDTO.getProductCode());
        if (coittDTO.getProductCode() == null) throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
        coittEntity = coittEntityMapper.toEntity(coittDTO);
        Integer entityType;
        if (Contants.ItemGroup.BTP == coittDTO.getItemGroupCode()) {
            entityType = Contants.EntityType.BTP;
        } else if (Contants.ItemGroup.NVL == coittDTO.getItemGroupCode()) {
            entityType = Contants.EntityType.NVL;
        } else if (Contants.ItemGroup.TP == coittDTO.getItemGroupCode()) {
            entityType = Contants.EntityType.TP;
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "unknow.item.group.code", String.valueOf(coittDTO.getItemGroupCode()));
        }

        MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(
            coittDTO.getMerchandiseGroup()
        );
        if (merchandiseGroupEntity != null) {
            coittEntity.setMerchandiseGroupEntity(merchandiseGroupEntity);
        }

        coittRepository.save(coittEntity);
        businessLogService.insertInsertionLog(
            coittEntity.getId(),
            entityType,
            DTOMapper.toLogDetail(coittEntity, coittDTO.getPropertiesMap())
        );
        if (coittDTO.getPropertiesMap().isEmpty()) return new CommonResponse<>().success("Thêm mới thành công");
        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
            coittEntity.getId(),
            coittDTO.getPropertiesMap(),
            entityType
        );
        keyValueV2Repository.saveAll(keyValueEntities);
        return new CommonResponse<>().success("Thêm mới thành công");
    }

    public CommonResponse updatedProduct(CoittDTO coittDTO, String productCode) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(productCode, true);
        if (coittEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        CoittEntity oldValue = new CoittEntity(coittEntity);
        coittEntityMapper.updateFromDTO(coittEntity, coittDTO);
        Integer entityType;
        if (Contants.ItemGroup.BTP == coittDTO.getItemGroupCode()) {
            entityType = Contants.EntityType.BTP;
        } else if (Contants.ItemGroup.NVL == coittDTO.getItemGroupCode()) {
            entityType = Contants.EntityType.NVL;
        } else if (Contants.ItemGroup.TP == coittDTO.getItemGroupCode()) {
            entityType = Contants.EntityType.TP;
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "unknow.item.group.code", String.valueOf(coittDTO.getItemGroupCode()));
        }

        MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(
            coittDTO.getMerchandiseGroup()
        );
        if (merchandiseGroupEntity != null) {
            coittEntity.setMerchandiseGroupEntity(merchandiseGroupEntity);
        }

        CoittEntity savedEntity = coittRepository.save(coittEntity);
        BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
            savedEntity.getId(),
            entityType,
            DTOMapper.toUpdateLogDetail(oldValue, coittEntity)
        );
        if (coittDTO.getPropertiesMap().isEmpty()) return new CommonResponse<>().success("Cập nhật thành công");
        keyValueService.createUpdateKeyValueOfEntityWithLog(coittEntity.getId(), coittDTO.getPropertiesMap(), entityType, true, logEntity);
        return new CommonResponse<>().success("Cập nhật thành công");
    }

    @Transactional
    public CommonResponse deleteProduct(String productCode) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(productCode, true);
        Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(productCode);
        if (coittEntity == null && citt1Entity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        if (citt1Entity != null) {
            citt1Entity.setActive(false);
            citt1Repository.save(citt1Entity);
            businessLogService.insertDeleteLog(citt1Entity.getId(), Contants.EntityType.NVL, DTOMapper.toDeletionLogDetail(citt1Entity));
            return new CommonResponse().success("Đã xóa " + productCode);
        }
        coittEntity.setActive(false);
        coittRepository.save(coittEntity);
        Integer entityType;
        if (Contants.ItemGroup.BTP == coittEntity.getItemGroupCode()) {
            entityType = Contants.EntityType.BTP;
        } else if (Contants.ItemGroup.NVL == coittEntity.getItemGroupCode()) {
            entityType = Contants.EntityType.NVL;
        } else if (Contants.ItemGroup.TP == coittEntity.getItemGroupCode()) {
            entityType = Contants.EntityType.TP;
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "unknow.item.group.code", String.valueOf(coittEntity.getItemGroupCode()));
        }
        businessLogService.insertDeleteLog(coittEntity.getId(), entityType, DTOMapper.toDeletionLogDetail(coittEntity));
        return new CommonResponse().success("Đã xóa " + productCode);
    }

    public CommonResponse getProductList(PageFilterInput<CoittDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        Map<Integer, List<KeyValueEntityV2>> propertyMap;

        long total = 0;
        List<CoittDTO> resultList = new ArrayList<>();
        if (input.getFilter().getItemGroupCode() == null || input.getFilter().getItemGroupCode() == Contants.ItemGroup.NVL) {
            Page<Citt1Entity> citt1Entities = citt1Repository.getAll(input, pageable);
            List<Citt1Entity> listCitt1Entity = citt1Entities.getContent();
            total += citt1Entities.getTotalElements();
            Map<Integer, Citt1Entity> mapCitt1Entity = listCitt1Entity
                .stream()
                .collect(Collectors.toMap(Citt1Entity::getId, Function.identity()));
            List<KeyValueEntityV2> nvlProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.NVL,
                mapCitt1Entity.keySet()
            );

            propertyMap = nvlProperties.stream().collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));

            for (Citt1Entity citt1Entity : listCitt1Entity) {
                resultList.add(DTOMapper.toCitt1DTO(citt1Entity, propertyMap.get(citt1Entity.getId())));
            }
            //return new PageResponse<List<CoittDTO>>(citt1Entities.getTotalElements()).success().data(resultList);
        }
        if (input.getFilter().getItemGroupCode() == null || input.getFilter().getItemGroupCode() != Contants.ItemGroup.NVL) {
            Page<CoittEntity> coittEntities = coittRepository.getAll(input, pageable);
            List<CoittEntity> listCoittEntity = coittEntities.getContent();
            Map<Integer, CoittEntity> mapCoittEntity = listCoittEntity
                .stream()
                .collect(Collectors.toMap(CoittEntity::getId, Function.identity()));

            List<KeyValueEntityV2> properties = new ArrayList<>();
            List<KeyValueEntityV2> tpProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.TP,
                mapCoittEntity.keySet()
            );
            List<KeyValueEntityV2> btpProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.BTP,
                mapCoittEntity.keySet()
            );
            //        List<KeyValueEntityV2> nvlProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            //            Contants.EntityType.NVL,
            //            mapCitt1Entity.keySet()
            //        );
            for (KeyValueEntityV2 k : tpProperties) {
                properties.add(k);
            }
            for (KeyValueEntityV2 k : btpProperties) {
                properties.add(k);
            }
            //        for (KeyValueEntityV2 k : nvlProperties) {
            //            properties.add(k);
            //        }
            propertyMap = properties.stream().collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));
            for (CoittEntity coittEntity : listCoittEntity) {
                resultList.add(DTOMapper.toCoittDTO(coittEntity, propertyMap.get(coittEntity.getId())));
            }
            total += coittEntities.getTotalElements();
        }

        return (PageResponse<List<CoittDTO>>) new PageResponse<List<CoittDTO>>(total).success().data(resultList);
    }

    public CommonResponse importProduct(MultipartFile file, Integer itemGroupCode) throws IOException, ParseException {
        List<CoittDTO> coittDTOS = xlsxExcelHandle.readProductFromExcel(file.getInputStream(), itemGroupCode);
        Integer entityType;
        if (Contants.ItemGroup.BTP == itemGroupCode) {
            entityType = Contants.EntityType.BTP;
        } else if (Contants.ItemGroup.NVL == itemGroupCode) {
            entityType = Contants.EntityType.NVL;
        } else if (Contants.ItemGroup.TP == itemGroupCode) {
            entityType = Contants.EntityType.TP;
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, "unknow.item.group.code", String.valueOf(itemGroupCode));
        }
        for (CoittDTO c : coittDTOS) {
            CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(c.getProductCode(), true);
            if (coittEntity != null) {
                throw new CustomException(HttpStatus.CONFLICT, "duplicate", c.getProductCode());
            }
            if (c.getProductCode() == null || c.getProductCode().isEmpty()) continue;
            CoittEntity coitt = coittEntityMapper.toEntity(c);
            List<KeyValueEntityV2> keyValues = keyValueService.createOrUpdateKeyValueEntity(
                coitt.getId(),
                c.getPropertiesMap(),
                entityType
            );
        }
        Map<Integer, List<BusinessLogDetailEntity>> businessLogMap = new HashMap<>();
        List<ParamDto> paramDtoList = new ArrayList<>();
        for (CoittDTO c : coittDTOS) {
            CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(c.getProductCode(), true);
            if (coittEntity == null) {
                if (c.getProductCode() == null || c.getProductCode().isEmpty()) continue;
                coittEntity = coittEntityMapper.toEntity(c);
                coittRepository.save(coittEntity);
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    coittEntity.getId(),
                    c.getPropertiesMap(),
                    entityType
                );
                businessLogMap.put(coittEntity.getId(), DTOMapper.toLogDetail(coittEntity, keyValueEntities));
                keyValueV2Repository.saveAll(keyValueEntities);

                if (coittEntity.getUnit() != null) {
                    ParamDto paramDto = new ParamDto();
                    paramDto.setParamValue(coittEntity.getUnit());
                    paramDto.setParamDesc(coittEntity.getUnit());
                    paramDtoList.add(paramDto);
                }
            }
        }
        businessLogService.insertInsertionLogByBatch(entityType, businessLogMap);
        if (paramDtoList != null) {
            CommonResponse commonResponse = paramService.addParams("DVT", paramDtoList);
        }
        return new CommonResponse<>().success();
    }

    public List<String> autocompleteCommon(PageFilterInput<CoittDTO> input) {
        Set<String> listAuto = new LinkedHashSet<>();
        String common = input.getCommon();
        Pageable pageable = Pageable.unpaged();
        Integer entityType;
        if (input.getFilter().getItemGroupCode() == Contants.ItemGroup.NVL) {
            entityType = Contants.EntityType.NVL;
        } else if (input.getFilter().getItemGroupCode() == Contants.ItemGroup.BTP) {
            entityType = Contants.EntityType.BTP;
        } else if (input.getFilter().getItemGroupCode() == Contants.ItemGroup.TP) {
            entityType = Contants.EntityType.TP;
        } else {
            entityType = 0;
            return null;
        }
        //Get list object that it had the search.
        List<CoittEntity> coittEntities = coittRepository.getAll(input, pageable).getContent();
        //Compare to filter to get the list result
        if (coittEntities != null && !coittEntities.isEmpty()) {
            coittEntities.forEach(item -> {
                List<KeyValueEntityV2> list = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    entityType,
                    new ArrayList<>(Arrays.asList(item.getId()))
                );
                if (
                    item.getProductCode() != null &&
                    !item.getProductCode().isEmpty() &&
                    item.getProductCode().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getProductCode());
                if (
                    item.getProName() != null &&
                    !item.getProName().isEmpty() &&
                    item.getProName().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getProName());
                if (
                    item.getTechName() != null &&
                    !item.getTechName().isEmpty() &&
                    item.getTechName().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getTechName());
                if (
                    item.getNote() != null && !item.getNote().isEmpty() && item.getNote().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getNote());
                if (
                    item.getUnit() != null && !item.getUnit().isEmpty() && item.getUnit().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getUnit());
                if (
                    item.getVersion() != null &&
                    !item.getVersion().isEmpty() &&
                    item.getVersion().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getVersion());
                if (
                    item.getNotice() != null && !item.getNotice().isEmpty() && item.getNotice().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getNotice());
                //                if (item.getItemGroupCode().toString().toLowerCase().contains(common.toLowerCase())) listAuto.add(item.getItemGroupCode().toString());
                //                if (item.getParent().toString().toLowerCase().contains(common.toLowerCase())) listAuto.add(item.getParent().toString());
                //                if (item.getTemplate().toString().toLowerCase().contains(common.toLowerCase())) listAuto.add(item.getTemplate().toString());
                for (KeyValueEntityV2 k : list) {
                    if (k.getCommonValue().toLowerCase().contains(common.toLowerCase())) listAuto.add(k.getCommonValue());
                }
            });
        }
        List<String> resultList = new ArrayList<>(listAuto);
        resultList = resultList.subList(0, Math.min(input.getPageSize(), resultList.size()));
        return resultList;
    }

    public PageResponse<List<CoittDTO>> getAllCoittByMerchandiseGroupCode(String merchandiseGroupCode) {
        List<CoittEntity> coittEntityList = coittRepository.getCoittEntitiesByMerchandiseGroupCode(merchandiseGroupCode);
        Map<Integer, CoittEntity> map = coittEntityList.stream().collect(Collectors.toMap(CoittEntity::getId, Function.identity()));

        List<Citt1Entity> citt1EntityList = citt1Repository.findCitt1EntitiesByMerchandiseGroupCode(merchandiseGroupCode);
        Map<Integer, Citt1Entity> mapCitt1Entity = citt1EntityList
            .stream()
            .collect(Collectors.toMap(Citt1Entity::getId, Function.identity()));

        List<KeyValueEntityV2> properties = new ArrayList<>();
        List<KeyValueEntityV2> tpProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.TP,
            map.keySet()
        );
        List<KeyValueEntityV2> btpProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.BTP,
            map.keySet()
        );
        List<KeyValueEntityV2> nvlProperties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.NVL,
            mapCitt1Entity.keySet()
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

        List<CoittDTO> resultList = new ArrayList<>();
        for (CoittEntity coittEntity : coittEntityList) {
            resultList.add(DTOMapper.toCoittDTO(coittEntity, propertyMap.get(coittEntity.getId())));
        }
        for (Citt1Entity citt1Entity : citt1EntityList) {
            resultList.add(DTOMapper.toCitt1DTO(citt1Entity, propertyMap.get(citt1Entity.getId())));
        }

        return (PageResponse<List<CoittDTO>>) new PageResponse<List<CoittDTO>>(resultList.size()).success().data(resultList);
    }

    @Transactional
    public CommonResponse updateListCoitt(List<CoittDTO> coittDTOS) {
        List<String> coittCodeList = new ArrayList<>();
        List<String> coittCodeDuplicate = new ArrayList<>();

        for (CoittDTO coittDTO : coittDTOS) {
            if (coittCodeList.contains(coittDTO.getProductCode()) && !coittCodeDuplicate.contains(coittDTO.getProductCode())) {
                coittCodeDuplicate.add(coittDTO.getProductCode());
            }
            coittCodeList.add(coittDTO.getProductCode());
            CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittDTO.getProductCode(), true);
            Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(coittDTO.getProductCode());
            if (coittEntity == null && citt1Entity == null) throw new CustomException(
                HttpStatus.NOT_FOUND,
                "Không tồn tại hàng hóa có mã: " + coittDTO.getProductCode()
            );
            MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(
                coittDTO.getMerchandiseGroup()
            );
            if (merchandiseGroupEntity == null) throw new CustomException(
                HttpStatus.NOT_FOUND,
                "Không tìm thấy nhóm hàng hóa có mã: " + coittDTO.getMerchandiseGroup()
            );
            if (citt1Entity != null) {
                citt1Entity.setMerchandiseGroupEntity(merchandiseGroupEntity);
                citt1Repository.save(citt1Entity);
            } else {
                coittEntity.setMerchandiseGroupEntity(merchandiseGroupEntity);
                coittRepository.save(coittEntity);
            }
        }

        if (coittCodeDuplicate != null && coittCodeDuplicate.size() > 0) {
            String coittCodeString = "";
            for (int i = 0; i < coittCodeDuplicate.size() - 1; i++) {
                coittCodeString = coittCodeString + coittCodeDuplicate.get(i) + ", ";
            }
            coittCodeString = coittCodeString + coittCodeDuplicate.get(coittCodeDuplicate.size() - 1) + ".";
            throw new CustomException(HttpStatus.CONFLICT, "Có các mã hàng hóa đang trùng nhau là: " + coittCodeString);
        }

        return new CommonResponse().success("Thành công");
    }

    @Transactional
    public CommonResponse deleteCoittMerchandiseGroup(String coittCode) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittCode, true);
        Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(coittCode);
        if (coittEntity == null && citt1Entity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy hàng hóa có mã: " + coittCode);
        }
        if (citt1Entity != null) {
            citt1Entity.setMerchandiseGroupEntity(null);
            citt1Repository.save(citt1Entity);
        } else {
            coittEntity.setMerchandiseGroupEntity(null);
            coittRepository.save(coittEntity);
        }
        return new CommonResponse().success("Thành công");
    }

    public CoittDTO getProductByCode(String productCode) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(productCode, true);
        if (coittEntity != null) {
            return DTOMapper.toCoittDTO(coittEntity, null);
        }
        Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(productCode);
        if (citt1Entity != null) {
            CoittDTO coittDTO = DTOMapper.toCitt1DTO(citt1Entity, null);
            coittDTO.setMaterialReplaceCode(
                materialReplacementRepository.getMaterialReplacementEntitiesByCode(citt1Entity.getMaterialCode())
            );
            coittDTO.setMaterialReplaceName(citt1Repository.getMaterialName(coittDTO.getMaterialReplaceCode()));
            return coittDTO;
        }
        throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy vật tư có mã: " + productCode);
    }

    public List<CoittDTO> getTPAndBTP() {
        return coittRepository.getAllGroupBy();
    }
}
