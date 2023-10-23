package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.DataToFillBom;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.CoittEntityMapper;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BomService {

    @Autowired
    Citt1Repository citt1Repository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    CoittRepository coittRepository;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    DTOMapper DTOMapper;

    @Autowired
    CoittEntityMapper coittEntityMapper;

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    AssemblyRepository assemblyRepository;

    //lấy danh sách BOM
    public PageResponse getAllBom(PageFilterInput<CoittDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        List<CoittDTO> resultList = new ArrayList<>();

        List<String> coittCodes = assemblyRepository.getAllParentCode();

        Page<CoittEntity> coittEntityPage = coittRepository.getAllBom(input, pageable, coittCodes);

        List<CoittEntity> listCoittEntity = coittEntityPage.getContent();
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
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));
        for (CoittEntity coittEntity : listCoittEntity) {
            CoittDTO coittDTO = DTOMapper.toCoittDTO(coittEntity, propertyMap.get(coittEntity.getId()));
            resultList.add(coittDTO);
        }

        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.BOM);

        return new PageResponse<List<CoittDTO>>()
            .data(resultList)
            .success()
            .dataCount(coittEntityPage.getTotalElements())
            .columns(columnPropertyEntities);
    }

    public PageResponse getAllBomByParent(PageFilterInput<CoittDTO> input, String coittCode) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        List<CoittDTO> resultList = new ArrayList<>();

        List<String> coittCodes = assemblyRepository.getChildCode(coittCode);

        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.BOM);

        if (coittCodes == null || coittCodes.size() == 0) {
            return new PageResponse<List<CoittDTO>>().data(resultList).success().dataCount(0).columns(columnPropertyEntities);
        }

        Page<CoittEntity> coittEntityPage = coittRepository.getAllBomChild(input, pageable, coittCodes);

        List<CoittEntity> listCoittEntity = coittEntityPage.getContent();
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
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));
        for (CoittEntity coittEntity : listCoittEntity) {
            CoittDTO coittDTO = DTOMapper.toCoittDTO(coittEntity, propertyMap.get(coittEntity.getId()));
            AssemblyEntity assemblyEntity = assemblyRepository.getAssemblyEntities(coittCode, coittDTO.getProductCode());
            if (assemblyEntity != null) {
                coittDTO.setVersion(assemblyEntity.getVersion());
            }
            resultList.add(coittDTO);
        }

        return new PageResponse<List<CoittDTO>>()
            .data(resultList)
            .success()
            .dataCount(coittEntityPage.getTotalElements())
            .columns(columnPropertyEntities);
    }

    //Tạo BOM mới
    @Transactional
    public CommonResponse createBom(CoittDTO coittDTO) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittDTO.getProductCode(), true);
        if (coittEntity == null) throw new CustomException(
            HttpStatus.CONFLICT,
            "Không tồn tại sản phẩm có mã: " + coittDTO.getProductCode()
        );
        coittEntity.setProDesc(coittDTO.getProDesc());
        coittEntity.setWareHouse(coittDTO.getWareHouse());
        coittEntity.setQuantity(coittDTO.getQuantity());
        coittEntity.setNote(coittDTO.getNote());
        coittEntity.setNotice(coittDTO.getNotice());
        coittRepository.save(coittEntity);
        if (coittDTO.getProParent().equals(coittDTO.getProductCode())) {
            coittEntity.setVersion(coittDTO.getVersion());
            AssemblyEntity assemblyEntity = assemblyRepository.getAssemblyEntitiesByCode(coittDTO.getProParent());
            if (assemblyEntity != null) throw new CustomException(
                HttpStatus.CONFLICT,
                "Đã tồn tại sản phẩm có mã: " + coittDTO.getProductCode()
            );
            assemblyEntity = new AssemblyEntity();
            assemblyEntity.setParentCode(coittDTO.getProductCode());
            assemblyRepository.save(assemblyEntity);
        } else {
            AssemblyEntity assemblyEntity = assemblyRepository.getAssemblyEntities(coittDTO.getProParent(), coittDTO.getProductCode());
            if (assemblyEntity != null) throw new CustomException(
                HttpStatus.CONFLICT,
                "Đã tồn tại sản phẩm có mã: " + coittDTO.getProductCode()
            );
            assemblyEntity = new AssemblyEntity();
            assemblyEntity.setParentCode(coittDTO.getProParent());
            assemblyEntity.setChildCode(coittDTO.getProductCode());
            assemblyEntity.setVersion(coittDTO.getVersion());
            assemblyRepository.save(assemblyEntity);
        }
        List<Citt1Entity> citt1EntityList = citt1Repository.getAllByCitt1ByParent(coittDTO.getProParent());
        citt1Repository.deleteAll(citt1EntityList);

        CoittEntity coittEntityParent = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittDTO.getProParent(), true);
        for (CoittDTO coittDTOChild : coittDTO.getCoittDTOS()) {
            Citt1Entity citt1EntityChild = citt1Repository.getCitt1EntitiesByBom(
                coittDTOChild.getProductCode(),
                coittDTO.getProParent(),
                coittDTO.getVersion()
            );
            if (citt1EntityChild != null) throw new CustomException(
                HttpStatus.CONFLICT,
                "Đã tồn tại hàng hóa có có mã: " + coittDTOChild.getProductCode()
            );

            CoittEntity checkCoitt = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittDTOChild.getProductCode(), true);
            citt1EntityChild = new Citt1Entity();
            citt1EntityChild.setMaterialCode(coittDTOChild.getProductCode());
            citt1EntityChild.setCoittEntity(coittEntityParent);
            citt1EntityChild.setQuantity(coittDTOChild.getQuantity());
            citt1EntityChild.setQuantity(coittDTOChild.getQuantity());
            if (checkCoitt != null) {
                citt1EntityChild.setVersion(coittDTOChild.getVersion());
            }
            citt1EntityChild.setVendor(coittDTOChild.getVendor());
            citt1EntityChild.setActive(true);
            citt1Repository.save(citt1EntityChild);
            //            if (citt1EntityChild != null) {
            //                citt1EntityChild.setQuantity(coittDTOChild.getQuantity());
            //                if (checkCoitt != null) {
            //                    citt1EntityChild.setVersion(coittDTOChild.getVersion());
            //                }
            //                citt1EntityChild.setVendor(coittDTOChild.getVendor());
            //                citt1EntityChild.setActive(true);
            //                citt1Repository.save(citt1EntityChild);
            //            } else {
            //
            //            }
        }
        return new CommonResponse<>().success();
    }

    //Cập nhật BOM
    @Transactional
    public CommonResponse updateBom(CoittDTO coittDTO, String productCode) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(productCode, true);
        if (coittEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm có mã: " + productCode);
        if (coittDTO.getProParent().equals(coittDTO.getProductCode())) {
            AssemblyEntity assemblyEntity = assemblyRepository.getAssemblyEntitiesByCode(coittDTO.getProParent());
            if (assemblyEntity == null) throw new CustomException(
                HttpStatus.CONFLICT,
                "Không tồn tại phẩm có mã: " + coittDTO.getProductCode()
            );
        } else {
            AssemblyEntity assemblyEntity = assemblyRepository.getAssemblyEntities(coittDTO.getProParent(), coittDTO.getProductCode());
            if (assemblyEntity == null) throw new CustomException(
                HttpStatus.CONFLICT,
                "Không tồn tại sản phẩm có mã: " + coittDTO.getProductCode()
            );
            assemblyEntity.setParentCode(coittDTO.getProParent());
            assemblyEntity.setChildCode(coittDTO.getProductCode());
            assemblyEntity.setVersion(coittDTO.getVersion());
            assemblyRepository.save(assemblyEntity);
        }
        coittEntityMapper.updateFromDTO(coittEntity, coittDTO);
        coittRepository.save(coittEntity);

        List<Citt1Entity> citt1EntityList = citt1Repository.getAllByCitt1ByParent(coittDTO.getProParent());
        citt1Repository.deleteAll(citt1EntityList);
        CoittEntity coittEntityParent = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittDTO.getProParent(), true);
        for (CoittDTO coittDTOChild : coittDTO.getCoittDTOS()) {
            Citt1Entity citt1EntityChild = citt1Repository.getCitt1EntitiesByBom(
                coittDTOChild.getProductCode(),
                coittDTO.getProParent(),
                coittDTO.getVersion()
            );
            if (citt1EntityChild != null) throw new CustomException(
                HttpStatus.CONFLICT,
                "Đã tồn tại hàng hóa có có mã: " + coittDTOChild.getProductCode()
            );
            CoittEntity checkCoitt = coittRepository.findByProductCodeIgnoreCaseAndIsActive(coittDTOChild.getProductCode(), true);
            citt1EntityChild = new Citt1Entity();
            citt1EntityChild.setMaterialCode(coittDTOChild.getProductCode());
            citt1EntityChild.setCoittEntity(coittEntityParent);
            citt1EntityChild.setQuantity(coittDTOChild.getQuantity());
            if (checkCoitt != null) {
                citt1EntityChild.setVersion(coittDTOChild.getVersion());
            }
            citt1EntityChild.setVendor(coittDTOChild.getVendor());
            citt1EntityChild.setActive(true);
            citt1Repository.save(citt1EntityChild);
            //            if (citt1EntityChild != null) {
            //                citt1EntityChild.setQuantity(coittDTOChild.getQuantity());
            //                if (checkCoitt != null) {
            //                    citt1EntityChild.setVersion(coittDTOChild.getVersion());
            //                }
            //                citt1EntityChild.setVendor(coittDTOChild.getVendor());
            //                citt1EntityChild.setActive(true);
            //                citt1Repository.save(citt1EntityChild);
            //            } else {
            //                citt1EntityChild = new Citt1Entity();
            //                citt1EntityChild.setCoittEntity(coittEntity);
            //                citt1EntityChild.setMaterialCode(coittDTOChild.getProductCode());
            //                citt1EntityChild.setQuantity(coittDTOChild.getQuantity());
            //                if (checkCoitt != null) {
            //                    citt1EntityChild.setVersion(coittDTOChild.getVersion());
            //                }
            //                citt1EntityChild.setVendor(coittDTOChild.getVendor());
            //                citt1EntityChild.setActive(true);
            //                citt1Repository.save(citt1EntityChild);
            //            }
        }
        return new CommonResponse<>().success("Cập nhật thành công");
    }

    //Xóa vật tư khỏi BOM
    @Transactional
    public CommonResponse deleteMaterial(String productCode, String materialCode, String version) {
        Citt1Entity citt1Entity = citt1Repository.getCitt1EntitiesByBom(materialCode, productCode, version);
        if (citt1Entity == null) throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy");
        citt1Entity.setActive(false);
        citt1Repository.save(citt1Entity);
        return new CommonResponse().success("Xóa thành công");
    }

    //Lấy data để fill ra chỗ chọn vật tư ở BOM
    public List<DataToFillBom> getDataToFillBom(List<String> productCodes) {
        List<DataToFillBom> resultList = new ArrayList<>();
        List<DataToFillBom> dataToFillBomListCoitt;
        List<DataToFillBom> dataToFillBomListCitt1;
        if (productCodes != null) {
            dataToFillBomListCoitt = coittRepository.getCoittEntitiesByCodeToFillByProductCodes(productCodes);
            dataToFillBomListCitt1 = citt1Repository.getCitt1EntitiesByProductCodesToFillBomByProductCodes(productCodes);
        } else {
            dataToFillBomListCoitt = coittRepository.getCoittEntitiesByCodeToFill();
            dataToFillBomListCitt1 = citt1Repository.getCitt1EntitiesByProductCodesToFillBom();
        }
        //        for (DataToFillBom data : dataToFillBomListCoitt) {
        //            data.setVendor(vendorRepository.getVendorNameByProductCode(data.getProductCode()));
        //            resultList.add(data);
        //        }
        //
        //        for (DataToFillBom data : dataToFillBomListCitt1) {
        //            data.setVendor(vendorRepository.getVendorNameByProductCode(data.getProductCode()));
        //            resultList.add(data);
        //        }
        return resultList;
    }

    public List<DataToFillBom> getDataByBom(String bomParentCode) {
        List<DataToFillBom> dataToFillBomList = citt1Repository.getCitt1EntitiesByBomParentCode(bomParentCode);
        List<DataToFillBom> result = new ArrayList<>();
        for (DataToFillBom data : dataToFillBomList) {
            Citt1Entity citt1Entity = citt1Repository.getCitt1EntitiesByCodeBOM(data.getProductCode());
            if (citt1Entity != null) {
                data.setProductName(citt1Entity.getProName());
                data.setTechName(citt1Entity.getTechName());
                data.setTechName(citt1Entity.getTechName());
                data.setItemGroup(citt1Entity.getItemGroupCode());
                data.setUnit(citt1Entity.getUnit());
            }
            CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(data.getProductCode(), true);
            if (coittEntity != null) {
                data.setProductName(coittEntity.getProName());
                data.setTechName(coittEntity.getTechName());
                data.setTechName(coittEntity.getTechName());
                data.setItemGroup(coittEntity.getItemGroupCode());
                data.setUnit(coittEntity.getUnit());
            }
            Citt1Entity materialReplace = citt1Repository.getCitt1EntitiesByCodeBOM(data.getMaterialReplaceCode());
            if (materialReplace != null) {
                data.setMaterialReplaceName(materialReplace.getProName());
            }
            result.add(data);
        }
        return result;
    }

    // import mã vật tư để fill BOM
    public List<DataToFillBom> importDataToFillBom(MultipartFile file) throws IOException {
        List<DataToFillBom> dataToFillBomList = xlsxExcelHandle.importDataToFillBom(file.getInputStream());
        List<DataToFillBom> result = new ArrayList<>();
        for (DataToFillBom data : dataToFillBomList) {
            Citt1Entity citt1Entity = citt1Repository.getCitt1EntitiesByCodeBOM(data.getProductCode());
            if (citt1Entity != null) {
                if (data.getVersion() != null) throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "Không được nhập version cho nguyên vật liệu"
                );
                data.setProductName(citt1Entity.getProName());
                data.setTechName(citt1Entity.getTechName());
                data.setTechName(citt1Entity.getTechName());
                data.setItemGroup(citt1Entity.getItemGroupCode());
                data.setUnit(citt1Entity.getUnit());
            } else {
                CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(data.getProductCode(), true);
                if (coittEntity != null) {
                    data.setProductName(coittEntity.getProName());
                    data.setTechName(coittEntity.getTechName());
                    data.setTechName(coittEntity.getTechName());
                    data.setItemGroup(coittEntity.getItemGroupCode());
                    data.setUnit(coittEntity.getUnit());
                } else {
                    throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy vật liệu có mã: " + data.getProductCode());
                }
            }

            Citt1Entity materialReplace = citt1Repository.getCitt1EntitiesByCodeBOM(data.getMaterialReplaceCode());
            if (materialReplace != null) {
                data.setMaterialReplaceName(materialReplace.getProName());
            }
            result.add(data);
        }
        return result;
    }

    @Transactional
    public CommonResponse changeStatus(String productCode, Integer status) {
        CoittEntity coittEntity = coittRepository.findByProductCodeIgnoreCaseAndIsActive(productCode, true);
        coittEntity.setStatus(status);
        coittRepository.save(coittEntity);
        return new CommonResponse<>().success();
    }

    public CommonResponse importBom(MultipartFile file) throws IOException {
        Map<CoittDTO, List<CoittDTO>> data = xlsxExcelHandle.readBomFromExcel(file.getInputStream());
        for (CoittDTO coittDTO : data.keySet()) {
            coittDTO.setProParent(coittDTO.getProductCode());
            createBom(coittDTO);
        }
        return new CommonResponse().success();
    }
}
