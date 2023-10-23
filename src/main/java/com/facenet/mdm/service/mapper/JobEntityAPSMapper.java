package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.service.dto.JobDTOAPS;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface JobEntityAPSMapper extends EntityMapper<JobDTOAPS, JobEntity> {}
