package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.ParamDto;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.CoittEntityMapper;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class Citt1Service {

    @Autowired
    CoittRepository coittRepository;

    @Autowired
    CoittEntityMapper coittEntityMapper;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    KeyValueRepository keyValueRepository;

    @Autowired
    Citt1Repository citt1Repository;

    @Autowired
    DTOMapper DTOMapper;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    BusinessLogService businessLogService;

    @Autowired
    MerchandiseGroupRepository merchandiseGroupRepository;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    ParamService paramService;

    public CommonResponse addMaterialForProduct(String productCode, String materialCode) {
        CoittEntity product = coittRepository.findByProductCodeIgnoreCaseAndIsActive(productCode, true);
        if (product == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound", productCode);
        CoittEntity material = coittRepository.findByProductCodeIgnoreCaseAndIsActive(materialCode, true);
        if (material == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound", materialCode);
        Citt1Entity citt1Entity = citt1Repository.findByProductCodeIgnoreCaseAndMaterialCodeIgnoreCaseAndIsActive(
            productCode,
            materialCode,
            true
        );
        if (citt1Entity != null) throw new CustomException(
            HttpStatus.CONFLICT,
            "duplicate.product.material.code",
            materialCode,
            productCode
        );
        citt1Entity = new Citt1Entity();
        CoittEntity coittMaterial = coittRepository.findByProductCodeIgnoreCaseAndIsActive(materialCode, true);
        if (coittMaterial != null) {
            citt1Entity = DTOMapper.coittToCitt1(coittMaterial, productCode);
        }
        citt1Repository.save(citt1Entity);
        return new CommonResponse<>().success("Thêm thành công " + materialCode + " vào " + productCode);
    }

    public List<String> autocompleteCommon(PageFilterInput<CoittDTO> input) {
        Set<String> listAuto = new LinkedHashSet<>();
        String common = input.getCommon();
        Pageable pageable = Pageable.unpaged();

        //Get list object that it had the search.
        List<Citt1Entity> citt1Entities = citt1Repository.getAll(input, pageable).getContent();
        //Compare to filter to get the list result
        if (citt1Entities != null && !citt1Entities.isEmpty()) {
            citt1Entities.forEach(item -> {
                List<KeyValueEntityV2> list = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    Contants.EntityType.NVL,
                    new ArrayList<>(Arrays.asList(item.getId()))
                );
                if (
                    item.getMaterialCode() != null &&
                    !item.getMaterialCode().isEmpty() &&
                    item.getMaterialCode().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getMaterialCode());
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

    public CommonResponse createProduct(CoittDTO coittDTO) {
        Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(coittDTO.getProductCode());
        if (citt1Entity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", coittDTO.getProductCode());
        if (coittDTO.getProductCode() == null) throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
        citt1Entity = new Citt1Entity();

        citt1Entity.setMaterialCode(coittDTO.getProductCode());
        citt1Entity.setProName(coittDTO.getProName());
        citt1Entity.setTechName(coittDTO.getTechName());
        citt1Entity.setUnit(coittDTO.getUnit());
        citt1Entity.setNote(coittDTO.getNote());
        citt1Entity.setStatus(coittDTO.getStatus());
        citt1Entity.setKind(coittDTO.getKind());
        citt1Entity.setItemGroupCode(coittDTO.getItemGroupCode());

        MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(
            coittDTO.getMerchandiseGroup()
        );
        if (merchandiseGroupEntity != null) {
            citt1Entity.setMerchandiseGroupEntity(merchandiseGroupEntity);
        }

        citt1Repository.save(citt1Entity);
        businessLogService.insertInsertionLog(
            citt1Entity.getId(),
            Contants.EntityType.NVL,
            DTOMapper.toLogDetail(citt1Entity, coittDTO.getPropertiesMap())
        );
        if (coittDTO.getPropertiesMap().isEmpty()) return new CommonResponse<>().success("Thêm mới thành công");
        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
            citt1Entity.getId(),
            coittDTO.getPropertiesMap(),
            Contants.EntityType.NVL
        );
        keyValueV2Repository.saveAll(keyValueEntities);
        return new CommonResponse<>().success("Thêm mới thành công");
    }

    public CommonResponse updatedProduct(CoittDTO coittDTO, String productCode) {
        Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(productCode);
        if (citt1Entity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        Citt1Entity oldValue = new Citt1Entity(citt1Entity);
        citt1Entity.setProName(coittDTO.getProName());
        citt1Entity.setTechName(coittDTO.getTechName());
        citt1Entity.setItemGroupCode(coittDTO.getItemGroupCode());
        citt1Entity.setUnit(coittDTO.getUnit());
        citt1Entity.setNote(coittDTO.getNote());
        citt1Entity.setKind(coittDTO.getKind());
        citt1Entity.setStatus(coittDTO.getStatus());
        MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(
            coittDTO.getMerchandiseGroup()
        );
        if (merchandiseGroupEntity != null) {
            citt1Entity.setMerchandiseGroupEntity(merchandiseGroupEntity);
        }

        Citt1Entity savedEntity = citt1Repository.save(citt1Entity);
        BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
            savedEntity.getId(),
            Contants.EntityType.NVL,
            DTOMapper.toUpdateLogDetail(oldValue, citt1Entity)
        );
        if (coittDTO.getPropertiesMap().isEmpty()) return new CommonResponse<>().success("Cập nhật thành công");
        keyValueService.createUpdateKeyValueOfEntityWithLog(
            citt1Entity.getId(),
            coittDTO.getPropertiesMap(),
            Contants.EntityType.NVL,
            true,
            logEntity
        );
        return new CommonResponse<>().success("Cập nhật thành công");
    }

    public CommonResponse importProduct(MultipartFile file, Integer itemGroupCode) throws IOException, ParseException {
        List<CoittDTO> coittDTOS = xlsxExcelHandle.readProductFromExcel(file.getInputStream(), itemGroupCode);

        for (CoittDTO c : coittDTOS) {
            Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(c.getProductCode());
            if (citt1Entity != null) {
                throw new CustomException(HttpStatus.CONFLICT, "duplicate", c.getProductCode());
            }
            if (c.getProductCode() == null || c.getProductCode().isEmpty()) continue;
            citt1Entity = new Citt1Entity();
            citt1Entity.setMaterialCode(c.getProductCode());
            citt1Entity.setProName(c.getProName());
            citt1Entity.setTechName(c.getTechName());
            citt1Entity.setUnit(c.getUnit());
            citt1Entity.setNote(c.getNote());
            citt1Entity.setStatus(c.getStatus());
            citt1Entity.setKind(c.getKind());
            citt1Entity.setItemGroupCode(Contants.ItemGroup.NVL);
            List<KeyValueEntityV2> keyValues = keyValueService.createOrUpdateKeyValueEntity(
                citt1Entity.getId(),
                c.getPropertiesMap(),
                Contants.EntityType.NVL
            );
        }
        Map<Integer, List<BusinessLogDetailEntity>> businessLogMap = new HashMap<>();
        List<ParamDto> paramDtoList = new ArrayList<>();
        for (CoittDTO c : coittDTOS) {
            Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(c.getProductCode());
            if (citt1Entity == null) {
                if (c.getProductCode() == null || c.getProductCode().isEmpty()) continue;
                citt1Entity = new Citt1Entity();
                citt1Entity.setMaterialCode(c.getProductCode());
                citt1Entity.setProName(c.getProName());
                citt1Entity.setTechName(c.getTechName());
                citt1Entity.setUnit(c.getUnit());
                citt1Entity.setNote(c.getNote());
                citt1Entity.setStatus(c.getStatus());
                citt1Entity.setKind(c.getKind());
                citt1Entity.setItemGroupCode(Contants.ItemGroup.NVL);
                citt1Repository.save(citt1Entity);
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    citt1Entity.getId(),
                    c.getPropertiesMap(),
                    Contants.EntityType.NVL
                );
                businessLogMap.put(citt1Entity.getId(), DTOMapper.toLogDetail(citt1Entity, keyValueEntities));
                keyValueV2Repository.saveAll(keyValueEntities);

                if (citt1Entity.getUnit() != null) {
                    ParamDto paramDto = new ParamDto();
                    paramDto.setParamValue(citt1Entity.getUnit());
                    paramDto.setParamDesc(citt1Entity.getUnit());
                    paramDtoList.add(paramDto);
                }
            }
        }
        businessLogService.insertInsertionLogByBatch(Contants.EntityType.NVL, businessLogMap);
        if (paramDtoList != null) {
            CommonResponse commonResponse = paramService.addParams("DVT", paramDtoList);
        }
        return new CommonResponse<>().success();
    }

    public CommonResponse removeMaterialForProduct(String productCode, String materialCode) {
        CoittEntity product = coittRepository.findByProductCodeIgnoreCaseAndIsActive(productCode, true);
        if (product == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound", productCode);
        CoittEntity material = coittRepository.findByProductCodeIgnoreCaseAndIsActive(materialCode, true);
        if (material == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound", materialCode);
        Citt1Entity citt1Entity = citt1Repository.findByProductCodeIgnoreCaseAndMaterialCodeIgnoreCaseAndIsActive(
            productCode,
            materialCode,
            true
        );
        if (citt1Entity == null) throw new CustomException(HttpStatus.NOT_FOUND, materialCode + " chưa đã thuộc " + productCode);
        citt1Entity.setActive(false);
        citt1Repository.save(citt1Entity);
        return new CommonResponse<>().success("Đã xóa " + materialCode + " khỏi " + productCode);
    }
    //    public CommonResponse getStructure(String productCode){
    //    }
}
