package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.CoittEntity;
import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.JobDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CoittEntityMapper extends EntityMapper<CoittDTO, CoittEntity>{
    @Mapping(target = "productCode", ignore = true)
    void updateFromDTO(@MappingTarget CoittEntity coittEntity, CoittDTO coittDTO);
}
