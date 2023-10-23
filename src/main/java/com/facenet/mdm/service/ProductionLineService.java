package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.repository.custom.ProductionLineCustomRepository;
import com.facenet.mdm.service.dto.BaseDynamicDTO;
import com.facenet.mdm.service.dto.ProductionLineDTO;
import com.facenet.mdm.service.dto.excel.ProductionLineExcel;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.ProductionLineDTOMapper;
import com.facenet.mdm.service.mapper.ProductionLineEntityMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import liquibase.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductionLineService {

    private static final Logger log = LoggerFactory.getLogger(ProductionLineService.class);
    private final ProductionLineRepository productionLineRepository;
    private final ProductionLineCustomRepository productionLineCustomRepository;
    private final ColumnPropertyRepository columnPropertyRepository;
    private final ProductionLineDTOMapper productionLineDTOMapper;
    private final ProductionLineEntityMapper productionLineEntityMapper;
    private final KeyValueService keyValueService;
    private final XlsxExcelHandle xlsxExcelHandle;
    private final ProductionLineTypeRepository productionLineTypeRepository;
    private final KeyValueV2Repository keyValueV2Repository;
    private final BusinessLogService businessLogService;

    public ProductionLineService(
        ProductionLineRepository productionLineRepository,
        ProductionLineCustomRepository productionLineCustomRepository,
        ColumnPropertyRepository columnPropertyRepository,
        ProductionLineDTOMapper productionLineDTOMapper,
        ProductionLineEntityMapper productionLineEntityMapper,
        KeyValueService keyValueService,
        XlsxExcelHandle xlsxExcelHandle,
        ProductionLineTypeRepository productionLineTypeRepository,
        KeyValueV2Repository keyValueV2Repository,
        BusinessLogService businessLogService
    ) {
        this.productionLineRepository = productionLineRepository;
        this.productionLineCustomRepository = productionLineCustomRepository;
        this.columnPropertyRepository = columnPropertyRepository;
        this.productionLineDTOMapper = productionLineDTOMapper;
        this.productionLineEntityMapper = productionLineEntityMapper;
        this.keyValueService = keyValueService;
        this.xlsxExcelHandle = xlsxExcelHandle;
        this.productionLineTypeRepository = productionLineTypeRepository;
        this.keyValueV2Repository = keyValueV2Repository;
        this.businessLogService = businessLogService;
    }

    public PageResponse<List<ProductionLineDTO>> getAllProductionLine(PageFilterInput<ProductionLineDTO> filterInput) {
        Pageable pageable = filterInput.getPageSize() == 0
            ? Pageable.unpaged()
            : PageRequest.of(filterInput.getPageNumber(), filterInput.getPageSize());
        Page<ProductionLineEntity> resultEntity = productionLineCustomRepository.getAll(filterInput, pageable);

        List<KeyValueEntityV2> properties;
        if (pageable.isPaged()) properties =
            keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.PRODUCTION_LINE,
                resultEntity.stream().map(ProductionLineEntity::getId).collect(Collectors.toList())
            ); else properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.PRODUCTION_LINE);

        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        List<ProductionLineDTO> resultList = new ArrayList<>();
        for (ProductionLineEntity productionLineEntity : resultEntity.getContent()) {
            resultList.add(productionLineDTOMapper.toDTO(productionLineEntity, propertyMap.get(productionLineEntity.getId())));
        }

        List<ColumnPropertyEntity> keyDTOList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.PRODUCTION_LINE
        );

        return new PageResponse<List<ProductionLineDTO>>()
            .success()
            .data(resultList)
            .dataCount(resultEntity.getTotalElements())
            .columns(keyDTOList);
    }

    public List<String> getAutoCompleteProductionLine(PageFilterInput<ProductionLineDTO> filterInput) {
        List<ProductionLineDTO> productionLineDTOList = getAllProductionLine(filterInput).getData();
        String common = filterInput.getCommon();
        List<String> searchAutoComplete = new ArrayList<>();
        for (ProductionLineDTO productionLineDTO : productionLineDTOList) {
            if (
                productionLineDTO.getProductionLineCode().toLowerCase().contains(common.toLowerCase()) &&
                !searchAutoComplete.contains(productionLineDTO.getProductionLineCode())
            ) searchAutoComplete.add(productionLineDTO.getProductionLineCode());
            if (
                productionLineDTO.getProductionLineName().toLowerCase().contains(common.toLowerCase()) &&
                !searchAutoComplete.contains(productionLineDTO.getProductionLineName())
            ) searchAutoComplete.add(productionLineDTO.getProductionLineName());
            if (
                productionLineDTO.getProductionLineType() != null &&
                productionLineDTO
                    .getProductionLineType()
                    .getProductionLineTypeName()
                    .toLowerCase()
                    .toString()
                    .contains(common.toLowerCase().toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getProductionLineType().getProductionLineTypeName().toString())
            ) searchAutoComplete.add(productionLineDTO.getProductionLineType().getProductionLineTypeName().toString());
            if (
                productionLineDTO.getDescription() != null &&
                productionLineDTO.getDescription().toLowerCase().toString().contains(common.toLowerCase().toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getDescription().toString())
            ) searchAutoComplete.add(productionLineDTO.getDescription().toString());
            if (
                productionLineDTO.getProductivity() != null &&
                productionLineDTO.getProductivity().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getProductivity().toString())
            ) searchAutoComplete.add(formatDoubleValue(productionLineDTO.getProductivity()).toString());
            if (
                productionLineDTO.getSupplier() != null &&
                productionLineDTO.getSupplier().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getSupplier().toString())
            ) searchAutoComplete.add(productionLineDTO.getSupplier().toString());
            if (
                productionLineDTO.getMinProductionQuantity() != null &&
                productionLineDTO.getMinProductionQuantity().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getMinProductionQuantity().toString())
            ) searchAutoComplete.add(formatDoubleValue(productionLineDTO.getMinProductionQuantity()));
            if (
                productionLineDTO.getMaxProductionQuantity() != null &&
                productionLineDTO.getMaxProductionQuantity().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getMaxProductionQuantity().toString())
            ) searchAutoComplete.add(formatDoubleValue(productionLineDTO.getMaxProductionQuantity()));
            if (
                productionLineDTO.getPurchaseDate() != null &&
                productionLineDTO.getPurchaseDate().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getPurchaseDate().toString())
            ) searchAutoComplete.add(productionLineDTO.getPurchaseDate().toString());
            if (
                productionLineDTO.getMaxWaitingTime() != null &&
                productionLineDTO.getMaxWaitingTime().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getMaxWaitingTime().toString())
            ) searchAutoComplete.add(formatDoubleValue(productionLineDTO.getMaxWaitingTime()));
            if (
                productionLineDTO.getMaintenanceTime() != null &&
                productionLineDTO.getMaintenanceTime().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getMaintenanceTime().toString())
            ) searchAutoComplete.add(formatDoubleValue(productionLineDTO.getMaintenanceTime()));
            if (
                productionLineDTO.getCycleTime() != null &&
                productionLineDTO.getCycleTime().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(productionLineDTO.getCycleTime().toString())
            ) searchAutoComplete.add(formatDoubleValue(productionLineDTO.getCycleTime()));

            if (productionLineDTO.getPropertiesMap() != null) {
                for (String key : productionLineDTO.getPropertiesMap().keySet()) {
                    if (
                        !StringUtil.isEmpty(productionLineDTO.getPropertiesMap().get(key)) &&
                        productionLineDTO.getPropertiesMap().get(key).toLowerCase().contains(common.toLowerCase()) &&
                        !searchAutoComplete.contains(productionLineDTO.getPropertiesMap().get(key))
                    ) {
                        searchAutoComplete.add(productionLineDTO.getPropertiesMap().get(key));
                    }
                }
            }
            if (searchAutoComplete.size() >= 10) break;
        }
        return searchAutoComplete;
    }

    private String formatDoubleValue(double value) {
        if (value == (int) value) {
            return String.format("%d", (int) value);
        } else {
            return String.format("%s", value);
        }
    }

    public void createProductionLine(ProductionLineDTO input) {
        ProductionLineEntity checkProductionLineEntity = productionLineRepository.findByProductionLineCodeAndIsActiveTrue(
            input.getProductionLineCode()
        );
        if (checkProductionLineEntity != null) throw new CustomException(
            HttpStatus.NOT_FOUND,
            "duplicate.productionLine.code",
            input.getProductionLineCode()
        );

        ProductionLineEntity productionLineEntity = productionLineEntityMapper.toEntity(input);
        if (input.getProductionLineType() != null && input.getProductionLineType().getId() != null) {
            ProductionLineTypeEntity productionLineType = productionLineTypeRepository
                .findById(input.getProductionLineType().getId())
                .orElse(null);
            productionLineEntity.setProductionLineType(productionLineType);
        }

        productionLineEntity = productionLineRepository.save(productionLineEntity);
        if (input.getPropertiesMap().isEmpty()) return;
        keyValueService.createUpdateKeyValueOfEntity(
            productionLineEntity.getId(),
            input.getPropertiesMap(),
            Contants.EntityType.PRODUCTION_LINE,
            false
        );

        businessLogService.insertInsertionLog(
            productionLineEntity.getId(),
            Contants.EntityType.PRODUCTION_LINE,
            productionLineDTOMapper.toLogDetail(productionLineEntity, input.getPropertiesMap())
        );
    }

    public void updateProductionLine(ProductionLineDTO input, String productionLineCode) {
        ProductionLineEntity productionLineEntity = productionLineRepository.findByProductionLineCodeAndIsActiveTrue(productionLineCode);
        if (productionLineEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        ProductionLineEntity oldValue = new ProductionLineEntity(productionLineEntity);
        productionLineEntityMapper.updateFromDTO(productionLineEntity, input);

        if (input.getProductionLineType() != null && input.getProductionLineType().getId() != null) {
            ProductionLineTypeEntity productionLineType = productionLineTypeRepository
                .findById(input.getProductionLineType().getId())
                .orElse(null);
            productionLineEntity.setProductionLineType(productionLineType);
        }

        ProductionLineEntity savedEntity = productionLineRepository.save(productionLineEntity);

        BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
            savedEntity.getId(),
            Contants.EntityType.PRODUCTION_LINE,
            productionLineDTOMapper.toUpdateLogDetail(oldValue, productionLineEntity)
        );

        if (input.getPropertiesMap().isEmpty()) return;
        keyValueService.createUpdateKeyValueOfEntityWithLog(
            productionLineEntity.getId(),
            input.getPropertiesMap(),
            Contants.EntityType.PRODUCTION_LINE,
            true,
            logEntity
        );
    }

    public void deleteProductionLine(String productionLineCode) {
        ProductionLineEntity productionLineEntity = productionLineRepository.findByProductionLineCodeAndIsActiveTrue(productionLineCode);
        if (productionLineEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        productionLineEntity.setActive(false);
        productionLineRepository.save(productionLineEntity);

        businessLogService.insertDeleteLog(
            productionLineEntity.getId(),
            Contants.EntityType.PRODUCTION_LINE,
            productionLineDTOMapper.toDeletionLogDetail(productionLineEntity)
        );
    }

    public void importExcel(MultipartFile file) throws IOException {
        ProductionLineExcel productionLineExcel = xlsxExcelHandle.readProductionLineFromExcel(file.getInputStream());
        Set<String> allProductionLineCode = productionLineRepository.getAllProductionLineCodeIn(
            productionLineExcel
                .getProductionLineEntities()
                .stream()
                .map(ProductionLineEntity::getProductionLineCode)
                .collect(Collectors.toList())
        );
        if (allProductionLineCode.size() != 0) {
            StringJoiner stringJoiner = new StringJoiner(",");
            productionLineExcel
                .getProductionLineEntities()
                .forEach(productionLineEntity -> {
                    if (allProductionLineCode.contains(productionLineEntity.getProductionLineCode())) {
                        stringJoiner.add(productionLineEntity.getProductionLineCode());
                    }
                });
            throw new CustomException(HttpStatus.BAD_REQUEST, "duplicate.productionLine.code", stringJoiner.toString());
        }

        List<ProductionLineEntity> productionLineEntities = productionLineRepository.saveAll(
            productionLineExcel.getProductionLineEntities()
        );
        for (ProductionLineEntity productionLineEntity : productionLineEntities) {
            BaseDynamicDTO baseDynamicDTO = productionLineExcel
                .getPropertiesOfProductionLine()
                .get(productionLineEntity.getProductionLineCode());
            List<KeyValueEntityV2> list = keyValueService.createOrUpdateKeyValueEntity(
                productionLineEntity.getId(),
                baseDynamicDTO.getPropertiesMap(),
                Contants.EntityType.PRODUCTION_LINE
            );
        }
        for (ProductionLineEntity productionLineEntity : productionLineEntities) {
            BaseDynamicDTO baseDynamicDTO = productionLineExcel
                .getPropertiesOfProductionLine()
                .get(productionLineEntity.getProductionLineCode());
            List<KeyValueEntityV2> list = keyValueService.createOrUpdateKeyValueEntity(
                productionLineEntity.getId(),
                baseDynamicDTO.getPropertiesMap(),
                Contants.EntityType.PRODUCTION_LINE
            );
            keyValueV2Repository.saveAll(list);
        }
    }
}
