package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.EmployeeDTO;
import com.facenet.mdm.service.dto.MerchandiseGroupDTO;
import com.facenet.mdm.service.utils.Contants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public interface MerchandiseGroupMapper {
    static MerchandiseGroupDTO convertToDTO(MerchandiseGroupEntity merchandiseGroupEntity) {
        MerchandiseGroupDTO merchandiseGroupDTO = new MerchandiseGroupDTO();
        merchandiseGroupDTO.setMerchandiseGroupCode(merchandiseGroupEntity.getMerchandiseGroupCode());
        merchandiseGroupDTO.setMerchandiseGroupName(merchandiseGroupEntity.getMerchandiseGroupName());
        merchandiseGroupDTO.setMerchandiseGroupDescription(merchandiseGroupEntity.getMerchandiseGroupDescription());
        merchandiseGroupDTO.setMerchandiseGroupNote(merchandiseGroupEntity.getMerchandiseGroupNote());
        merchandiseGroupDTO.setMerchandiseGroupStatus(merchandiseGroupEntity.getMerchandiseGroupStatus());
        return merchandiseGroupDTO;
    }

    static MerchandiseGroupEntity convertToEntity(MerchandiseGroupDTO merchandiseGroupDTO) {
        MerchandiseGroupEntity merchandiseGroupEntity = new MerchandiseGroupEntity();
        merchandiseGroupEntity.setMerchandiseGroupCode(merchandiseGroupDTO.getMerchandiseGroupCode());
        merchandiseGroupEntity.setMerchandiseGroupName(merchandiseGroupDTO.getMerchandiseGroupName());
        merchandiseGroupEntity.setMerchandiseGroupDescription(merchandiseGroupDTO.getMerchandiseGroupDescription());
        merchandiseGroupEntity.setMerchandiseGroupNote(merchandiseGroupDTO.getMerchandiseGroupNote());
        merchandiseGroupEntity.setMerchandiseGroupStatus(merchandiseGroupDTO.getMerchandiseGroupStatus());
        return merchandiseGroupEntity;
    }

    static MerchandiseGroupDTO entityToDTOMap(MerchandiseGroupEntity merchandiseGroupEntity, List<KeyValueEntityV2> keyValueEntityV2List) {
        MerchandiseGroupDTO merchandiseGroupDTO = convertToDTO(merchandiseGroupEntity);
        if (CollectionUtils.isEmpty(keyValueEntityV2List)) return merchandiseGroupDTO;
        for (KeyValueEntityV2 keyValueEntityV2 : keyValueEntityV2List) {
            if (StringUtils.isEmpty(keyValueEntityV2.getCommonValue())) {
                merchandiseGroupDTO.getPropertiesMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
                continue;
            }
            if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.INT_VALUE) {
                merchandiseGroupDTO
                    .getPropertiesMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getIntValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.FLOAT_VALUE) {
                merchandiseGroupDTO
                    .getPropertiesMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDoubleValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.STRING_VALUE) {
                merchandiseGroupDTO
                    .getPropertiesMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getStringValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.JSON_VALUE) {
                merchandiseGroupDTO
                    .getPropertiesMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getJsonValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.DATE_VALUE) {
                merchandiseGroupDTO
                    .getPropertiesMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDateValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.BOOLEAN_VALUE) {
                merchandiseGroupDTO
                    .getPropertiesMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getBooleanValue()));
            } else {
                merchandiseGroupDTO.getPropertiesMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
            }
        }
        return merchandiseGroupDTO;
    }

    static List<BusinessLogDetailEntity> toLogDetail(MerchandiseGroupEntity merchandiseGroupEntity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(merchandiseGroupEntity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    static List<BusinessLogDetailEntity> toUpdateLogDetail(MerchandiseGroupEntity oldValue, MerchandiseGroupEntity newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if ("merchandiseGroupId".equals(field.getName()) || "isActive".equals(field.getName())) continue;
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

    static List<BusinessLogDetailEntity> toLogDetail(
        MerchandiseGroupEntity merchandiseGroupEntity,
        List<KeyValueEntityV2> keyValueEntities
    ) {
        List<BusinessLogDetailEntity> result = toLogDetail(merchandiseGroupEntity);
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

    static List<BusinessLogDetailEntity> toLogDetail(MerchandiseGroupEntity merchandiseGroupEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : merchandiseGroupEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (
                    field.get(merchandiseGroupEntity) != null &&
                    !"merchandiseGroupId".equals(field.getName()) &&
                    !"isActive".equals(field.getName())
                ) {
                    result.add(
                        new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(merchandiseGroupEntity).toString())
                    );
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    static List<BusinessLogDetailEntity> toDeletionLogDetail(MerchandiseGroupEntity merchandiseGroupEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : merchandiseGroupEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (
                    field.get(merchandiseGroupEntity) != null &&
                    !"merchandiseGroupId".equals(field.getName()) &&
                    !"isActive".equals(field.getName())
                ) {
                    result.add(
                        new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(merchandiseGroupEntity).toString())
                    );
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
