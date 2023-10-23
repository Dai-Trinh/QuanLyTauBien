package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.domain.VendorEntity;
import com.facenet.mdm.service.dto.JobDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.VendorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface JobEntityMapper extends EntityMapper<JobDTO, JobEntity>{
    @Mapping(target = "id", source = "jobDTO.id")
    @Mapping(target = "jobCode", ignore = true)
    void updateFromDTO(@MappingTarget JobEntity jobEntity, JobDTO jobDTO);
}
