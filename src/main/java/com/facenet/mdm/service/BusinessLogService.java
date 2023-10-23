package com.facenet.mdm.service;

import com.facenet.mdm.domain.BusinessLogDetailEntity;
import com.facenet.mdm.domain.BusinessLogEntity;
import com.facenet.mdm.repository.BusinessLogDetailRepository;
import com.facenet.mdm.repository.BusinessLogRepository;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.BusinessLogCustomRepository;
import com.facenet.mdm.repository.custom.BusinessLogDetailCustomRepository;
import com.facenet.mdm.service.dto.BusinessLogDTO;
import com.facenet.mdm.service.dto.BusinessLogDetailDTO;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.Utils;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BusinessLogService {

    private final BusinessLogCustomRepository businessLogCustomRepository;
    private final BusinessLogDetailCustomRepository businessLogDetailCustomRepository;
    private final BusinessLogRepository businessLogRepository;
    private final BusinessLogDetailRepository businessLogDetailRepository;
    private final ColumnPropertyRepository columnPropertyRepository;

    public BusinessLogService(
        BusinessLogCustomRepository businessLogCustomRepository,
        BusinessLogDetailCustomRepository businessLogDetailCustomRepository,
        BusinessLogRepository businessLogRepository,
        BusinessLogDetailRepository businessLogDetailRepository,
        ColumnPropertyRepository columnPropertyRepository
    ) {
        this.businessLogCustomRepository = businessLogCustomRepository;
        this.businessLogDetailCustomRepository = businessLogDetailCustomRepository;
        this.businessLogRepository = businessLogRepository;
        this.businessLogDetailRepository = businessLogDetailRepository;
        this.columnPropertyRepository = columnPropertyRepository;
    }

    public Page<BusinessLogEntity> getAllChangeLog(PageFilterInput<BusinessLogDTO> filter) {
        return businessLogCustomRepository.getAllLog(filter, Utils.getPageable(filter.getPageNumber(), filter.getPageSize()));
    }

    public Page<BusinessLogDetailDTO> getChangeLogDetail(Long id, PageFilterInput<BusinessLogDetailDTO> filter) {
        BusinessLogEntity businessLog = businessLogRepository.findById(id).orElse(null);
        if (businessLog == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        return businessLogDetailCustomRepository.getAllByBusinessLogEntity(
            businessLog,
            filter,
            Utils.getPageable(filter.getPageNumber(), filter.getPageSize())
        );
    }

    public Set<String> getCommonAutocomplete(PageFilterInput<BusinessLogDTO> input) {
        Page<BusinessLogEntity> data = getAllChangeLog(input);
        String query = input.getCommon().toLowerCase();
        Set<String> result = new HashSet<>();
        for (BusinessLogEntity businessLogEntity : data.getContent()) {
            if (businessLogEntity.getActionName().toLowerCase().contains(query)) result.add(businessLogEntity.getActionName());
            if (businessLogEntity.getUserName().toLowerCase().contains(query)) result.add(businessLogEntity.getUserName());
            if (businessLogEntity.getFunctionName().toLowerCase().contains(query)) result.add(businessLogEntity.getFunctionName());
        }
        return result;
    }

    /**
     * Insert insert log of entity include dynamic properties
     * @param entityType entity type
     * @param newValue inserted value
     */
    public void insertInsertionLog(Integer entityId, int entityType, List<BusinessLogDetailEntity> newValue) {
        BusinessLogEntity savedBusinessLogEntity = businessLogRepository.save(getBusinessLog(entityId, entityType, "INSERT"));

        newValue.forEach(businessLogDetailEntity -> businessLogDetailEntity.setBusinessLog(savedBusinessLogEntity));
        businessLogDetailRepository.saveAll(newValue);
    }

    public void insertInsertionLogByBatch(int entityType, Map<Integer, List<BusinessLogDetailEntity>> newValue) {
        List<BusinessLogEntity> businessLogEntities = new ArrayList<>(newValue.size());
        newValue.forEach((key, value) -> {
            BusinessLogEntity businessLogEntity = getBusinessLog(key, entityType, "INSERT");
            businessLogEntity.setBusinessLogDetails(value);
            value.forEach(businessLogDetailEntity -> businessLogDetailEntity.setBusinessLog(businessLogEntity));
            businessLogEntities.add(businessLogEntity);
        });
        businessLogRepository.saveAll(businessLogEntities);
    }

    /**
     * Insert update log
     * @param entityId entity id
     * @param entityType entity type
     * @param value updated value with last value exclude Dynamic properties
     * @return saved business log
     */
    public BusinessLogEntity insertUpdateLog(Integer entityId, int entityType, List<BusinessLogDetailEntity> value) {
        BusinessLogEntity savedBusinessLogEntity = businessLogRepository.save(getBusinessLog(entityId, entityType, "UPDATE"));

        value.forEach(businessLogDetailEntity -> businessLogDetailEntity.setBusinessLog(savedBusinessLogEntity));
        businessLogDetailRepository.saveAll(value);

        return savedBusinessLogEntity;
    }

    /**
     * Insert update dynamic properties log, owner entity must be inserted
     * @param newValue updated properties
     */
    public void insertUpdateDynamicPropertiesLog(BusinessLogEntity businessLogEntity, List<BusinessLogDetailEntity> newValue) {
        newValue.forEach(businessLogDetailEntity -> businessLogDetailEntity.setBusinessLog(businessLogEntity));
        businessLogDetailRepository.saveAll(newValue);
    }

    /**
     * insert delete
     * @param entityId entity id
     * @param entityType entity type
     */
    public void insertDeleteLog(int entityId, int entityType, List<BusinessLogDetailEntity> oldValue) {
        BusinessLogEntity savedBusinessLogEntity = businessLogRepository.save(getBusinessLog(entityId, entityType, "DELETE"));

        oldValue.forEach(businessLogDetailEntity -> businessLogDetailEntity.setBusinessLog(savedBusinessLogEntity));
        businessLogDetailRepository.saveAll(oldValue);
    }

    private BusinessLogEntity getBusinessLog(int entityId, int entityType, String actionName) {
        BusinessLogEntity businessLogEntity = new BusinessLogEntity();
        businessLogEntity.setEntityId(entityId);
        businessLogEntity.setEntityType(entityType);
        //businessLogEntity.setFunctionName(Contants.FUNCTION_NAME.get(entityType));
        businessLogEntity.setActionName(actionName);
        return businessLogEntity;
    }
}
