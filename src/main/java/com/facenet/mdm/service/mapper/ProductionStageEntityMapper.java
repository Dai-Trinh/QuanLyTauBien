package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ProductionStageEntityMapper extends EntityMapper<ProductionStageDTO, ProductionStageEntity> {

}
