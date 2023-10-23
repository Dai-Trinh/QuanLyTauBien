package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.CoittEntity;
import com.facenet.mdm.domain.CustomerEntity;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CustomerEntityMapper extends EntityMapper<CustomerDTO, CustomerEntity> {
    @Mapping(target = "customerCode", ignore = true)
    void updateFromDTO(@MappingTarget CustomerEntity customerEntity, CustomerDTO customerDTO);
}
