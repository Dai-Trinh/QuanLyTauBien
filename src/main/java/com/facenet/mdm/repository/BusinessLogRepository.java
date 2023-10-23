package com.facenet.mdm.repository;

import com.facenet.mdm.domain.BusinessLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessLogRepository extends JpaRepository<BusinessLogEntity, Long> {
    BusinessLogEntity findByEntityIdAndEntityType(Integer entityId, Integer entityType);
}
