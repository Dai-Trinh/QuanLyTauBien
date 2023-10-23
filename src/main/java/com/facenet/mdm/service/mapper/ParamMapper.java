package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.Param;
import com.facenet.mdm.service.dto.ParamDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ParamMapper {
    ParamDto toDto(Param param);

    List<ParamDto> toDtoList(List<Param> params);
}
