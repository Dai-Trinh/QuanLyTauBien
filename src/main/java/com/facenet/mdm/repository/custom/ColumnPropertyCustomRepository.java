package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.service.dto.JobDTO;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColumnPropertyCustomRepository {
    List<ColumnPropertyEntity> getAllColumn(KeyDictionaryDTO input, String common, Pageable pageable);
}
