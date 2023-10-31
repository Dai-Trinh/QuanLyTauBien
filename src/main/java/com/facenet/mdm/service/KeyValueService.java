package com.facenet.mdm.service;

import com.facenet.mdm.domain.BusinessLogDetailEntity;
import com.facenet.mdm.domain.BusinessLogEntity;
import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.KeyValueEntityV2;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.KeyValueV2Repository;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.utils.Contants;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class KeyValueService {

    private static final Logger log = LoggerFactory.getLogger(KeyValueService.class);
    private final ColumnPropertyRepository columnPropertyRepository;
    private final KeyValueV2Repository keyValueV2Repository;
    private final BusinessLogService businessLogService;

    public KeyValueService(
        ColumnPropertyRepository columnPropertyRepository,
        KeyValueV2Repository keyValueV2Repository,
        BusinessLogService businessLogService
    ) {
        this.columnPropertyRepository = columnPropertyRepository;
        this.keyValueV2Repository = keyValueV2Repository;
        this.businessLogService = businessLogService;
    }

    public void createUpdateKeyValueOfEntity(Integer entityId, Map<String, String> inputMap, int entityType, boolean isUpdate) {
        createUpdateKeyValueOfEntityWithLog(entityId, inputMap, entityType, isUpdate, null);
    }

    public void createUpdateKeyValueOfEntityWithLog(
        Integer entityId,
        Map<String, String> inputMap,
        int entityType,
        boolean isUpdate,
        BusinessLogEntity ownerLogEntity
    ) {
        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(entityType);
        Map<String, ColumnPropertyEntity> keyNameColumnMap = columnPropertyEntities
            .stream()
            .collect(Collectors.toMap(ColumnPropertyEntity::getKeyName, Function.identity()));

        List<KeyValueEntityV2> insertList = new ArrayList<>();

        List<BusinessLogDetailEntity> logEntities = new ArrayList<>();

        for (String keyName : inputMap.keySet()) {
            BusinessLogDetailEntity businessLogDetailEntity = new BusinessLogDetailEntity();
            KeyValueEntityV2 keyValueEntity = null;
            if (isUpdate) {
                keyValueEntity = keyValueV2Repository.findByEntityKeyAndColumnPropertyEntity(entityId, keyNameColumnMap.get(keyName));
                businessLogDetailEntity.setLastValue(keyValueEntity == null ? null : keyValueEntity.getCommonValue());
            }
            if (keyValueEntity == null) {
                keyValueEntity = new KeyValueEntityV2();
                keyValueEntity.setEntityType(entityType);
                keyValueEntity.setColumnPropertyEntity(keyNameColumnMap.get(keyName));
                keyValueEntity.setEntityKey(entityId);
            }
            if (StringUtils.isEmpty(inputMap.get(keyName))) continue;
            int dataType = keyNameColumnMap.get(keyName).getDataType();
            try {
                if ((inputMap.get(keyName) == null)) {
                    keyValueEntity.setCommonValue(null);
                } else {
                    keyValueEntity.setCommonValue(inputMap.get(keyName));
                }
                switch (dataType) {
                    case Contants.INT_VALUE:
                        keyValueEntity.setIntValue(Integer.parseInt(inputMap.get(keyName)));
                        break;
                    case Contants.FLOAT_VALUE:
                        keyValueEntity.setDoubleValue(Double.parseDouble(inputMap.get(keyName)));
                        break;
                    case Contants.STRING_VALUE:
                        keyValueEntity.setStringValue(inputMap.get(keyName));
                        break;
                    case Contants.JSON_VALUE:
                        keyValueEntity.setJsonValue(inputMap.get(keyName));
                        break;
                    case Contants.DATE_VALUE:
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

                        // Chuyển đổi chuỗi thành đối tượng Instant
                        Instant instant = Instant.from(formatter.parse(inputMap.get(keyName)));
                        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                        // Chuyển đổi từ ZonedDateTime thành LocalDate
                        LocalDate localDate = zonedDateTime.toLocalDate();
                        keyValueEntity.setDateValue(localDate);
                        break;
                    default:
                        throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", keyNameColumnMap.get(keyName).getKeyTitle());
                }

                // Set log
                businessLogDetailEntity.setNewValue(keyValueEntity.getCommonValue());
                businessLogDetailEntity.setKeyName(keyName);
                logEntities.add(businessLogDetailEntity);
            } catch (Exception e) {
                //                log.error("Invalid data type", e);
                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", keyNameColumnMap.get(keyName).getKeyTitle());
            }
            insertList.add(keyValueEntity);
        }
        keyValueV2Repository.saveAll(insertList);

        if (isUpdate && ownerLogEntity != null) businessLogService.insertUpdateDynamicPropertiesLog(ownerLogEntity, logEntities);
    }

    public List<KeyValueEntityV2> createOrUpdateKeyValueEntity(Integer entityId, Map<String, String> inputMap, int entityType) {
        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.findByKeyNameInAndEntityTypeAndIsActiveTrue(
            inputMap.keySet(),
            entityType
        );
        //        List<KeyDictionaryEntity> keyDictionaryEntities = keyDictionaryRepository.findByKeyNameIn(inputMap.keySet());
        //        Map<String, KeyDictionaryEntity> keyNameIdMap = keyDictionaryEntities
        //            .stream()
        //            .collect(Collectors.toMap(KeyDictionaryEntity::getKeyName, Function.identity()));
        Map<String, ColumnPropertyEntity> keyNameColumnMap = columnPropertyEntities
            .stream()
            .collect(Collectors.toMap(ColumnPropertyEntity::getKeyName, Function.identity()));
        List<KeyValueEntityV2> keyValueEntities = new ArrayList<>();
        for (String keyName : inputMap.keySet()) {
            KeyValueEntityV2 keyValueEntity = keyValueV2Repository.findByEntityKeyAndColumnPropertyEntity(
                entityId,
                keyNameColumnMap.get(keyName)
            );

            if (keyValueEntity == null) {
                keyValueEntity = new KeyValueEntityV2();
                keyValueEntity.setEntityType(entityType);
                keyValueEntity.setEntityKey(entityId);
                keyValueEntity.setColumnPropertyEntity(keyNameColumnMap.get(keyName));
            }

            int dataType = keyNameColumnMap.get(keyName).getDataType();
            //            System.err.println(inputMap.get(keyName) + "----" + dataType);
            try {
                if ((inputMap.get(keyName) == null)) {
                    keyValueEntity.setCommonValue(null);
                } else {
                    keyValueEntity.setCommonValue(inputMap.get(keyName));
                }
                if (dataType == Contants.INT_VALUE) {
                    keyValueEntity.setIntValue(Integer.parseInt(inputMap.get(keyName)));
                } else if (dataType == Contants.FLOAT_VALUE) {
                    keyValueEntity.setDoubleValue(Double.parseDouble(inputMap.get(keyName)));
                } else if (dataType == Contants.STRING_VALUE) {
                    keyValueEntity.setStringValue(inputMap.get(keyName));
                } else if (dataType == Contants.JSON_VALUE) {
                    keyValueEntity.setJsonValue(inputMap.get(keyName));
                } else if (dataType == Contants.DATE_VALUE) {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

                    // Chuyển đổi chuỗi thành đối tượng Instant
                    Instant instant = Instant.from(formatter.parse(inputMap.get(keyName)));
                    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

                    // Chuyển đổi từ ZonedDateTime thành LocalDate
                    LocalDate localDate = zonedDateTime.toLocalDate();
                    keyValueEntity.setDateValue(localDate);
                }
            } catch (Exception e) {
                log.error("Invalid data type", e);
                throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.datatype", keyNameColumnMap.get(keyName).getKeyTitle());
            }

            keyValueEntities.add(keyValueEntity);
        }
        return keyValueEntities;
    }
}
