package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.Citt1Entity;
import com.facenet.mdm.domain.CoittEntity;
import com.facenet.mdm.service.dto.Citt1DTO;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Citt1CustomRepository {
    Page<Citt1Entity> getAll(PageFilterInput<CoittDTO> input, Pageable pageable);
}
