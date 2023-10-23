package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.CoittEntity;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.DataItemInVendor;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoittCustomRepository {
    Page<CoittEntity> getAll(PageFilterInput<CoittDTO> input, Pageable pageable);
    PageResponse<List<DataItemInVendor>> getAllItemAlongVendor(
        PageFilterInput<DataItemInVendor> input,
        String vendorCode,
        Pageable pageable
    );

    Page<CoittEntity> getAllBom(PageFilterInput<CoittDTO> input, Pageable pageable, List<String> coittCodes);

    Page<CoittEntity> getAllBomChild(PageFilterInput<CoittDTO> input, Pageable pageable, List<String> coittCodes);
}
