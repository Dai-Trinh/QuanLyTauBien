package com.facenet.mdm.service.mapper;

import com.facenet.mdm.config.Constants;
import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.ErrorDTO;
import com.facenet.mdm.service.dto.ErrorGroupDTO;
import com.facenet.mdm.service.utils.Contants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public interface ErrorGroupMapper {
    static ErrorGroupDTO entityToDTO(ErrorGroupEntity errorGroupEntity) {
        ErrorGroupDTO errorGroupDTO = new ErrorGroupDTO();
        errorGroupDTO.setErrorGroupCode(errorGroupEntity.getErrorGroupCode());
        errorGroupDTO.setErrorGroupName(errorGroupEntity.getErrorGroupName());
        errorGroupDTO.setErrorGroupDesc(errorGroupEntity.getErrorGroupDesc());
        errorGroupDTO.setErrorGroupType(errorGroupEntity.getErrorGroupType());
        if (errorGroupEntity.getErrorGroupStatus() == 0) {
            errorGroupDTO.setErrorGroupStatus("Ngừng hoạt động");
        } else if (errorGroupEntity.getErrorGroupStatus() == 1) {
            errorGroupDTO.setErrorGroupStatus("Hoạt động");
        } else {
            errorGroupDTO.setErrorGroupStatus("Đang chờ xử lý");
        }
        return errorGroupDTO;
    }

    static ErrorGroupEntity dtoToEntity(ErrorGroupDTO errorGroupDTO) {
        ErrorGroupEntity errorGroupEntity = new ErrorGroupEntity();
        errorGroupEntity.setErrorGroupCode(errorGroupEntity.getErrorGroupCode());
        errorGroupEntity.setErrorGroupName(errorGroupEntity.getErrorGroupName());
        errorGroupEntity.setErrorGroupDesc(errorGroupEntity.getErrorGroupDesc());
        errorGroupEntity.setErrorGroupType(errorGroupEntity.getErrorGroupType());
        if (errorGroupDTO.getErrorGroupStatus().equals("Hoạt động")) {
            errorGroupEntity.setErrorGroupStatus(1);
        } else if (errorGroupDTO.getErrorGroupStatus().equals("Ngừng hoạt động")) {
            errorGroupEntity.setErrorGroupStatus(0);
        } else {
            errorGroupEntity.setErrorGroupStatus(2);
        }
        return errorGroupEntity;
    }

    static ErrorGroupDTO entytoDTOMap(ErrorGroupEntity entity, List<KeyValueEntityV2> keyValueEntityV2List) {
        ErrorGroupDTO errorGroupDTO = entityToDTO(entity);
        if (CollectionUtils.isEmpty(keyValueEntityV2List)) return errorGroupDTO;
        for (KeyValueEntityV2 keyValueEntityV2 : keyValueEntityV2List) {
            if (StringUtils.isEmpty(keyValueEntityV2.getCommonValue())) {
                errorGroupDTO.getErrorGroupMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
                continue;
            }
            if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.INT_VALUE) {
                errorGroupDTO
                    .getErrorGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getIntValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.FLOAT_VALUE) {
                errorGroupDTO
                    .getErrorGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDoubleValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.STRING_VALUE) {
                errorGroupDTO
                    .getErrorGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getStringValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.JSON_VALUE) {
                errorGroupDTO
                    .getErrorGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getJsonValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.DATE_VALUE) {
                errorGroupDTO
                    .getErrorGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDateValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.BOOLEAN_VALUE) {
                errorGroupDTO
                    .getErrorGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getBooleanValue()));
            } else {
                errorGroupDTO.getErrorGroupMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
            }
        }
        return errorGroupDTO;
    }

    static List<BusinessLogDetailEntity> toLogDetail(ErrorGroupEntity entity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(entity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    static List<BusinessLogDetailEntity> toUpdateLogDetail(ErrorGroupEntity oldValue, ErrorGroupEntity newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (
                "errorGroupId".equals(field.getName()) || "isActive".equals(field.getName()) || "errorEntities".equals(field.getName())
            ) continue;
            try {
                Field oldValueField = oldValue.getClass().getDeclaredField(field.getName());

                oldValueField.setAccessible(true);
                if (field.get(newValue) != null) {
                    //System.err.println(field.getName());
                    BusinessLogDetailEntity businessLogDetailEntity = new BusinessLogDetailEntity()
                        .keyName(field.getName())
                        .newValue(field.get(newValue).toString());

                    if (oldValueField.get(oldValue) != null) {
                        //System.err.println(oldValueField.get(oldValue).toString());

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

    static List<BusinessLogDetailEntity> toLogDetail(ErrorGroupEntity errorGroupEntity, List<KeyValueEntityV2> keyValueEntities) {
        List<BusinessLogDetailEntity> result = toLogDetail(errorGroupEntity);
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

    static List<BusinessLogDetailEntity> toLogDetail(ErrorGroupEntity errorEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : errorEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(errorEntity) != null && !"errorGroupId".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(errorEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    static List<BusinessLogDetailEntity> toDeletionLogDetail(ErrorGroupEntity errorEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : errorEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(errorEntity) != null && !"errorGroupId".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(errorEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
