package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.StageQmsDTO;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import com.querydsl.core.types.Order;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductionStageCustomRepository {
    Page<ProductionStageEntity> getAllStage(PageFilterInput<ProductionStageDTO> input, Pageable pageable);
    List<ProductionStageEntity> getAllStageForQms(StageQmsDTO stageQmsDTO, String common, String sortProperty, Order sortOrder);
    List<String> getAutoComplete(String value, String keyName);
}
