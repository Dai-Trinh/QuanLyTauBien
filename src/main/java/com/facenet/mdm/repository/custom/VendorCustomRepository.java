package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.domain.VendorEntity;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.VendorDTO;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VendorCustomRepository {
    PageResponse<List<VendorEntity>> getAllVendor(PageFilterInput<VendorDTO> input, Pageable pageable);

}
