package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.repository.custom.AutoCompleteCustomRepository;
import com.facenet.mdm.repository.custom.MachineCustomRepository;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.dto.excel.MachineExcel;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.MachineDTOMapper;
import com.facenet.mdm.service.mapper.MachineEntityMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.ExcelUtils;
import com.facenet.mdm.service.utils.Utils;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MachineService {

    private final Logger log = LogManager.getLogger(MachineService.class);
    private final MachineRepository machineRepository;
    private final MachineEntityMapper machineEntityMapper;
    private final KeyValueRepository keyValueRepository;
    private final MachineDTOMapper machineDTOMapper;
    private final ColumnPropertyRepository columnPropertyRepository;
    private final MachineCustomRepository machineCustomRepository;
    private final KeyValueService keyValueService;
    private final XlsxExcelHandle xlsxExcelHandle;
    private final MachineTypeRepository machineTypeRepository;
    private final KeyValueV2Repository keyValueV2Repository;
    private final AutoCompleteCustomRepository<MachineEntity> autoCompleteCustomRepository;
    private final BusinessLogService businessLogService;

    public MachineService(
        MachineRepository machineRepository,
        MachineEntityMapper machineEntityMapper,
        KeyValueRepository keyValueRepository,
        MachineDTOMapper machineDTOMapper,
        ColumnPropertyRepository columnPropertyRepository,
        MachineCustomRepository machineCustomRepository,
        KeyValueService keyValueService,
        XlsxExcelHandle xlsxExcelHandle,
        MachineTypeRepository machineTypeRepository,
        KeyValueV2Repository keyValueV2Repository,
        AutoCompleteCustomRepository<MachineEntity> autoCompleteCustomRepository,
        BusinessLogService businessLogService
    ) {
        this.machineRepository = machineRepository;
        this.machineEntityMapper = machineEntityMapper;
        this.keyValueRepository = keyValueRepository;
        this.machineDTOMapper = machineDTOMapper;
        this.columnPropertyRepository = columnPropertyRepository;
        this.machineCustomRepository = machineCustomRepository;
        this.xlsxExcelHandle = xlsxExcelHandle;
        this.keyValueService = keyValueService;
        this.machineTypeRepository = machineTypeRepository;
        this.keyValueV2Repository = keyValueV2Repository;
        this.autoCompleteCustomRepository = autoCompleteCustomRepository;
        this.businessLogService = businessLogService;
    }

    public Set<String> getForCommonSearch(PageFilterInput<MachineDTO> filterInput) {
        Set<String> listAuto = new LinkedHashSet<>();

        //Get list object that it had the search.
        Pageable pageable = filterInput.getPageSize() == 0
            ? Pageable.unpaged()
            : PageRequest.of(filterInput.getPageNumber(), filterInput.getPageSize());
        Page<MachineEntity> resultEntity = machineCustomRepository.getAll(filterInput, pageable);

        List<KeyValueEntityV2> properties;
        if (pageable.isPaged()) properties =
            keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.MACHINE,
                resultEntity.getContent().stream().map(MachineEntity::getId).collect(Collectors.toList())
            ); else properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.MACHINE);

        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        if (properties != null && !properties.isEmpty()) {
            properties.forEach(prop -> {
                if (prop.getCommonValue().toLowerCase().contains(filterInput.getCommon().toLowerCase())) {
                    listAuto.add(prop.getCommonValue());
                }
            });
        }

        List<MachineDTO> resultList = new ArrayList<>();
        for (MachineEntity machineEntity : resultEntity.getContent()) {
            resultList.add(machineDTOMapper.toDTO(machineEntity, propertyMap.get(machineEntity.getId())));
        }

        // Compare to filter to get the list result
        if (resultList != null && !resultList.isEmpty()) {
            System.err.println("vào hàm này");
            resultList.forEach(item -> {
                if (item.getMachineCode() != null) {
                    if (item.getMachineCode().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getMachineCode()
                    );
                }

                if (item.getMachineName() != null) {
                    if (item.getMachineName().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getMachineName()
                    );
                }

                if (item.getMachineType() != null) {
                    if (
                        item.getMachineType().getMachineTypeName().toLowerCase().contains(filterInput.getCommon().toLowerCase())
                    ) listAuto.add(item.getMachineType().getMachineTypeName());
                }

                if (item.getDescription() != null) {
                    if (item.getDescription().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getDescription()
                    );
                }

                if (item.getMaxProductionQuantity() != null) {
                    if (String.valueOf(item.getMaxProductionQuantity()).contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        formatDoubleValue(item.getMaxProductionQuantity())
                    );
                }

                if (item.getMinProductionQuantity() != null) {
                    if (String.valueOf(item.getMinProductionQuantity()).contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        formatDoubleValue(item.getMinProductionQuantity())
                    );
                }

                if (item.getProductivity() != null) {
                    if (String.valueOf(item.getProductivity()).contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        formatDoubleValue(item.getProductivity())
                    );
                }

                if (item.getCycleTime() != null) {
                    if (String.valueOf(item.getCycleTime()).contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        formatDoubleValue(item.getCycleTime())
                    );
                }

                if (item.getMaintenanceTime() != null) {
                    if (String.valueOf(item.getMaintenanceTime()).contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        formatDoubleValue(item.getMaintenanceTime())
                    );
                }

                if (item.getMaxWaitingTime() != null) {
                    if (String.valueOf(item.getMaxWaitingTime()).contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        formatDoubleValue(item.getMaxWaitingTime())
                    );
                }
            });
        }
        System.err.println(listAuto);
        return listAuto;
    }

    private String formatDoubleValue(double value) {
        if (value == (int) value) {
            return String.format("%d", (int) value);
        } else {
            return String.format("%s", value);
        }
    }

    public PageResponse<List<MachineDTO>> getAllMachine(PageFilterInput<MachineDTO> filterInput) {
        Pageable pageable = filterInput.getPageSize() == 0
            ? Pageable.unpaged()
            : PageRequest.of(filterInput.getPageNumber(), filterInput.getPageSize());
        Page<MachineEntity> resultEntity = machineCustomRepository.getAll(filterInput, pageable);

        List<KeyValueEntityV2> properties;
        if (pageable.isPaged()) properties =
            keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.MACHINE,
                resultEntity.getContent().stream().map(MachineEntity::getId).collect(Collectors.toList())
            ); else properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.MACHINE);
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        List<MachineDTO> resultList = new ArrayList<>();
        for (MachineEntity machineEntity : resultEntity.getContent()) {
            resultList.add(machineDTOMapper.toDTO(machineEntity, propertyMap.get(machineEntity.getId())));
        }

        List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.MACHINE);

        return new PageResponse<List<MachineDTO>>().success().data(resultList).dataCount(resultEntity.getTotalElements()).columns(columns);
    }

    @Transactional
    public void createMachine(MachineDTO input) {
        MachineEntity checkMachineEntity = machineRepository.findByMachineCodeIgnoreCaseAndIsActiveTrue(input.getMachineCode());
        if (checkMachineEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate.machine.code", input.getMachineCode());

        MachineEntity machineEntity = machineEntityMapper.toEntity(input);
        if (input.getMachineType() != null) {
            MachineTypeEntity machineTypeEntity = machineTypeRepository.findById(input.getMachineType().getId()).orElse(null);
            machineEntity.setMachineTypeEntity(machineTypeEntity);
        }

        machineEntity = machineRepository.save(machineEntity);

        if (input.getPropertiesMap().isEmpty()) return;
        keyValueService.createUpdateKeyValueOfEntity(machineEntity.getId(), input.getPropertiesMap(), Contants.EntityType.MACHINE, false);

        businessLogService.insertInsertionLog(
            machineEntity.getId(),
            Contants.EntityType.MACHINE,
            machineDTOMapper.toLogDetail(machineEntity, input.getPropertiesMap())
        );
    }

    @Transactional
    public void updateMachine(MachineDTO input, String machineCode) {
        MachineEntity machineEntity = machineRepository.findByMachineCodeIgnoreCaseAndIsActiveTrue(machineCode);
        if (machineEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        MachineEntity oldValue = new MachineEntity(machineEntity);
        machineEntityMapper.updateFromDTO(machineEntity, input);

        if (input.getMachineType() != null) {
            MachineTypeEntity machineTypeEntity = machineTypeRepository.findById(input.getMachineType().getId()).orElse(null);
            machineEntity.setMachineTypeEntity(machineTypeEntity);
        }

        MachineEntity savedEntity = machineRepository.save(machineEntity);

        BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
            savedEntity.getId(),
            Contants.EntityType.MACHINE,
            machineDTOMapper.toUpdateLogDetail(oldValue, machineEntity)
        );

        if (input.getPropertiesMap().isEmpty()) return;
        keyValueService.createUpdateKeyValueOfEntityWithLog(
            machineEntity.getId(),
            input.getPropertiesMap(),
            Contants.EntityType.MACHINE,
            true,
            logEntity
        );
    }

    public void deleteMachine(String machineCode) {
        MachineEntity machineEntity = machineRepository.findByMachineCodeIgnoreCaseAndIsActiveTrue(machineCode);
        if (machineEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        machineEntity.setIsActive(false);
        machineRepository.save(machineEntity);

        businessLogService.insertDeleteLog(
            machineEntity.getId(),
            Contants.EntityType.MACHINE,
            machineDTOMapper.toDeletionLogDetail(machineEntity)
        );
    }

    @Transactional
    public void importExcel(MultipartFile file) throws IOException {
        MachineExcel machineExcel = readMachineFromExcel(file.getInputStream());
        Set<String> allMachineCode = machineRepository.getAllMachineCodeIn(
            machineExcel.getMachineEntities().stream().map(MachineEntity::getMachineCode).collect(Collectors.toList())
        );
        if (allMachineCode.size() != 0) {
            StringJoiner stringJoiner = new StringJoiner(",");
            machineExcel
                .getMachineEntities()
                .forEach(machineEntity -> {
                    if (allMachineCode.contains(machineEntity.getMachineCode())) {
                        stringJoiner.add(machineEntity.getMachineCode());
                    }
                });
            throw new CustomException(HttpStatus.CONFLICT, "duplicate.machine.code", stringJoiner.toString());
        }

        List<MachineEntity> machineEntities = machineRepository.saveAll(machineExcel.getMachineEntities());
        List<KeyValueEntityV2> properties = new ArrayList<>();

        Map<Integer, List<BusinessLogDetailEntity>> businessLogMap = new HashMap<>(machineEntities.size());

        for (MachineEntity machineEntity : machineEntities) {
            List<KeyValueEntityV2> keyValueEntities = machineExcel.getProperties().get(machineEntity.getMachineCode());
            if (!CollectionUtils.isEmpty(keyValueEntities)) {
                for (KeyValueEntityV2 keyValueEntity : keyValueEntities) {
                    keyValueEntity.setEntityType(Contants.EntityType.MACHINE);
                    keyValueEntity.setEntityKey(machineEntity.getId());
                    properties.add(keyValueEntity);
                }
            }
            businessLogMap.put(machineEntity.getId(), machineDTOMapper.toLogDetail(machineEntity, keyValueEntities));
        }
        keyValueV2Repository.saveAll(properties);

        businessLogService.insertInsertionLogByBatch(Contants.EntityType.MACHINE, businessLogMap);
    }

    private MachineExcel readMachineFromExcel(InputStream file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        MachineExcel machineExcel = new MachineExcel();
        List<MachineEntity> machineEntities = new ArrayList<>();
        List<MachineTypeEntity> allMachineType = machineTypeRepository.findAll();
        Map<String, MachineTypeEntity> machineTypeMap = allMachineType
            .stream()
            .collect(Collectors.toMap(machineType -> machineType.getMachineTypeName().toLowerCase(), Function.identity()));

        List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllVisiblePropertyByEntityType(Contants.EntityType.MACHINE);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            MachineEntity entity = new MachineEntity();
            for (int i = 0, cellIndex = 0; i < columns.size(); i++, cellIndex++) {
                Cell currentCell = row.getCell(cellIndex);
                //                Utils.validateSpecialCharacters(ExcelUtils.getStringCellValue(currentCell));

                switch (columns.get(i).getKeyName()) {
                    case "machineCode":
                        entity.setMachineCode(ExcelUtils.getStringCellValue(currentCell));
                        break;
                    case "machineName":
                        entity.setMachineName(ExcelUtils.getStringCellValue(currentCell));
                        break;
                    case "machineType":
                        String machineTypeName = ExcelUtils.getStringCellValue(currentCell);
                        if (machineTypeName != null) {
                            if (machineTypeMap.containsKey(machineTypeName.toLowerCase())) {
                                entity.setMachineTypeEntity(machineTypeMap.get(machineTypeName.toLowerCase()));
                            } else {
                                MachineTypeEntity machineTypeEntity = machineTypeRepository.save(new MachineTypeEntity(machineTypeName));
                                entity.setMachineTypeEntity(machineTypeEntity);
                                //                                throw new CustomException(HttpStatus.BAD_REQUEST, "unknown.type", machineTypeName);
                            }
                        }
                        break;
                    case "description":
                        entity.setDescription(ExcelUtils.getStringCellValue(currentCell));
                        break;
                    case "productivity":
                        entity.setProductivity(ExcelUtils.getNumberCellValue(currentCell));
                        break;
                    case "minProductionQuantity":
                        entity.setMinProductionQuantity(ExcelUtils.getNumberCellValue(currentCell));
                        break;
                    case "maxProductionQuantity":
                        entity.setMaxProductionQuantity(ExcelUtils.getNumberCellValue(currentCell));
                        break;
                    case "supplier":
                        entity.setSupplier(ExcelUtils.getStringCellValue(currentCell));
                        break;
                    case "maintenanceTime":
                        Double maintenanceTime = ExcelUtils.getNumberCellValue(currentCell);
                        if (maintenanceTime != null) {
                            String timeUnit = ExcelUtils.getStringCellValue(row.getCell(cellIndex + 1));
                            if (!Utils.validateTimeUnit(timeUnit)) {
                                throw new CustomException(HttpStatus.BAD_REQUEST, "unknown.time.unit", timeUnit);
                            }
                            entity.setMaintenanceTime(maintenanceTime * Contants.TIME_UNIT.get(timeUnit.toLowerCase()));
                        }
                        cellIndex++;
                        break;
                    case "purchaseDate":
                        entity.setPurchaseDate(ExcelUtils.getDateCell(currentCell));
                        break;
                    case "maxWaitingTime":
                        Double maxWaitingTime = ExcelUtils.getNumberCellValue(currentCell);
                        if (maxWaitingTime != null) {
                            String timeUnit = ExcelUtils.getStringCellValue(row.getCell(cellIndex + 1));
                            if (!Utils.validateTimeUnit(timeUnit)) {
                                throw new CustomException(HttpStatus.BAD_REQUEST, "unknown.time.unit", timeUnit);
                            }
                            entity.setMaxWaitingTime(maxWaitingTime * Contants.TIME_UNIT.get(timeUnit.toLowerCase()));
                        }
                        cellIndex++;
                        break;
                    case "cycleTime":
                        entity.setCycleTime(ExcelUtils.getNumberCellValue(currentCell));
                        break;
                    case "status":
                        String statusStr = ExcelUtils.getStringCellValue(currentCell);
                        if (!StringUtils.isEmpty(statusStr)) {
                            Integer status = Contants.MachineStatus.getStatus(statusStr);
                            if (status == null) throw new CustomException(
                                HttpStatus.BAD_REQUEST,
                                "unknow.status",
                                ExcelUtils.getStringCellValue(currentCell)
                            );
                            entity.setStatus(status);
                        }
                        break;
                    default:
                        if (ExcelUtils.isEmpty(currentCell)) continue;
                        KeyValueEntityV2 keyValueEntity = new KeyValueEntityV2();
                        String value = ExcelUtils.getStringCellValue(currentCell);
                        if (StringUtils.isEmpty(value)) continue;
                        keyValueEntity.setColumnPropertyEntity(columns.get(i));
                        keyValueEntity.setCommonValue(value);
                        int dataType = columns.get(i).getDataType();
                        try {
                            if (dataType == Contants.INT_VALUE) {
                                keyValueEntity.setIntValue(Integer.parseInt(value));
                            } else if (dataType == Contants.FLOAT_VALUE) {
                                keyValueEntity.setDoubleValue(Double.parseDouble(value));
                            } else if (dataType == Contants.STRING_VALUE) {
                                keyValueEntity.setStringValue(value);
                            } else if (dataType == Contants.JSON_VALUE) {
                                keyValueEntity.setJsonValue(value);
                            } else if (dataType == Contants.DATE_VALUE) {
                                keyValueEntity.setDateValue(LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
                            }
                        } catch (Exception e) {
                            log.error("Invalid data type", e);
                            throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", columns.get(i).getKeyTitle());
                        }
                        machineExcel.getProperties().putIfAbsent(entity.getMachineCode(), new ArrayList<>());
                        machineExcel.getProperties().get(entity.getMachineCode()).add(keyValueEntity);
                }
            }

            // Check maxProd > minProd
            if (
                entity.getMaxProductionQuantity() != null &&
                entity.getMinProductionQuantity() != null &&
                entity.getMaxProductionQuantity() < entity.getMinProductionQuantity()
            ) {
                String minProductionQuantityTitle = "";
                String maxProductionQuantityTitle = "";
                for (ColumnPropertyEntity column : columns) {
                    switch (column.getKeyName()) {
                        case "minProductionQuantity":
                            minProductionQuantityTitle = column.getKeyTitle();
                            break;
                        case "maxProductionQuantity":
                            maxProductionQuantityTitle = column.getKeyTitle();
                            break;
                    }
                }
                throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    "object.must.be.greater.than.other.at",
                    maxProductionQuantityTitle,
                    minProductionQuantityTitle,
                    String.valueOf(row.getRowNum() + 1)
                );
            }
            machineEntities.add(entity);
        }

        machineExcel.setMachineEntities(machineEntities);
        file.close();
        return machineExcel;
    }

    public List<String> getAutoComplete(String keyName, String value) {
        //        return machineCustomRepository.getAutoComplete(value, keyName);
        return autoCompleteCustomRepository.getAutoComplete(keyName, value, MachineEntity.class);
    }
}
