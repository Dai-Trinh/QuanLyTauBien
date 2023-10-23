package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.ErrorDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.ErrorMapper;
import com.facenet.mdm.service.model.*;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class ErrorService {

    @Autowired
    ErrorResponesitory errorResponesitory;

    @Autowired
    ErrorGroupResponsitory errorGroupResponsitory;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    KeyDictionaryRepository keyDictionaryRepository;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    BusinessLogService businessLogService;

    Logger logger = LoggerFactory.getLogger(ErrorService.class);

    public CommonResponse getErrorWithPaging(PageFilterInput<ErrorFilter> errorInput) {
        List<ErrorDTO> errorDTOS = new ArrayList<>();

        List<ErrorEntity> errorEntityList = new ArrayList<>();

        QErrorEntity qErrorEntity = QErrorEntity.errorEntity;
        QErrorDetailEntity qErrorDetailEntity = QErrorDetailEntity.errorDetailEntity;
        QErrorGroupEntity qErrorGroupEntity = QErrorGroupEntity.errorGroupEntity;

        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qErrorEntity);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qErrorEntity.isActive.eq(1));

        JPAQuery querySearchErrorGroup = new JPAQueryFactory(entityManager)
            .select(qErrorDetailEntity.errorId)
            .from(qErrorGroupEntity)
            .join(qErrorDetailEntity)
            .on(qErrorGroupEntity.errorGroupId.eq(qErrorDetailEntity.errorGroupId));

        BooleanBuilder booleanBuilderSearchErrorGroup = new BooleanBuilder();
        booleanBuilderSearchErrorGroup.and(qErrorGroupEntity.isActive.eq(1));

        ErrorFilter errorFilter = errorInput.getFilter();
        //        ErrorSearchColumn operator = errorInput.getOperator();
        //
        //
        //        try {
        //            operator.getErrorCodeCulumn();
        //        } catch (Exception ex){
        //            ErrorSearchColumn errorSearchColumn = new ErrorSearchColumn(0,0,0,0,0);
        //            operator = errorSearchColumn;
        //        }

        if (!StringUtil.isEmpty(errorFilter.getErrorCode())) {
            booleanBuilder.and(qErrorEntity.errorCode.containsIgnoreCase(errorFilter.getErrorCode()));
            //            if(operator.getErrorCodeCulumn() == Constants.SearchType.INCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorCode.containsIgnoreCase(errorFilter.getErrorCode()));
            //            } else if(operator.getErrorCodeCulumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorCode.notLike("%" + errorFilter.getErrorCode() + "%"));
            //            } else if(operator.getErrorCodeCulumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorEntity.errorCode.eq(errorFilter.getErrorCode()));
            //            } else if(operator.getErrorCodeCulumn() == Constants.SearchType.UNEQUAL){
            //                booleanBuilder.and(qErrorEntity.errorCode.notEqualsIgnoreCase(errorFilter.getErrorCode()));
            //            }
        }

        if (!StringUtil.isEmpty(errorFilter.getErrorName())) {
            booleanBuilder.and(qErrorEntity.errorName.containsIgnoreCase(errorFilter.getErrorName()));
            //            if(operator.getErrorNameColumn() == Constants.SearchType.INCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorName.containsIgnoreCase(errorFilter.getErrorName()));
            //            } else if(operator.getErrorNameColumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorName.notLike("%" + errorFilter.getErrorName() + "%"));
            //            } else if(operator.getErrorNameColumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorEntity.errorName.eq(errorFilter.getErrorName()));
            //            } else if(operator.getErrorNameColumn() == Constants.SearchType.UNEQUAL){
            //                booleanBuilder.and(qErrorEntity.errorName.notEqualsIgnoreCase(errorFilter.getErrorName()));
            //            }
        }

        if (!StringUtil.isEmpty(errorFilter.getErrorDesc())) {
            booleanBuilder.and(qErrorEntity.errorDesc.containsIgnoreCase(errorFilter.getErrorDesc()));
            //            if(operator.getErrorDescColumn() == Constants.SearchType.INCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorDesc.containsIgnoreCase(errorFilter.getErrorDesc()));
            //            } else if(operator.getErrorDescColumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorDesc.notLike("%" + errorFilter.getErrorDesc() + "%"));
            //            } else if(operator.getErrorDescColumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorEntity.errorDesc.eq(errorFilter.getErrorDesc()));
            //            } else if(operator.getErrorDescColumn() == Constants.SearchType.UNEQUAL){
            //                booleanBuilder.and(qErrorEntity.errorDesc.notEqualsIgnoreCase(errorFilter.getErrorDesc()));
            //            }

        }

        if (!StringUtil.isEmpty(errorFilter.getErrorType())) {
            booleanBuilder.and(qErrorEntity.errorType.containsIgnoreCase(errorFilter.getErrorType()));
            //            if(operator.getErrorTypeColumn() == Constants.SearchType.INCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorType.containsIgnoreCase(errorFilter.getErrorType()));
            //            } else if(operator.getErrorTypeColumn() == Constants.SearchType.EXCLUDE){
            //                booleanBuilder.and(qErrorEntity.errorType.notLike("%" + errorFilter.getErrorType() + "%"));
            //            } else if(operator.getErrorTypeColumn() == Constants.SearchType.EQUAL){
            //                booleanBuilder.and(qErrorEntity.errorType.eq(errorFilter.getErrorType()));
            //            } else if(operator.getErrorTypeColumn() == Constants.SearchType.UNEQUAL)
            //                booleanBuilder.and(qErrorEntity.errorType.notEqualsIgnoreCase(errorFilter.getErrorType()));
        }

        List<Integer> errorIDColumn = new ArrayList<>();

        if (!StringUtil.isEmpty(errorFilter.getErrorGroup())) {
            booleanBuilderSearchErrorGroup.and(qErrorGroupEntity.errorGroupName.containsIgnoreCase(errorFilter.getErrorGroup()));
            querySearchErrorGroup.where(booleanBuilderSearchErrorGroup);

            errorIDColumn = querySearchErrorGroup.fetch();
            booleanBuilder.and(qErrorEntity.errorId.in(errorIDColumn));
        }

        if (!StringUtil.isEmpty(errorFilter.getErrorStatus())) {
            try {
                booleanBuilder.and(qErrorEntity.errorStatus.eq(Integer.parseInt(errorFilter.getErrorStatus())));
                //                if(operator.getErrorStatusColumn() == Constants.SearchType.INCLUDE || operator.getErrorStatusColumn() == Constants.SearchType.EQUAL){
                //                    booleanBuilder.and(qErrorEntity.errorStatus.eq(Integer.parseInt(errorFilter.getErrorStatus())));
                //                } else if(operator.getErrorStatusColumn() == Constants.SearchType.EXCLUDE || operator.getErrorStatusColumn() == Constants.SearchType.UNEQUAL){
                //                    booleanBuilder.and(qErrorEntity.errorStatus.eq(Integer.parseInt(errorFilter.getErrorStatus())));
                //                }
            } catch (Exception e) {}
        }

        Map<String, ColumnPropertyEntity> columnPropertyEntityMap = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.ERROR
        );
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;
        if (!errorFilter.getErrorMap().isEmpty()) {
            Map<String, List<Integer>> errorIdMap = new HashMap<>();

            errorFilter
                .getErrorMap()
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
                        errorIdMap.put(keyName, propertySubQuery.fetch());
                    }
                });

            for (String key : errorIdMap.keySet()) {
                booleanBuilder.and(qErrorEntity.errorId.in(errorIdMap.get(key)));
            }
        }

        if (errorInput.getPageSize() == 0) {
            query.where(booleanBuilder);
        } else {
            Pageable pageable = PageRequest.of(errorInput.getPageNumber(), errorInput.getPageSize());
            query.limit(pageable.getPageSize()).offset(pageable.getOffset()).where(booleanBuilder);
        }

        if (!StringUtil.isEmpty(errorInput.getCommon())) {
            BooleanBuilder booleanBuilderSearchGeneral = new BooleanBuilder();
            booleanBuilderSearchGeneral.or(qErrorEntity.errorCode.containsIgnoreCase(errorInput.getCommon()));
            booleanBuilderSearchGeneral.or(qErrorEntity.errorName.containsIgnoreCase(errorInput.getCommon()));
            booleanBuilderSearchGeneral.or(qErrorEntity.errorDesc.containsIgnoreCase(errorInput.getCommon()));
            booleanBuilderSearchGeneral.or(qErrorEntity.errorType.containsIgnoreCase(errorInput.getCommon()));

            booleanBuilderSearchErrorGroup.and(qErrorGroupEntity.errorGroupName.containsIgnoreCase(errorInput.getCommon()));
            querySearchErrorGroup.where(booleanBuilderSearchErrorGroup);

            errorIDColumn = querySearchErrorGroup.fetch();
            booleanBuilderSearchGeneral.or(qErrorEntity.errorId.in(errorIDColumn));

            try {
                booleanBuilderSearchGeneral.or(qErrorEntity.errorStatus.eq(Integer.parseInt(errorInput.getCommon())));
            } catch (Exception ex) {}

            Map<String, List<Integer>> errorIdCommon = new HashMap<>();
            columnPropertyEntityMap.forEach((s, columnPropertyEntity) -> {
                if (columnPropertyEntityMap.get(s).getIsFixed() == 0) {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(errorInput.getCommon()));
                    propertySubQuery.where(dynamicBooleanBuilder);
                    errorIdCommon.put(s, propertySubQuery.fetch());
                }
            });
            for (String key : errorIdCommon.keySet()) {
                booleanBuilderSearchGeneral.or(qErrorEntity.errorId.in(errorIdCommon.get(key)));
            }

            query.where(booleanBuilderSearchGeneral);
        }

        if (!StringUtil.isEmpty(errorInput.getSortProperty())) {
            boolean checkSort = true;
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.ERROR);
            for (ColumnPropertyEntity column : columns) {
                // Valid sort column name
                if (errorInput.getSortProperty().equals(column.getKeyName())) {
                    checkSort = false;
                    if (column.getIsFixed() == 1 && !errorInput.getSortProperty().equals("errorGroup")) {
                        // Default col
                        Path<Object> fieldPath = Expressions.path(Object.class, qErrorEntity, errorInput.getSortProperty());
                        query.orderBy(new OrderSpecifier(errorInput.getSortOrder(), fieldPath));
                    } else if (errorInput.getSortProperty().equals("errorGroup")) {
                        query
                            .leftJoin(qErrorDetailEntity)
                            .on(qErrorEntity.errorId.eq(qErrorDetailEntity.errorId))
                            .leftJoin(qErrorGroupEntity)
                            .on(qErrorDetailEntity.errorGroupId.eq(qErrorGroupEntity.errorGroupId))
                            .orderBy(new OrderSpecifier<>(errorInput.getSortOrder(), qErrorGroupEntity.errorGroupName));
                    } else {
                        // Dynamic col
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qErrorEntity.errorId)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.ERROR))
                                    .and(qKeyValueEntityV22.columnPropertyEntity.eq(column))
                            );
                        switch (column.getDataType()) {
                            case Contants.INT_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorInput.getSortOrder(), qKeyValueEntityV22.intValue));
                                break;
                            case Contants.FLOAT_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorInput.getSortOrder(), qKeyValueEntityV22.doubleValue));
                                break;
                            case Contants.STRING_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorInput.getSortOrder(), qKeyValueEntityV22.stringValue));
                                break;
                            case Contants.JSON_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorInput.getSortOrder(), qKeyValueEntityV22.jsonValue));
                                break;
                            case Contants.DATE_VALUE:
                                query.orderBy(new OrderSpecifier<>(errorInput.getSortOrder(), qKeyValueEntityV22.dateValue));
                                break;
                        }
                    }
                }
            }
            if (checkSort) {
                Path<Object> fieldPath = Expressions.path(Object.class, qErrorEntity, errorInput.getSortProperty());
                query.orderBy(new OrderSpecifier(errorInput.getSortOrder(), fieldPath));
            }
        }

        if (errorInput.getPageSize() == 0) {
            //            if (!StringUtil.isEmpty(errorInput.getSortProperty())) {
            //                Path<Object> fieldPath = Expressions.path(Object.class, qErrorEntity, errorInput.getSortProperty());
            //                query.orderBy(new OrderSpecifier(errorInput.getSortOrder(), fieldPath));
            //            }
            errorEntityList = query.fetch();

            List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.ERROR);

            Map<Integer, List<KeyValueEntityV2>> errorPropertyMap = properties
                .stream()
                .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

            for (ErrorEntity errorEntity : errorEntityList) {
                //logger.info("Giá giạ errorID là: " + errorId);
                JPAQuery queryErrorGroup = new JPAQueryFactory(entityManager)
                    .select(qErrorGroupEntity.errorGroupName)
                    .from(qErrorGroupEntity)
                    .join(qErrorDetailEntity)
                    .on(qErrorGroupEntity.errorGroupId.eq(qErrorDetailEntity.errorGroupId));
                BooleanBuilder booleanBuilderErrorGroup = new BooleanBuilder();
                booleanBuilderErrorGroup.and(qErrorGroupEntity.isActive.eq(1));
                booleanBuilderErrorGroup.and(qErrorDetailEntity.errorId.eq(errorEntity.getErrorId()));
                queryErrorGroup.where(booleanBuilderErrorGroup);
                List<String> errorGroupName = queryErrorGroup.fetch();
                errorDTOS.add(ErrorMapper.entytoDTOMap(errorEntity, errorPropertyMap.get(errorEntity.getErrorId()), errorGroupName));
            }

            List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
                Contants.EntityType.ERROR
            );

            return new PageResponse<List<ErrorDTO>>(errorEntityList.size()).success().data(errorDTOS).columns(keyDictionaryDTOS);
        }

        //        if (!StringUtil.isEmpty(errorInput.getSortProperty())) {
        //            Path<Object> fieldPath = Expressions.path(Object.class, qErrorEntity, errorInput.getSortProperty());
        //            query.orderBy(new OrderSpecifier(errorInput.getSortOrder(), fieldPath));
        //        }

        errorEntityList = query.fetch();

        long count = query.fetchCount();

        Pageable pageable = PageRequest.of(errorInput.getPageNumber(), errorInput.getPageSize());
        Page<ErrorEntity> errorAndErrrGroupPage = new PageImpl<>(errorEntityList, pageable, count);

        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.ERROR,
            errorAndErrrGroupPage.getContent().stream().map(ErrorEntity::getErrorId).collect(Collectors.toList())
        );

        Map<Integer, List<KeyValueEntityV2>> errorPropertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        for (ErrorEntity errorEntity : errorAndErrrGroupPage.getContent()) {
            JPAQuery queryErrorGroup = new JPAQueryFactory(entityManager)
                .select(qErrorGroupEntity.errorGroupName)
                .from(qErrorGroupEntity)
                .join(qErrorDetailEntity)
                .on(qErrorGroupEntity.errorGroupId.eq(qErrorDetailEntity.errorGroupId));
            BooleanBuilder booleanBuilderErrorGroup = new BooleanBuilder();
            booleanBuilderErrorGroup.and(qErrorGroupEntity.isActive.eq(1));
            booleanBuilderErrorGroup.and(qErrorDetailEntity.errorId.eq(errorEntity.getErrorId()));
            queryErrorGroup.where(booleanBuilderErrorGroup);
            List<String> errorGroupName = queryErrorGroup.fetch();
            errorDTOS.add(ErrorMapper.entytoDTOMap(errorEntity, errorPropertyMap.get(errorEntity.getErrorId()), errorGroupName));
        }

        List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.ERROR
        );

        return new PageResponse<List<ErrorDTO>>(errorAndErrrGroupPage.getTotalElements())
            .success()
            .data(errorDTOS)
            .columns(keyDictionaryDTOS);
    }

    public List<String> getAutoComplete(PageFilterInput<ErrorFilter> input) {
        List<ErrorDTO> errorDTOList = (List<ErrorDTO>) getErrorWithPaging(input).getData();
        String common = input.getCommon();
        List<String> searchCommon = new ArrayList<>();
        for (ErrorDTO errorDTO : errorDTOList) {
            if (
                errorDTO.getErrorCode().toLowerCase().contains(common.toLowerCase()) && !searchCommon.contains(errorDTO.getErrorCode())
            ) searchCommon.add(errorDTO.getErrorCode());
            if (
                errorDTO.getErrorName().toLowerCase().contains(common.toLowerCase()) && !searchCommon.contains(errorDTO.getErrorName())
            ) searchCommon.add(errorDTO.getErrorName());
            if (errorDTO.getErrorGroup() != null) {
                for (String errorGroupName : errorDTO.getErrorGroup()) {
                    if (
                        !StringUtil.isEmpty(errorGroupName) &&
                        errorGroupName.toLowerCase().contains(common.toLowerCase()) &&
                        !searchCommon.contains(errorGroupName)
                    ) searchCommon.add(errorGroupName);
                }
            }
            if (
                !StringUtil.isEmpty(errorDTO.getErrorDesc()) &&
                errorDTO.getErrorDesc().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(errorDTO.getErrorDesc())
            ) searchCommon.add(errorDTO.getErrorCode());
            if (
                !StringUtil.isEmpty(errorDTO.getErrorType()) &&
                errorDTO.getErrorType().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(errorDTO.getErrorType())
            ) searchCommon.add(errorDTO.getErrorType());
            if (errorDTO.getErrorMap() != null) {
                for (String key : errorDTO.getErrorMap().keySet()) {
                    if (
                        !StringUtil.isEmpty(errorDTO.getErrorMap().get(key)) &&
                        errorDTO.getErrorMap().get(key).toLowerCase().contains(common.toLowerCase()) &&
                        !searchCommon.contains(errorDTO.getErrorMap().get(key))
                    ) searchCommon.add(errorDTO.getErrorCode());
                }
            }
            if (searchCommon.size() >= 10) break;
        }
        return searchCommon;
    }

    @Transactional
    public CommonResponse createError(ErrorDTO errorDTO) {
        QErrorGroupEntity qErrorGroupEntity = QErrorGroupEntity.errorGroupEntity;
        ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorDTO.getErrorCode());
        if (errorEntity != null) {
            throw new CustomException(HttpStatus.CONFLICT, "Đã tồn tại lỗi có mã: " + errorDTO.getErrorCode());
        } else {
            //try {
            errorEntity = new ErrorEntity();
            errorEntity.setErrorCode(errorDTO.getErrorCode());
            errorEntity.setErrorName(errorDTO.getErrorName());
            errorEntity.setErrorDesc(errorDTO.getErrorDesc());
            errorEntity.setErrorType(errorDTO.getErrorType());
            errorEntity.setErrorStatus(Contants.ErrorStatus.getStatus(errorDTO.getErrorStatus()));
            JPAQuery query = new JPAQueryFactory(entityManager)
                .selectFrom(qErrorGroupEntity)
                .where(qErrorGroupEntity.errorGroupName.in(errorDTO.getErrorGroup()))
                .where(qErrorGroupEntity.isActive.eq(1));
            List<ErrorGroupEntity> errorGroupList = query.fetch();
            Set<ErrorGroupEntity> errorGroupEntities = new HashSet<>(errorGroupList);
            errorEntity.setErrorGroupEntities(errorGroupEntities);
            errorEntity.setIsActive(1);
            ErrorEntity errorEntitySave = errorResponesitory.save(errorEntity);
            if (!errorDTO.getErrorMap().isEmpty()) {
                keyValueService.createUpdateKeyValueOfEntity(
                    errorEntitySave.getErrorId(),
                    errorDTO.getErrorMap(),
                    Contants.EntityType.ERROR,
                    false
                );
            }

            businessLogService.insertInsertionLog(
                errorEntity.getErrorId(),
                Contants.EntityType.ERROR,
                ErrorMapper.toLogDetail(errorEntity, errorDTO.getErrorMap())
            );

            return new CommonResponse().success();
            //            } catch (Exception e) {
            //                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hơp lệ");
            //            }
        }
    }

    @Transactional
    public CommonResponse updateError(String errorCode, ErrorDTO errorDTO) {
        QErrorGroupEntity qErrorGroupEntity = QErrorGroupEntity.errorGroupEntity;
        ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorCode);
        if (errorEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Lỗi không tồn tại");
        } else {
            try {
                ErrorEntity oldValue = new ErrorEntity(errorEntity);
                errorEntity.setErrorCode(errorDTO.getErrorCode());
                errorEntity.setErrorName(errorDTO.getErrorName());
                errorEntity.setErrorDesc(errorDTO.getErrorDesc());
                errorEntity.setErrorType(errorDTO.getErrorType());
                errorEntity.setErrorStatus(Contants.ErrorStatus.getStatus(errorDTO.getErrorStatus()));
                errorEntity.setIsActive(1);
                JPAQuery query = new JPAQueryFactory(entityManager)
                    .selectFrom(qErrorGroupEntity)
                    .where(qErrorGroupEntity.errorGroupName.in(errorDTO.getErrorGroup()))
                    .where(qErrorGroupEntity.isActive.eq(1));
                List<ErrorGroupEntity> errorGroupList = query.fetch();
                Set<ErrorGroupEntity> errorGroupEntities = new HashSet<>(errorGroupList);
                errorEntity.setErrorGroupEntities(errorGroupEntities);
                ErrorEntity saveError = errorResponesitory.save(errorEntity);

                BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                    saveError.getId(),
                    Contants.EntityType.ERROR,
                    ErrorMapper.toUpdateLogDetail(oldValue, errorEntity)
                );

                if (!errorDTO.getErrorMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntityWithLog(
                        errorEntity.getErrorId(),
                        errorDTO.getErrorMap(),
                        Contants.EntityType.ERROR,
                        true,
                        logEntity
                    );
                }

                return new CommonResponse().success("Cập nhật thành công");
            } catch (Exception e) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
            }
        }
    }

    public CommonResponse saveorUpdateListError(List<ErrorDTO> errorDTOList) {
        List<String> erorrCodeList = new ArrayList<>();
        List<String> erorrCodeDuplicate = new ArrayList<>();

        for (ErrorDTO errorDTO : errorDTOList) {
            if (erorrCodeList.contains(errorDTO.getErrorCode()) && !erorrCodeDuplicate.contains(errorDTO.getErrorCode())) {
                erorrCodeDuplicate.add(errorDTO.getErrorCode());
            }
            erorrCodeList.add(errorDTO.getErrorCode());

            ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorDTO.getErrorCode());
            if (errorEntity != null) {
                ErrorEntity oldValue = new ErrorEntity(errorEntity);
                Set<ErrorGroupEntity> errorEntitySet = errorEntity.getErrorGroupEntities();
                ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(
                    errorDTO.getErrorGroup().get(0).toString()
                );
                errorEntitySet.add(errorGroupEntity);
                errorEntity.setErrorGroupEntities(errorEntitySet);
                errorEntity.setIsActive(1);
                ErrorEntity saveError = errorResponesitory.save(errorEntity);

                BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                    saveError.getId(),
                    Contants.EntityType.ERROR,
                    ErrorMapper.toUpdateLogDetail(oldValue, errorEntity)
                );

                if (!errorDTO.getErrorMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntityWithLog(
                        errorEntity.getErrorId(),
                        errorDTO.getErrorMap(),
                        Contants.EntityType.ERROR,
                        false,
                        logEntity
                    );
                }
            } else {
                try {
                    errorEntity = new ErrorEntity();
                    errorEntity.setErrorCode(errorDTO.getErrorCode());
                    errorEntity.setErrorName(errorDTO.getErrorName());
                    errorEntity.setErrorDesc(errorDTO.getErrorDesc());
                    errorEntity.setErrorType(errorDTO.getErrorType());
                    errorEntity.setErrorStatus(Contants.ErrorStatus.getStatus(errorDTO.getErrorStatus()));
                    Set<ErrorGroupEntity> errorEntitySet = new HashSet<>();
                    ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(
                        errorDTO.getErrorGroup().get(0).toString()
                    );
                    errorEntitySet.add(errorGroupEntity);
                    errorEntity.setErrorGroupEntities(errorEntitySet);
                    errorEntity.setIsActive(1);
                    errorResponesitory.save(errorEntity);

                    if (!errorDTO.getErrorMap().isEmpty()) {
                        keyValueService.createUpdateKeyValueOfEntity(
                            errorEntity.getErrorId(),
                            errorDTO.getErrorMap(),
                            Contants.EntityType.ERROR,
                            true
                        );
                    }
                } catch (Exception ex) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
                }
            }
        }

        if (erorrCodeDuplicate != null && erorrCodeDuplicate.size() > 0) {
            String errorCodeString = "";
            for (int i = 0; i < erorrCodeDuplicate.size() - 1; i++) {
                errorCodeString = errorCodeString + erorrCodeDuplicate.get(i) + ", ";
            }
            errorCodeString = errorCodeString + erorrCodeDuplicate.get(erorrCodeDuplicate.size() - 1) + ".";
            throw new CustomException(HttpStatus.CONFLICT, "Có các mã lỗi đang trùng nhau là: " + errorCodeString);
        }

        return new CommonResponse().success();
    }

    @Transactional
    public CommonResponse deleteError(String errorCode) {
        ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorCode);
        if (errorEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy lỗi");
        } else {
            errorEntity.setIsActive(0);
            errorResponesitory.save(errorEntity);
            businessLogService.insertDeleteLog(
                errorEntity.getId(),
                Contants.EntityType.ERROR,
                ErrorMapper.toDeletionLogDetail(errorEntity)
            );
            return new CommonResponse().success("Đa xóa thành công " + errorCode);
        }
    }

    @Transactional
    public CommonResponse importErrorFromExcel(MultipartFile file) throws IOException, ParseException {
        List<ErrorDTO> errorDTOList = xlsxExcelHandle.readErrorFromExcel(file.getInputStream());
        for (ErrorDTO errorDTO : errorDTOList) {
            ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorDTO.getErrorCode());
            if (errorEntity == null) {
                this.createError(errorDTO);
            } else {
                throw new CustomException(HttpStatus.CONFLICT, "Đã tồn tại mã lỗi:" + errorDTO.getErrorCode());
            }
        }
        return new CommonResponse().success();
    }
}
