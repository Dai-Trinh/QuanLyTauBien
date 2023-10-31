package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class DTOMapper {

    private static final Logger log = LoggerFactory.getLogger(DTOMapper.class);

    @Autowired
    SeaportEntityMapper seaportEntityMapper;


    public SeaportDTO toDTO(SeaportEntity seaportEntity, List<KeyValueEntityV2> columnProperties){
        SeaportDTO seaportDTO = seaportEntityMapper.toDto(seaportEntity);
        if (CollectionUtils.isEmpty(columnProperties)) return seaportDTO;

        for (KeyValueEntityV2 machineProperty : columnProperties) {
            if (machineProperty.getBooleanValue() != null) {
                seaportDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getBooleanValue()));
            }
            if (machineProperty.getIntValue() != null) {
                seaportDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getIntValue()));
            }
            if (machineProperty.getDoubleValue() != null) {
                seaportDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getDoubleValue()));
            }

            if (machineProperty.getStringValue() != null) {
                seaportDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getStringValue()));
            }

            if (machineProperty.getJsonValue() != null) {
                seaportDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getJsonValue()));
            }

            if (machineProperty.getDateValue() != null) {
                seaportDTO
                    .getPropertiesMap()
                    .put(machineProperty.getColumnPropertyEntity().getKeyName(), String.valueOf(machineProperty.getDateValue()));
            }
        }
        return seaportDTO;
    }

}
