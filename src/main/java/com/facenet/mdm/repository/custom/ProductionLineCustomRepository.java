package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.ProductionLineEntity;
import com.facenet.mdm.service.dto.ProductionLineDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

public interface ProductionLineCustomRepository {
    Page<ProductionLineEntity> getAll(PageFilterInput<ProductionLineDTO> input, Pageable pageable);
}
