package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.domain.VendorEntity;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.VendorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface VendorEntityMapper extends EntityMapper<VendorDTO, VendorEntity> {
    @Mapping(target = "vendorCode", ignore = true)
    void updateFromDTO(@MappingTarget VendorEntity vendorEntity, VendorDTO vendorDTO);
//    public default void updateFromVendorDTO(VendorEntity vendorEntity, VendorDTO dto){
//        vendorEntity.setVendorName( dto.getVendorName() );
//        vendorEntity.setOtherName( dto.getOtherName() );
//        vendorEntity.setStatus( dto.getStatus() );
//        vendorEntity.setTaxCode( dto.getTaxCode() );
//        vendorEntity.setCurrency( dto.getCurrency() );
//        vendorEntity.setPhone( dto.getPhone() );
//        vendorEntity.setEmail( dto.getEmail() );
//        vendorEntity.setFaxCode( dto.getFaxCode() );
//        vendorEntity.setAddress( dto.getAddress() );
//        vendorEntity.setContactId( dto.getContactId() );
//        vendorEntity.setContactName( dto.getContactName() );
//        vendorEntity.setContactPosition( dto.getContactPosition() );
//        vendorEntity.setContactTitle( dto.getContactTitle() );
//        vendorEntity.setContactGender( dto.getContactGender() );
//        vendorEntity.setContactPhone( dto.getContactPhone() );
//        vendorEntity.setContactEmail( dto.getContactEmail() );
//        vendorEntity.setContactAddress( dto.getContactAddress() );
//        vendorEntity.setContactNationality( dto.getContactNationality() );
//        vendorEntity.setContactBirthDate( dto.getContactBirthDate() );
//    }
}
