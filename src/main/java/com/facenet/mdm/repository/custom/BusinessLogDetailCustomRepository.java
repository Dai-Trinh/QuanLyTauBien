package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.BusinessLogDetailEntity;
import com.facenet.mdm.domain.BusinessLogEntity;
import com.facenet.mdm.service.dto.BusinessLogDetailDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusinessLogDetailCustomRepository {
    Page<BusinessLogDetailDTO> getAllByBusinessLogEntity(
        BusinessLogEntity businessLogEntity,
        PageFilterInput<BusinessLogDetailDTO> filter,
        Pageable pageable
    );
}
