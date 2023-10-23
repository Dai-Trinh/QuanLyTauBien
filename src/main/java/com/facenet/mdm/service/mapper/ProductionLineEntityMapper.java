package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.ProductionLineEntity;
import com.facenet.mdm.service.dto.ProductionLineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ProductionLineEntityMapper extends EntityMapper<ProductionLineDTO, ProductionLineEntity> {
    @Mapping(target = "productionLineCode", ignore = true)
    @Mapping(target = "productionLineType", ignore = true)
    void updateFromDTO(@MappingTarget ProductionLineEntity machineEntity, ProductionLineDTO machineDTO);

    @Mapping(target = "productionLineType", source = "productionLineType")
    ProductionLineDTO toDto(ProductionLineEntity productionLineEntity);
}
