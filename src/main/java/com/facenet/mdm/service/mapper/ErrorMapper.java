package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.ErrorDTO;
import com.facenet.mdm.service.utils.Contants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public interface ErrorMapper {
    static ErrorDTO entityToDTO(ErrorEntity entity) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setErrorCode(entity.getErrorCode());
        errorDTO.setErrorName(entity.getErrorName());
        errorDTO.setErrorDesc(entity.getErrorDesc());
        errorDTO.setErrorType(entity.getErrorType());
        if (entity.getErrorStatus() == 0) {
            errorDTO.setErrorStatus("Ngừng hoạt động");
        } else if (entity.getErrorStatus() == 1) {
            errorDTO.setErrorStatus("Hoạt động");
        } else {
            errorDTO.setErrorStatus("Đang chờ xử lý");
        }
        return errorDTO;
    }

    static ErrorDTO entytoDTOMap(ErrorEntity entity, List<KeyValueEntityV2> keyValueEntityV2List, List<String> errorGroup) {
        ErrorDTO errorDTO = entityToDTO(entity);
        if (!CollectionUtils.isEmpty(errorGroup)) errorDTO.setErrorGroup(errorGroup);
        if (CollectionUtils.isEmpty(keyValueEntityV2List)) return errorDTO;
        for (KeyValueEntityV2 keyValueEntityV2 : keyValueEntityV2List) {
            if (StringUtils.isEmpty(keyValueEntityV2.getCommonValue())) {
                errorDTO.getErrorMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
                continue;
            }
            if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.INT_VALUE) {
                errorDTO
                    .getErrorMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getIntValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.FLOAT_VALUE) {
                errorDTO
                    .getErrorMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDoubleValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.STRING_VALUE) {
                errorDTO
                    .getErrorMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getStringValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.JSON_VALUE) {
                errorDTO
                    .getErrorMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getJsonValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.DATE_VALUE) {
                errorDTO
                    .getErrorMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDateValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.BOOLEAN_VALUE) {
                errorDTO
                    .getErrorMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getBooleanValue()));
            } else {
                errorDTO.getErrorMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
            }
        }
        return errorDTO;
    }

    static List<BusinessLogDetailEntity> toLogDetail(ErrorEntity entity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(entity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    static List<BusinessLogDetailEntity> toUpdateLogDetail(ErrorEntity oldValue, ErrorEntity newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (
                "errorId".equals(field.getName()) || "isActive".equals(field.getName()) || "errorGroupEntities".equals((field.getName()))
            ) continue;
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

    static List<BusinessLogDetailEntity> toLogDetail(ErrorEntity errorEntity, List<KeyValueEntityV2> keyValueEntities) {
        List<BusinessLogDetailEntity> result = toLogDetail(errorEntity);
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

    static List<BusinessLogDetailEntity> toLogDetail(ErrorEntity errorEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : errorEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(errorEntity) != null && !"errorId".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(errorEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    static List<BusinessLogDetailEntity> toDeletionLogDetail(ErrorEntity errorEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : errorEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(errorEntity) != null && !"errorId".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(errorEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
