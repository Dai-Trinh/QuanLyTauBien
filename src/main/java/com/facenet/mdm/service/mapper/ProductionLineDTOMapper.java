package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.dto.ProductionLineDTO;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class ProductionLineDTOMapper {

    private final ProductionLineEntityMapper ProductionLineEntityMapper;

    public ProductionLineDTOMapper(com.facenet.mdm.service.mapper.ProductionLineEntityMapper productionLineEntityMapper) {
        ProductionLineEntityMapper = productionLineEntityMapper;
    }

    public ProductionLineDTO toDTO(ProductionLineEntity productionLineEntity, List<KeyValueEntityV2> properties) {
        ProductionLineDTO productionLineDTO = ProductionLineEntityMapper.toDto(productionLineEntity);
        if (CollectionUtils.isEmpty(properties)) return productionLineDTO;

        Map<String, String> propertiesMap = productionLineDTO.getPropertiesMap();
        for (KeyValueEntityV2 machineProperty : properties) {
            if (machineProperty.getBooleanValue() != null) {
                propertiesMap.put(
                    machineProperty.getColumnPropertyEntity().getKeyName(),
                    String.valueOf(machineProperty.getBooleanValue())
                );
            }
            if (machineProperty.getIntValue() != null) {
                propertiesMap.put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getIntValue()));
            }
            if (machineProperty.getDoubleValue() != null) {
                propertiesMap.put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getDoubleValue()));
            }

            if (machineProperty.getStringValue() != null) {
                propertiesMap.put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getStringValue()));
            }

            if (machineProperty.getJsonValue() != null) {
                propertiesMap.put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getJsonValue()));
            }

            if (machineProperty.getDateValue() != null) {
                propertiesMap.put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getDateValue()));
            }
        }
        return productionLineDTO;
    }

    public List<BusinessLogDetailEntity> toUpdateLogDetail(ProductionLineEntity oldValue, ProductionLineEntity newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if ("productionLineId".equals(field.getName()) || "isActive".equals(field.getName())) continue;
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
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public List<BusinessLogDetailEntity> toLogDetail(ProductionLineEntity productionLineEntity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(productionLineEntity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    public List<BusinessLogDetailEntity> toLogDetail(ProductionLineEntity productionLineEntity, List<KeyValueEntityV2> keyValueEntities) {
        List<BusinessLogDetailEntity> result = toLogDetail(productionLineEntity);
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

    public List<BusinessLogDetailEntity> toLogDetail(ProductionLineEntity productionLineEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : productionLineEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (
                    field.get(productionLineEntity) != null &&
                    !"productionLineId".equals(field.getName()) &&
                    !"isActive".equals(field.getName())
                ) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(productionLineEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public List<BusinessLogDetailEntity> toDeletionLogDetail(ProductionLineEntity productionLineEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : productionLineEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (
                    field.get(productionLineEntity) != null &&
                    !"productionLineId".equals(field.getName()) &&
                    !"isActive".equals(field.getName())
                ) {
                    result.add(
                        new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(productionLineEntity).toString())
                    );
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
