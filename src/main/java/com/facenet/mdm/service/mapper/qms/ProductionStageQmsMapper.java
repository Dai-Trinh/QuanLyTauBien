package com.facenet.mdm.service.mapper.qms;

import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.service.dto.StageQmsDTO;
import com.facenet.mdm.service.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ProductionStageQmsMapper extends EntityMapper<StageQmsDTO, ProductionStageEntity> {
    @Mapping(target = "id", source = "productionStageEntity.id")
    @Mapping(target = "productionStageCode", source = "productionStageEntity.productionStageCode")
    StageQmsDTO toDto(ProductionStageEntity productionStageEntity);

}
