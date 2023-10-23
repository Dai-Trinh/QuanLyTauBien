package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColumnPropertyCustomRepository {
    List<ColumnPropertyEntity> getAllColumn(KeyDictionaryDTO input, String common, Pageable pageable);
}
