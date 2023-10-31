package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.KeyValueEntityV2;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface KeyValueV2Repository extends JpaRepository<KeyValueEntityV2, Integer> {
    List<KeyValueEntityV2> findByEntityTypeAndEntityKeyInAndIsActiveTrue(Integer entityType, Collection<Integer> entityKeys);
    List<KeyValueEntityV2> findByEntityTypeAndIsActiveTrue(Integer entityType);
    KeyValueEntityV2 findByEntityKeyAndColumnPropertyEntity(Integer entityKey, ColumnPropertyEntity columnProperty);
}
