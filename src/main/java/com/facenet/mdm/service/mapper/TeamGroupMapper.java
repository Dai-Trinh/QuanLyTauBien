package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.EmployeeDTO;
import com.facenet.mdm.service.dto.TeamGroupDTO;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.utils.Contants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public interface TeamGroupMapper {
    static TeamGroupDTO entityToDTO(TeamGroupEntity teamGroupEntity) {
        TeamGroupDTO teamGroupDTO = new TeamGroupDTO();
        teamGroupDTO.setTeamGroupCode(teamGroupEntity.getTeamGroupCode());
        teamGroupDTO.setTeamGroupName(teamGroupEntity.getTeamGroupName());
        if (teamGroupEntity.getTeamGroupQuota() == null) {
            teamGroupDTO.setTeamGroupQuota(null);
        } else {
            teamGroupDTO.setTeamGroupQuota(teamGroupEntity.getTeamGroupQuota().toString());
        }
        teamGroupDTO.setTeamGroupNote(teamGroupEntity.getTeamGroupNote());
        teamGroupDTO.setNumberOfEmployee(teamGroupEntity.getEmployeeEntitySet().size());
        teamGroupDTO.setTeamGroupStatus(teamGroupEntity.getTeamGroupStatus());
        return teamGroupDTO;
    }

    static TeamGroupEntity dtoToEntity(TeamGroupDTO teamGroupDTO) {
        TeamGroupEntity teamGroupEntity = new TeamGroupEntity();
        teamGroupEntity.setTeamGroupCode(teamGroupDTO.getTeamGroupCode());
        teamGroupEntity.setTeamGroupName(teamGroupDTO.getTeamGroupName());
        if (StringUtils.isEmpty(teamGroupDTO.getTeamGroupQuota())) {
            teamGroupEntity.setTeamGroupQuota(null);
        } else {
            try {
                teamGroupEntity.setTeamGroupQuota(Integer.parseInt(teamGroupDTO.getTeamGroupQuota()));
            } catch (Exception exception) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
            }
        }
        teamGroupEntity.setTeamGroupNote(teamGroupDTO.getTeamGroupNote());
        teamGroupEntity.setTeamGroupStatus(teamGroupDTO.getTeamGroupStatus());
        return teamGroupEntity;
    }

    static TeamGroupDTO entityToDTOMap(TeamGroupEntity teamGroupEntity, List<KeyValueEntityV2> keyValueEntityV2List) {
        TeamGroupDTO teamGroupDTO = entityToDTO(teamGroupEntity);
        if (CollectionUtils.isEmpty(keyValueEntityV2List)) return teamGroupDTO;
        for (KeyValueEntityV2 keyValueEntityV2 : keyValueEntityV2List) {
            if (StringUtils.isEmpty(keyValueEntityV2.getCommonValue())) {
                teamGroupDTO.getTeamGroupMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
                continue;
            }
            if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.INT_VALUE) {
                teamGroupDTO
                    .getTeamGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getIntValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.FLOAT_VALUE) {
                teamGroupDTO
                    .getTeamGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDoubleValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.STRING_VALUE) {
                teamGroupDTO
                    .getTeamGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getStringValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.JSON_VALUE) {
                teamGroupDTO
                    .getTeamGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getJsonValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.DATE_VALUE) {
                teamGroupDTO
                    .getTeamGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getDateValue()));
            } else if (keyValueEntityV2.getColumnPropertyEntity().getDataType() == Contants.BOOLEAN_VALUE) {
                teamGroupDTO
                    .getTeamGroupMap()
                    .put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), String.valueOf(keyValueEntityV2.getBooleanValue()));
            } else {
                teamGroupDTO.getTeamGroupMap().put(keyValueEntityV2.getColumnPropertyEntity().getKeyName(), null);
            }
        }
        return teamGroupDTO;
    }

    static List<BusinessLogDetailEntity> toLogDetail(TeamGroupEntity teamGroupEntity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(teamGroupEntity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    static List<BusinessLogDetailEntity> toUpdateLogDetail(TeamGroupEntity oldValue, TeamGroupEntity newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if ("teamGroupId".equals(field.getName()) || "isActive".equals(field.getName())) continue;
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

    static List<BusinessLogDetailEntity> toLogDetail(TeamGroupEntity teamGroupEntity, List<KeyValueEntityV2> keyValueEntities) {
        List<BusinessLogDetailEntity> result = toLogDetail(teamGroupEntity);
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

    static List<BusinessLogDetailEntity> toLogDetail(TeamGroupEntity teamGroupEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : teamGroupEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(teamGroupEntity) != null && !"teamGroupId".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(teamGroupEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    static List<BusinessLogDetailEntity> toDeletionLogDetail(TeamGroupEntity teamGroupEntity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : teamGroupEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(teamGroupEntity) != null && !"teamGroupId".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(teamGroupEntity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
