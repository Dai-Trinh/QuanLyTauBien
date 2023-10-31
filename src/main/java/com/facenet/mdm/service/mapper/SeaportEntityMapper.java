package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.KeyValueEntityV2;
import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.domain.SeaportEntity;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.dto.SeaportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SeaportEntityMapper extends EntityMapper<SeaportDTO, SeaportEntity>{


    @Mapping(target = "seaportCode", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateFromDTO(@MappingTarget SeaportEntity seaportEntity, SeaportDTO seaportDTO);


}
