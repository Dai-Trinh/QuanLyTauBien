package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.service.dto.MachineDTO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MachineEntityMapper extends EntityMapper<MachineDTO, MachineEntity> {
    @Mapping(target = "machineCode", ignore = true)
    void updateFromDTO(@MappingTarget MachineEntity machineEntity, MachineDTO machineDTO);

    MachineDTO toDto(MachineEntity machineEntity);
}
