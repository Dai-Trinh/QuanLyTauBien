package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.BusinessLogDetailEntity;
import com.facenet.mdm.domain.KeyValueEntity;
import com.facenet.mdm.domain.KeyValueEntityV2;
import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.exception.CustomException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class MachineDTOMapper {

    private static final Logger log = LoggerFactory.getLogger(MachineDTOMapper.class);
    private final MachineEntityMapper machineEntityMapper;

    public MachineDTOMapper(MachineEntityMapper machineEntityMapper) {
        this.machineEntityMapper = machineEntityMapper;
    }

    public MachineDTO toDTO(MachineEntity machineEntity, List<KeyValueEntityV2> machineProperties) {
        MachineDTO machineDTO = machineEntityMapper.toDto(machineEntity);
        if (CollectionUtils.isEmpty(machineProperties)) return machineDTO;

        for (KeyValueEntityV2 machineProperty : machineProperties) {
            if (machineProperty.getBooleanValue() != null) {
                machineDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getBooleanValue()));
            }
            if (machineProperty.getIntValue() != null) {
                machineDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getIntValue()));
            }
            if (machineProperty.getDoubleValue() != null) {
                machineDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getDoubleValue()));
            }

            if (machineProperty.getStringValue() != null) {
                machineDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getStringValue()));
            }

            if (machineProperty.getJsonValue() != null) {
                machineDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getJsonValue()));
            }

            if (machineProperty.getDateValue() != null) {
                machineDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getDateValue()));
            }
        }
        return machineDTO;
    }

    public List<BusinessLogDetailEntity> toLogDetail(MachineEntity machineEntity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(machineEntity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    public List<BusinessLogDetailEntity> toUpdateLogDetail(MachineEntity oldValue, MachineEntity newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if ("id".equals(field.getName()) || "isActive".equals(field.getName())) continue;
            try {
                Field oldValueField = oldValue.getClass().getDeclaredField(field.getName());
                oldValueField.setAccessible(true);
                if (field.get(newValue) != null) {
                    BusinessLogDetailEntity businessLogDetailEntity = new BusinessLogDetailEntity()
                        .keyName(field.getName())
                        .newValue(field.get(newValue).toString());
                    if (oldValueField.get(oldValue) != null) {
                        businessLogDetailEntity.lastValue(oldValueField.get(oldValue).toString());
                    }
                    result.add(businessLogDetailEntity);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                log.error("Error when reading reflection", e);
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public List<BusinessLogDetailEntity> toLogDetail(MachineEntity machineEntity, List<KeyValueEntityV2> keyValueEntities) {
        List<BusinessLogDetailEntity> result = toLogDetail(machineEntity);
        if (CollectionUtils.isEmpty(keyValueEntities)) return result;

        keyValueEntities.forEach(keyValueEntityV2 ->
            result.add(
                new BusinessLogDetailEntity()
                    .keyName(keyValueEntityV2.getColumnPropertyEntity().getKeyName())
                    .newValue(keyValueEntityV2.getCommonValue())
            )
        );
        return result;
    }

    public List<BusinessLogDetailEntity> toLogDetail(MachineEntity machineEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : machineEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(machineEntity) != null && !"id".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(machineEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public List<BusinessLogDetailEntity> toDeletionLogDetail(MachineEntity machineEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : machineEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(machineEntity) != null && !"id".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(machineEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
