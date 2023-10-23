package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.LeadTimeEntity;
import com.facenet.mdm.service.dto.MqqLeadTimeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface LeadTimeMapper {

    @Mapping(target = "id", source = "dto.leadTimeId")
    @Mapping(target = "vendorCode", source = "vendorCode")
    @Mapping(target = "itemCode", source = "itemCode")
    @Mapping(target = "leadTime", source = "dto.leadTime")
    @Mapping(target = "note", source = "dto.note")
    @Mapping(target = "timeEnd", source = "dto.dueDate")
    LeadTimeEntity dtoToEntity(String vendorCode, String itemCode, MqqLeadTimeDTO dto);

}
