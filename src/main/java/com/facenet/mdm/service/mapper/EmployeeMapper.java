package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.EmployeeDTO;
import com.facenet.mdm.service.utils.Contants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public interface EmployeeMapper {
    static EmployeeDTO entityToDTO(EmployeeEntity employeeEntity) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeCode(employeeEntity.getEmployeeCode());
        employeeDTO.setEmployeeName(employeeEntity.getEmployeeName());
        if (employeeEntity.getTeamGroup() != null) {
            employeeDTO.setTeamGroup(employeeEntity.getTeamGroup().getTeamGroupName());
        }
        employeeDTO.setEmployeePhone(employeeEntity.getEmployeePhone());
        employeeDTO.setEmployeeEmail(employeeEntity.getEmployeeEmail());
        employeeDTO.setEmployeeNote(employeeEntity.getEmployeeNote());
        employeeDTO.setEmployeeStatus(employeeEntity.getEmployeeStatus());
        return employeeDTO;
    }

    static EmployeeEntity dtoToEntity(EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setEmployeeCode(employeeDTO.getEmployeeCode());
        employeeEntity.setEmployeeName(employeeDTO.getEmployeeName());
        employeeEntity.setEmployeePhone(employeeDTO.getEmployeePhone());
        employeeEntity.setEmployeeEmail(employeeDTO.getEmployeeEmail());
        employeeEntity.setEmployeeNote(employeeDTO.getEmployeeNote());
        employeeEntity.setEmployeeStatus(employeeDTO.getEmployeeStatus());
        return employeeEntity;
    }

    static EmployeeDTO entityToDTOMap(EmployeeEntity employeeEntity, List<KeyValueEntityV2> keyValueEntityV2List) {
        EmployeeDTO employeeDTO = entityToDTO(employeeEntity);
        if (CollectionUtils.isEmpty(keyValueEntityV2List)) return employeeDTO;
        for (KeyValueEntityV2 keyValueEntityV2 : keyValueEntityV2List) {
            if (StringUtils.isEmpty(keyValueEntityV2.getCommonValue())) {
                employeeDTO.getEmployeeMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
                continue;
            }
            if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.INT_VALUE) {
                employeeDTO
                    .getEmployeeMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getIntValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.FLOAT_VALUE) {
                employeeDTO
                    .getEmployeeMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDoubleValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.STRING_VALUE) {
                employeeDTO
                    .getEmployeeMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getStringValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.JSON_VALUE) {
                employeeDTO
                    .getEmployeeMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getJsonValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.DATE_VALUE) {
                employeeDTO
                    .getEmployeeMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDateValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.BOOLEAN_VALUE) {
                employeeDTO
                    .getEmployeeMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getBooleanValue()));
            } else {
                employeeDTO.getEmployeeMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
            }
        }
        return employeeDTO;
    }

    static List<BusinessLogDetailEntity> toLogDetail(EmployeeEntity employeeEntity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(employeeEntity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    static List<BusinessLogDetailEntity> toUpdateLogDetail(EmployeeEntity oldValue, EmployeeEntity newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if ("employeeId".equals(field.getName()) || "isActive".equals(field.getName())) continue;
            try {
                Field oldValueField = oldValue.getClass().getDeclaredField(field.getName());
                oldValueField.setAccessible(true);
                if (field.get(newValue) != null) {
                    BusinessLogDetailEntity businessLogDetailEntity = new BusinessLogDetailEntity()
                        .keyName(field.getName())
                        .newValue(field.get(newValue).toString());
                    if ("teamGroup".equals(field.getName())) {
                        businessLogDetailEntity =
                            new BusinessLogDetailEntity().keyName(field.getName()).newValue(newValue.getTeamGroup().getTeamGroupName());
                    }
                    if (oldValueField.get(oldValue) != null) {
                        businessLogDetailEntity.lastValue(oldValueField.get(oldValue).toString());
                        if ("teamGroup".equals(field.getName())) {
                            businessLogDetailEntity.lastValue(oldValue.getTeamGroup().getTeamGroupName());
                        }
                    }
                    result.add(businessLogDetailEntity);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    static List<BusinessLogDetailEntity> toLogDetail(EmployeeEntity employeeEntity, List<KeyValueEntityV2> keyValueEntities) {
        List<BusinessLogDetailEntity> result = toLogDetail(employeeEntity);
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

    static List<BusinessLogDetailEntity> toLogDetail(EmployeeEntity employeeEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : employeeEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (
                    field.get(employeeEntity) != null &&
                    !"teamGroup".equals(field.getName()) &&
                    !"employeeId".equals(field.getName()) &&
                    !"isActive".equals(field.getName())
                ) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(employeeEntity).toString()));
                }
                if (field.get(employeeEntity) != null && employeeEntity.getTeamGroup() != null && "teamGroup".equals(field.getName())) {
                    result.add(
                        new BusinessLogDetailEntity().keyName(field.getName()).newValue(employeeEntity.getTeamGroup().getTeamGroupName())
                    );
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    static List<BusinessLogDetailEntity> toDeletionLogDetail(EmployeeEntity employeeEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : employeeEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(employeeEntity) != null && !"employeeId".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(employeeEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
