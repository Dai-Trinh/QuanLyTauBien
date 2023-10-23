package com.facenet.mdm.service;

import com.facenet.mdm.config.Constants;
import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.repository.impl.AutoCompleteCustomRepositoryImpl;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.ErrorGroupMapper;
import com.facenet.mdm.service.mapper.ErrorMapper;
import com.facenet.mdm.service.model.*;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import liquibase.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ErrorGroupService {

    @Autowired
    private ErrorGroupResponsitory errorGroupResponsitory;

    @Autowired
    private XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    KeyDictionaryRepository keyDictionaryRepository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    ColumnPropertyService columnPropertyService;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ErrorService errorService;

    @Autowired
    ErrorResponesitory errorResponesitory;

    @Autowired
    ErrorDetailService errorDetailService;

    @Autowired
    AutoCompleteCustomRepositoryImpl autoCompleteCustomRepository;

    @Autowired
    BusinessLogService businessLogService;

    Logger logger = LoggerFactory.getLogger(ErrorGroupService.class);

    public CommonResponse getErrorGroupWithPaging(PageFilterInput<ErrorGroupFilter> errorGroupInput) {
        List<ErrorGroupDTO> errorGroupDTOList = new ArrayList<>();
        List<ErrorGroupEntity> errorGroupEntityList = new ArrayList<>();

        QErrorGroupEntity qErrorGroupEntity = QErrorGroupEntity.errorGroupEntity;
        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qErrorGroupEntity);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qErrorGroupEntity.isActive.eq(1));

        ErrorGroupFilter filter = errorGroupInput.getFilter();

        //        ErrorGroupSearchColumn operator = errorGroupInput.getOperator();
        //
        //
        //        try {
        //            operator.getErrorGroupNameColumn();
        //        } catch (Exception ex){
        //            ErrorGroupSearchColumn errorGroupSearchColumn = new ErrorGroupSearchColumn(0,0,0,0, 0);
        //            operator = errorGroupSearchColumn;
        //        }

        if (!StringUtil.isEmpty(filter.getErrorGroupCode())) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupCode.containsIgnoreCase(filter.getErrorGroupCode()));
            //            if(operator.getErrorGroupCodeCulumn() == Constants.SearchType.INCLUDE){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupCode.containsIgnoreCase(filter.getErrorGroupCode()));
            //            } else if(operator.getErrorGroupCodeCulumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupCode.notLike("%" + filter.getErrorGroupCode() + "%"));
            //            } else if(operator.getErrorGroupCodeCulumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupCode.eq(filter.getErrorGroupCode()));
            //            } else if(operator.getErrorGroupCodeCulumn() == Constants.SearchType.UNEQUAL){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupCode.notEqualsIgnoreCase(filter.getErrorGroupCode()));
            //            }
        }

        if (!StringUtil.isEmpty(filter.getErrorGroupName())) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupName.containsIgnoreCase(filter.getErrorGroupName()));
            //            if(operator.getErrorGroupNameColumn() == Constants.SearchType.INCLUDE){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupName.containsIgnoreCase(filter.getErrorGroupName()));
            //            } else if(operator.getErrorGroupNameColumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupName.notLike("%" + filter.getErrorGroupName() + "%"));
            //            } else if(operator.getErrorGroupNameColumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupName.eq(filter.getErrorGroupName()));
            //            } else if(operator.getErrorGroupNameColumn() == Constants.SearchType.UNEQUAL){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupName.notEqualsIgnoreCase(filter.getErrorGroupName()));
            //            }
        }

        if (!StringUtil.isEmpty(filter.getErrorGroupDesc())) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupDesc.containsIgnoreCase(filter.getErrorGroupDesc()));
            //            if(operator.getErrorGroupDescColumn() == Constants.SearchType.INCLUDE){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupDesc.containsIgnoreCase(filter.getErrorGroupDesc()));
            //            } else if(operator.getErrorGroupDescColumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupDesc.notLike("%" + filter.getErrorGroupDesc() + "%"));
            //            } else if(operator.getErrorGroupDescColumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupDesc.eq(filter.getErrorGroupDesc()));
            //            } else if(operator.getErrorGroupDescColumn() == Constants.SearchType.UNEQUAL){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupDesc.notEqualsIgnoreCase(filter.getErrorGroupDesc()));
            //            }

        }

        if (!StringUtil.isEmpty(filter.getErrorGroupType())) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupType.containsIgnoreCase(filter.getErrorGroupType()));
            //            if(operator.getErrorGroupTypeColumn() == Constants.SearchType.INCLUDE){
            //                booleanBuildegr.and(qErrorGroupEntity.errorGroupType.containsIgnoreCase(filter.getErrorGroupType()));
            //            } else if(operator.getErrorGroupTypeColumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupType.notLike("%" + filter.getErrorGroupType() + "%"));
            //            } else if(operator.getErrorGroupTypeColumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupType.eq(filter.getErrorGroupType()));
            //            } else if(operator.getErrorGroupTypeColumn() == Constants.SearchType.UNEQUAL)
            //                booleanBuilder.and(qErrorGroupEntity.errorGroupType.notEqualsIgnoreCase(filter.getErrorGroupType()));
        }

        if (!StringUtil.isEmpty(filter.getErrorGroupStatus())) {
            try {
                booleanBuilder.and(qErrorGroupEntity.errorGroupStatus.eq(Integer.parseInt(filter.getErrorGroupStatus())));
                //                if(operator.getErrorGroupStatusColumn() == Constants.SearchType.INCLUDE || operator.getErrorGroupStatusColumn() == Constants.SearchType.EQUAL){
                //                    booleanBuilder.and(qErrorGroupEntity.errorGroupStatus.eq(Integer.parseInt(filter.getErrorGroupStatus())));
                //                } else if(operator.getErrorGroupStatusColumn() == Constants.SearchType.EXCLUDE || operator.getErrorGroupStatusColumn() == Constants.SearchType.UNEQUAL){
                //                    booleanBuilder.and(qErrorGroupEntity.errorGroupStatus.eq(Integer.parseInt(filter.getErrorGroupStatus())));
                //                }
            } catch (Exception e) {}
        }
        Map<String, ColumnPropertyEntity> columnPropertyEntityMap = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.ERRORGROUP
        );

        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;
        Map<String, List<Integer>> errorGroupIdMap = new HashMap<>();

        if (!filter.getErrorGroupMap().isEmpty()) {
            filter
                .getErrorGroupMap()
                .forEach((keyName, value) -> {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));

                    if (!StringUtil.isEmpty(value)) {
                        dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(keyName));
                        if (columnPropertyEntityMap.get(keyName).getDataType() == Contants.DATE_VALUE) {
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                            String[] date = value.split(" ");

                            LocalDate startDate = Instant
                                .from(formatter.parse(date[0].trim()))
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                            LocalDate endDate = Instant.from(formatter.parse(date[1].trim())).atZone(ZoneId.systemDefault()).toLocalDate();
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.dateValue.between(startDate, endDate));
                        } else {
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(value));
                        }

                        propertySubQuery.where(dynamicBooleanBuilder);
                        errorGroupIdMap.put(keyName, propertySubQuery.fetch());
                    }
                });
            for (String key : errorGroupIdMap.keySet()) {
                booleanBuilder.and(qErrorGroupEntity.errorGroupId.in(errorGroupIdMap.get(key)));
            }
        }

        if (errorGroupInput.getPageSize() == 0) {
            query.where(booleanBuilder);
        } else {
            Pageable pageable = PageRequest.of(errorGroupInput.getPageNumber(), errorGroupInput.getPageSize());
            query.limit(pageable.getPageSize()).offset(pageable.getOffset()).where(booleanBuilder);
        }

        if (!StringUtil.isEmpty(errorGroupInput.getCommon())) {
            BooleanBuilder booleanBuilderSearchGeneral = new BooleanBuilder();
            booleanBuilderSearchGeneral.or(qErrorGroupEntity.errorGroupCode.containsIgnoreCase(errorGroupInput.getCommon()));
            booleanBuilderSearchGeneral.or(qErrorGroupEntity.errorGroupName.containsIgnoreCase(errorGroupInput.getCommon()));
            booleanBuilderSearchGeneral.or(qErrorGroupEntity.errorGroupDesc.containsIgnoreCase(errorGroupInput.getCommon()));
            booleanBuilderSearchGeneral.or(qErrorGroupEntity.errorGroupType.containsIgnoreCase(errorGroupInput.getCommon()));
            try {
                booleanBuilderSearchGeneral.or(qErrorGroupEntity.errorGroupStatus.eq(Integer.parseInt(errorGroupInput.getCommon())));
            } catch (Exception exception) {}
            Map<String, List<Integer>> errorGroupIdCommon = new HashMap<>();
            columnPropertyEntityMap.forEach((s, columnPropertyEntity) -> {
                if (columnPropertyEntityMap.get(s).getIsFixed() == 0) {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(errorGroupInput.getCommon()));
                    propertySubQuery.where(dynamicBooleanBuilder);
                    errorGroupIdCommon.put(s, propertySubQuery.fetch());
                }
            });
            for (String key : errorGroupIdCommon.keySet()) {
                booleanBuilderSearchGeneral.or(qErrorGroupEntity.errorGroupId.in(errorGroupIdCommon.get(key)));
            }

            query.where(booleanBuilderSearchGeneral);
        }

        boolean checkSort = true;

        if (!StringUtil.isEmpty(errorGroupInput.getSortProperty())) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.ERRORGROUP);
            for (ColumnPropertyEntity column : columns) {
                // Valid sort column name
                if (errorGroupInput.getSortProperty().equals(column.getKeyName())) {
                    checkSort = false;
                    if (column.getIsFixed() == 1) {
                        // Default col
                        Path<Object> fieldPath = Expressions.path(Object.class, qErrorGroupEntity, errorGroupInput.getSortProperty());
                        query.orderBy(new OrderSpecifier(errorGroupInput.getSortOrder(), fieldPath));
                    } else {
                        // Dynamic col
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qErrorGroupEntity.errorGroupId)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.ERRORGROUP))
                                    .and(qKeyValueEntityV22.columnPropertyEntity.eq(column))
                            );
                        switch (column.getDataType()) {
                            case Contants.INT_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorGroupInput.getSortOrder(), qKeyValueEntityV22.intValue));
                                break;
                            case Contants.FLOAT_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorGroupInput.getSortOrder(), qKeyValueEntityV22.doubleValue));
                                break;
                            case Contants.STRING_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorGroupInput.getSortOrder(), qKeyValueEntityV22.stringValue));
                                break;
                            case Contants.JSON_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorGroupInput.getSortOrder(), qKeyValueEntityV22.jsonValue));
                                break;
                            case Contants.DATE_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorGroupInput.getSortOrder(), qKeyValueEntityV22.dateValue));
                                break;
                        }
                    }
                }
            }
            if (checkSort) {
                Path<Object> fieldPath = Expressions.path(Object.class, qErrorGroupEntity, errorGroupInput.getSortProperty());
                query.orderBy(new OrderSpecifier(errorGroupInput.getSortOrder(), fieldPath));
            }
        }

        if (errorGroupInput.getPageSize() == 0) {
            errorGroupEntityList = query.fetch();

            List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.ERRORGROUP);
            Map<Integer, List<KeyValueEntityV2>> errorPropertyMap = properties
                .stream()
                .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

            for (ErrorGroupEntity errorGroupEntity : errorGroupEntityList) {
                errorGroupDTOList.add(
                    ErrorGroupMapper.entytoDTOMap(errorGroupEntity, errorPropertyMap.get(errorGroupEntity.getErrorGroupId()))
                );
            }

            List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
                Contants.EntityType.ERRORGROUP
            );

            return new PageResponse<List<ErrorGroupDTO>>(errorGroupEntityList.size())
                .success()
                .data(errorGroupDTOList)
                .columns(keyDictionaryDTOS);
        }

        Pageable pageable = PageRequest.of(errorGroupInput.getPageNumber(), errorGroupInput.getPageSize());

        errorGroupEntityList = query.fetch();
        long count = query.fetchCount();

        Page<ErrorGroupEntity> errorGroupEntityPage = new PageImpl<>(errorGroupEntityList, pageable, count);

        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.ERRORGROUP,
            errorGroupEntityPage.getContent().stream().map(ErrorGroupEntity::getErrorGroupId).collect(Collectors.toList())
        );
        Map<Integer, List<KeyValueEntityV2>> errorPropertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        errorGroupDTOList = new ArrayList<>();

        for (ErrorGroupEntity errorGroupEntity : errorGroupEntityPage.getContent()) {
            errorGroupDTOList.add(
                ErrorGroupMapper.entytoDTOMap(errorGroupEntity, errorPropertyMap.get(errorGroupEntity.getErrorGroupId()))
            );
        }

        List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.ERRORGROUP
        );

        return new PageResponse<List<ErrorGroupDTO>>(errorGroupEntityPage.getTotalElements())
            .success()
            .data(errorGroupDTOList)
            .columns(keyDictionaryDTOS);
    }

    @Transactional
    public CommonResponse newErrorGroup(ErrorGroupDTO errorGroupDTO) {
        ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(errorGroupDTO.getErrorGroupCode());
        if (errorGroupEntity != null) {
            throw new CustomException(HttpStatus.CONFLICT, "Nhóm lỗi đã tồn tại mã " + errorGroupEntity.getErrorGroupCode());
        } else {
            errorGroupEntity = new ErrorGroupEntity();
            errorGroupEntity.setErrorGroupCode(errorGroupDTO.getErrorGroupCode());
            errorGroupEntity.setErrorGroupName(errorGroupDTO.getErrorGroupName());
            errorGroupEntity.setErrorGroupDesc(errorGroupDTO.getErrorGroupDesc());
            errorGroupEntity.setErrorGroupStatus(Contants.ErrorStatus.getStatus(errorGroupDTO.getErrorGroupStatus()));
            errorGroupEntity.setErrorGroupType(errorGroupDTO.getErrorGroupType());
            errorGroupEntity.setIsActive(1);
            ErrorGroupEntity errorGroupEntitySave = errorGroupResponsitory.save(errorGroupEntity);
            if (!errorGroupDTO.getErrorGroupMap().isEmpty()) {
                keyValueService.createUpdateKeyValueOfEntity(
                    errorGroupEntitySave.getErrorGroupId(),
                    errorGroupDTO.getErrorGroupMap(),
                    Contants.EntityType.ERRORGROUP,
                    false
                );
            }

            businessLogService.insertInsertionLog(
                errorGroupEntity.getId(),
                Contants.EntityType.ERRORGROUP,
                ErrorGroupMapper.toLogDetail(errorGroupEntity, errorGroupDTO.getErrorGroupMap())
            );

            return new CommonResponse().success();
            //            try {
            //
            //            } catch (Exception ex) {
            //                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
            //            }
        }
    }

    @Transactional
    public CommonResponse updateErrorGroup(String errorGroupCode, ErrorGroupDTO errorGroupDTO) {
        ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(errorGroupCode);
        if (errorGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Nhóm lỗi không tồn tại");
        } else {
            ErrorGroupEntity oldValue = new ErrorGroupEntity(errorGroupEntity);
            //errorGroupEntity.setErrorGroupCode(errorGroupDTO.getErrorGroupCode());
            errorGroupEntity.setErrorGroupName(errorGroupDTO.getErrorGroupName());
            errorGroupEntity.setErrorGroupDesc(errorGroupDTO.getErrorGroupDesc());
            errorGroupEntity.setErrorGroupStatus(Contants.ErrorStatus.getStatus(errorGroupDTO.getErrorGroupStatus()));
            errorGroupEntity.setErrorGroupType(errorGroupDTO.getErrorGroupType());
            errorGroupEntity.setIsActive(1);
            ErrorGroupEntity savedEntity = errorGroupResponsitory.save(errorGroupEntity);

            BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                savedEntity.getId(),
                Contants.EntityType.ERRORGROUP,
                ErrorGroupMapper.toUpdateLogDetail(oldValue, errorGroupEntity)
            );

            if (!errorGroupDTO.getErrorGroupMap().isEmpty()) {
                keyValueService.createUpdateKeyValueOfEntityWithLog(
                    errorGroupEntity.getId(),
                    errorGroupDTO.getErrorGroupMap(),
                    Contants.EntityType.ERRORGROUP,
                    true,
                    logEntity
                );
            }

            return new CommonResponse().success("Cập nhật thành công");
            //            try {
            //
            //            } catch (Exception e) {
            //                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu  không hợp lệ");
            //            }
        }
    }

    @Transactional
    public CommonResponse deleteErrorGroup(String errorGroupCode) {
        ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(errorGroupCode);
        if (errorGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm lỗi: " + errorGroupCode);
        } else {
            errorGroupEntity.setIsActive(0);
            errorGroupResponsitory.save(errorGroupEntity);
            businessLogService.insertDeleteLog(
                errorGroupEntity.getId(),
                Contants.EntityType.ERRORGROUP,
                ErrorGroupMapper.toDeletionLogDetail(errorGroupEntity)
            );
            return new CommonResponse().success("Xóa thành công " + errorGroupCode);
        }
    }

    @Transactional
    public CommonResponse importErrorGroupFromExcel(MultipartFile file) throws IOException {
        Map<ErrorGroupDTO, List<ErrorDTO>> errorGroupDTOList = xlsxExcelHandle.readErrorGroupExcel(file.getInputStream());
        for (ErrorGroupDTO errorGroupDTO : errorGroupDTOList.keySet()) {
            if (StringUtil.isEmpty(errorGroupDTO.getErrorGroupCode()) || StringUtil.isEmpty(errorGroupDTO.getErrorGroupName())) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Không được để trống mã và tên nhóm lỗi");
            }
            ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(errorGroupDTO.getErrorGroupCode());
            if (errorGroupEntity == null) {
                this.newErrorGroup(errorGroupDTO);
            } else {
                throw new CustomException(HttpStatus.CONFLICT, "Đã tồn tại nhóm lỗi có mã: " + errorGroupDTO.getErrorGroupCode());
            }

            for (ErrorDTO errorDTO : errorGroupDTOList.get(errorGroupDTO)) {
                ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorDTO.getErrorCode());
                if (errorEntity == null) {
                    throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy lỗi có mã: " + errorDTO.getErrorCode());
                } else {
                    ErrorGroupEntity errorGroupEntitySave = errorGroupResponsitory.getErrorGroupEntitiesByCode(
                        errorGroupDTO.getErrorGroupCode()
                    );
                    ErrorEntity oldValue = new ErrorEntity(errorEntity);
                    Set<ErrorGroupEntity> errorGroupEntities = errorEntity.getErrorGroupEntities();
                    errorGroupEntities.add(errorGroupEntitySave);
                    errorEntity.setErrorGroupEntities(errorGroupEntities);
                    ErrorEntity savedEntity = errorResponesitory.save(errorEntity);

                    businessLogService.insertUpdateLog(
                        savedEntity.getId(),
                        Contants.EntityType.ERROR,
                        ErrorMapper.toUpdateLogDetail(oldValue, errorEntity)
                    );
                }
            }
        }
        return new CommonResponse().success();
    }

    public List<String> getAutoComplete(PageFilterInput<ErrorGroupDTO> input) {
        String common = input.getCommon();
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }

        QErrorGroupEntity qErrorGroupEntity = QErrorGroupEntity.errorGroupEntity;
        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qErrorGroupEntity).where(qErrorGroupEntity.isActive.eq(1));
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (!StringUtil.isEmpty(common)) {
            Map<String, ColumnPropertyEntity> columnPropertyEntityMap = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
                Contants.EntityType.ERRORGROUP
            );
            QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;
            booleanBuilder.or(qErrorGroupEntity.errorGroupName.containsIgnoreCase(common));
            booleanBuilder.or(qErrorGroupEntity.errorGroupCode.containsIgnoreCase(common));
            booleanBuilder.or(qErrorGroupEntity.errorGroupDesc.containsIgnoreCase(common));
            booleanBuilder.or(qErrorGroupEntity.errorGroupType.containsIgnoreCase(common));
            try {
                booleanBuilder.or(qErrorGroupEntity.errorGroupStatus.eq(Integer.parseInt(common)));
            } catch (Exception exception) {}
            Map<String, List<Integer>> errorGroupIdCommon = new HashMap<>();
            columnPropertyEntityMap.forEach((s, columnPropertyEntity) -> {
                if (columnPropertyEntityMap.get(s).getIsFixed() == 0) {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.or(qKeyValueEntityV2.isActive.eq(true));
                    dynamicBooleanBuilder.or(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                    dynamicBooleanBuilder.or(qKeyValueEntityV2.commonValue.containsIgnoreCase(common));
                    propertySubQuery.where(dynamicBooleanBuilder);
                    errorGroupIdCommon.put(s, propertySubQuery.fetch());
                }
            });
            for (String key : errorGroupIdCommon.keySet()) {
                booleanBuilder.or(qErrorGroupEntity.errorGroupId.in(errorGroupIdCommon.get(key)));
            }

            query.where(booleanBuilder);
        }

        if (!StringUtil.isEmpty(input.getSortProperty())) {
            Path<Object> fieldPath = Expressions.path(Object.class, qErrorGroupEntity, input.getSortProperty());
            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
        }

        List<ErrorGroupEntity> errorGroupEntities = query.fetch();

        long count = query.fetchCount();

        Page<ErrorGroupEntity> errorGroupEntityPage = new PageImpl<>(errorGroupEntities, pageable, count);
        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.ERRORGROUP);
        Map<Integer, List<KeyValueEntityV2>> errorPropertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));
        List<String> searchCommon = new ArrayList<>();
        for (ErrorGroupEntity errorGroupEntity : errorGroupEntityPage.getContent()) {
            if (searchCommon.size() >= 10) {
                break;
            }
            if (
                errorGroupEntity.getErrorGroupCode().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(errorGroupEntity.getErrorGroupCode())
            ) searchCommon.add(errorGroupEntity.getErrorGroupCode());
            if (
                errorGroupEntity.getErrorGroupName().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(errorGroupEntity.getErrorGroupName())
            ) searchCommon.add(errorGroupEntity.getErrorGroupName());
            if (
                !StringUtil.isEmpty(errorGroupEntity.getErrorGroupDesc()) &&
                errorGroupEntity.getErrorGroupDesc().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(errorGroupEntity.getErrorGroupDesc())
            ) searchCommon.add(errorGroupEntity.getErrorGroupDesc());
            if (
                !StringUtil.isEmpty(errorGroupEntity.getErrorGroupType()) &&
                errorGroupEntity.getErrorGroupType().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(errorGroupEntity.getErrorGroupType())
            ) searchCommon.add(errorGroupEntity.getErrorGroupType());
            if (!CollectionUtils.isEmpty(errorPropertyMap.get(errorGroupEntity.getErrorGroupId()))) {
                for (KeyValueEntityV2 keyValueEntityV2 : errorPropertyMap.get(errorGroupEntity.getErrorGroupId())) {
                    if (
                        !StringUtil.isEmpty(keyValueEntityV2.getCommonValue()) &&
                        keyValueEntityV2.getCommonValue().toLowerCase().contains(common.toLowerCase()) &&
                        !searchCommon.contains(keyValueEntityV2.getCommonValue())
                    ) {
                        searchCommon.add(keyValueEntityV2.getCommonValue());
                    }
                }
            }
        }
        return searchCommon;
    }

    public List<ErrorGroupQMSDTO> getListErrorForQMS(PageFilterInput<ErrorGroupQMSDTO> filterInput) {
        ErrorGroupQMSDTO errorGroupQMSDTO = filterInput.getFilter();
        List<ErrorGroupQMSDTO> errorGroupQMSDTOList = new ArrayList<>();
        QErrorGroupEntity qErrorGroupEntity = QErrorGroupEntity.errorGroupEntity;
        QErrorEntity qErrorEntity = QErrorEntity.errorEntity;
        QErrorDetailEntity qErrorDetailEntity = QErrorDetailEntity.errorDetailEntity;
        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qErrorGroupEntity);

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qErrorGroupEntity.isActive.eq(1));
        if (errorGroupQMSDTO.getId() != null) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupId.eq(errorGroupQMSDTO.getId()));
        }

        if (!StringUtil.isEmpty(errorGroupQMSDTO.getErrorGroupCode())) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupCode.containsIgnoreCase(errorGroupQMSDTO.getErrorGroupCode()));
        }

        if (!StringUtil.isEmpty(errorGroupQMSDTO.getErrorGroupName())) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupName.containsIgnoreCase(errorGroupQMSDTO.getErrorGroupName()));
        }

        if (!StringUtil.isEmpty(errorGroupQMSDTO.getErrorGroupDesc())) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupDesc.containsIgnoreCase(errorGroupQMSDTO.getErrorGroupDesc()));
        }

        if (errorGroupQMSDTO.getCreatedAt() != null) {
            booleanBuilder.and(qErrorGroupEntity.createdAt.eq(errorGroupQMSDTO.getCreatedAt()));
        }

        if (!StringUtil.isEmpty(errorGroupQMSDTO.getCreatedBy())) {
            booleanBuilder.and(qErrorGroupEntity.createdBy.containsIgnoreCase(errorGroupQMSDTO.getCreatedBy()));
        }

        if (errorGroupQMSDTO.getErrorGroupStatus() != null) {
            booleanBuilder.and(qErrorGroupEntity.errorGroupStatus.eq(errorGroupQMSDTO.getErrorGroupStatus()));
        }

        //        if(!CollectionUtils.isEmpty(errorGroupQMSDTO.getErrorList())){
        //            ErrorQMSDTO errorQMSDTO = errorGroupQMSDTO.getErrorList().get(0);
        //            JPAQuery querySearchError = new JPAQueryFactory(entityManager)
        //                .select(qErrorDetailEntity.errorGroupId)
        //                .from(qErrorDetailEntity)
        //                .innerJoin(qErrorEntity)
        //                .on(qErrorDetailEntity.errorId.eq(qErrorEntity.errorId));
        //            BooleanBuilder booleanBuilderSearchError = new BooleanBuilder();
        //            booleanBuilderSearchError.and(qErrorEntity.isActive.eq(1));
        //            if(errorQMSDTO.getId() != null){
        //                booleanBuilderSearchError.and(qErrorEntity.errorId.eq(errorQMSDTO.getId()));
        //            }
        //
        //            if(!StringUtil.isEmpty(errorQMSDTO.getErrorCode())){
        //                booleanBuilderSearchError.and(qErrorEntity.errorCode.containsIgnoreCase(errorQMSDTO.getErrorCode()));
        //            }
        //
        //            if(!StringUtil.isEmpty(errorQMSDTO.getErrorName())){
        //                booleanBuilderSearchError.and(qErrorEntity.errorName.containsIgnoreCase(errorQMSDTO.getErrorName()));
        //            }
        //
        //            if(!StringUtil.isEmpty(errorQMSDTO.getErrorDesc())){
        //                booleanBuilderSearchError.and(qErrorEntity.errorDesc.containsIgnoreCase(errorQMSDTO.getErrorDesc()));
        //            }
        //
        //            if(errorQMSDTO.getCreatedAt() != null){
        //                booleanBuilderSearchError.and(qErrorEntity.createdAt.eq(errorQMSDTO.getCreatedAt()));
        //            }
        //
        //            if(!StringUtil.isEmpty(errorQMSDTO.getCreatedBy())){
        //                booleanBuilderSearchError.and(qErrorEntity.createdBy.containsIgnoreCase(errorQMSDTO.getCreatedBy()));
        //            }
        //
        //            if(errorQMSDTO.getErrorStatus() != null){
        //                booleanBuilderSearchError.and(qErrorEntity.errorStatus.eq(errorQMSDTO.getErrorStatus()));
        //            }
        //            querySearchError.where(booleanBuilderSearchError);
        //            List<Integer> errorGroupId = querySearchError.fetch();
        //            booleanBuilder.and(qErrorGroupEntity.errorGroupId.in(errorGroupId));
        //
        //        }

        query.where(booleanBuilder);

        if (!StringUtil.isEmpty(filterInput.getCommon())) {
            BooleanBuilder booleanBuilderCommon = new BooleanBuilder();
            try {
                booleanBuilderCommon.or(qErrorGroupEntity.errorGroupId.eq(Integer.parseInt(filterInput.getCommon())));
            } catch (Exception ex) {}
            booleanBuilderCommon.or(qErrorGroupEntity.errorGroupName.containsIgnoreCase(filterInput.getCommon()));
            booleanBuilderCommon.or(qErrorGroupEntity.errorGroupCode.containsIgnoreCase(filterInput.getCommon()));
            booleanBuilderCommon.or(qErrorGroupEntity.errorGroupDesc.containsIgnoreCase(filterInput.getCommon()));
            booleanBuilderCommon.or(qErrorGroupEntity.createdBy.containsIgnoreCase(filterInput.getCommon()));
            query.where(booleanBuilderCommon);
        }

        List<ErrorGroupEntity> errorGroupEntities = query.fetch();
        for (ErrorGroupEntity errorGroupEntity : errorGroupEntities) {
            JPAQuery queryError = new JPAQueryFactory(entityManager)
                .select(
                    Projections.constructor(
                        ErrorQMSDTO.class,
                        qErrorEntity.errorId,
                        qErrorEntity.errorCode,
                        qErrorEntity.errorName,
                        qErrorEntity.errorDesc,
                        qErrorEntity.createdBy,
                        qErrorEntity.createdAt,
                        qErrorEntity.errorStatus
                    )
                )
                .from(qErrorEntity)
                .join(qErrorDetailEntity)
                .on(qErrorEntity.errorId.eq(qErrorDetailEntity.errorId))
                .where(qErrorDetailEntity.errorGroupId.eq(errorGroupEntity.getErrorGroupId()));
            List<ErrorQMSDTO> errorQMSDTOS = queryError.fetch();
            errorGroupQMSDTOList.add(
                new ErrorGroupQMSDTO(
                    errorGroupEntity.getId(),
                    errorGroupEntity.getErrorGroupCode(),
                    errorGroupEntity.getErrorGroupName(),
                    errorGroupEntity.getErrorGroupDesc(),
                    errorGroupEntity.getCreatedBy(),
                    errorGroupEntity.getCreatedAt(),
                    errorGroupEntity.getErrorGroupStatus(),
                    errorQMSDTOS
                )
            );
        }
        return errorGroupQMSDTOList;
    }
}
