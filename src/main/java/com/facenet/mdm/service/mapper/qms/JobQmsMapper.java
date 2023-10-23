package com.facenet.mdm.service.mapper.qms;

import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.service.dto.JobQmsDTO;
import com.facenet.mdm.service.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface JobQmsMapper extends EntityMapper<JobQmsDTO, ProductionStageEntity> {
    @Mapping(target = "id", source = "jobEntity.id")
    @Mapping(target = "jobCode", source = "jobEntity.jobCode")
    JobQmsDTO toDto(JobEntity jobEntity);
}
