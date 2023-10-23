package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.BusinessLogEntity;
import com.facenet.mdm.service.dto.BusinessLogDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusinessLogCustomRepository {
    Page<BusinessLogEntity> getAllLog(PageFilterInput<BusinessLogDTO> input, Pageable pageable);
}
