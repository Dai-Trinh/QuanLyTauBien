package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.domain.QJobEntity;
import com.facenet.mdm.domain.QKeyValueEntityV2;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.JobCustomRepository;
import com.facenet.mdm.service.dto.JobDTO;
import com.facenet.mdm.service.dto.JobQmsDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class JobRepositoryImpl implements JobCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ColumnPropertyRepository columnPropertyRepository;

    @Override
    public Page<JobEntity> getAllJob(PageFilterInput<JobDTO> input, Pageable pageable, List<String> jobCodes) {
        JobDTO jobDTO = input.getFilter();
        String common = input.getCommon();
        QJobEntity qJobEntity = QJobEntity.jobEntity;

        JPAQuery<JobEntity> query = new JPAQueryFactory(em).selectFrom(qJobEntity);
        if (pageable.isPaged()) {
            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qJobEntity.isActive.eq(true));
        booleanBuilder.and(qJobEntity.productionStageCode.isNull());

        //        if (jobCodes != null && jobCodes.size() > 0) {
        //
        //        }
        booleanBuilder.and(qJobEntity.jobCode.in(jobCodes));

        if (jobDTO.getStatus() != null) {
            booleanBuilder.and(qJobEntity.status.eq(jobDTO.getStatus()));
        }

        //        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;
        //        JPAQuery<Integer> propertySubQueryCommon = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
        //        BooleanBuilder commonDynamicBooleanBuilder = new BooleanBuilder();
        //        commonDynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
        //        if (!StringUtils.isEmpty(input.getCommon())) {
        //            commonDynamicBooleanBuilder.and(
        //                qKeyValueEntityV2.intValue
        //                    .stringValue()
        //                    .containsIgnoreCase(input.getCommon())
        //                    .or(qKeyValueEntityV2.doubleValue.stringValue().containsIgnoreCase(input.getCommon()))
        //                    .or(qKeyValueEntityV2.dateValue.stringValue().containsIgnoreCase(input.getCommon()))
        //                    .or(qKeyValueEntityV2.booleanValue.stringValue().containsIgnoreCase(input.getCommon()))
        //                    .or(qKeyValueEntityV2.jsonValue.containsIgnoreCase(input.getCommon()))
        //                    .or(qKeyValueEntityV2.stringValue.containsIgnoreCase(input.getCommon()))
        //            );
        //            propertySubQueryCommon.where(commonDynamicBooleanBuilder);
        //        }
        //        JPAQuery<JobEntity> query = new JPAQueryFactory(em).selectFrom(qJobEntity);
        //        if (pageable.isPaged()) {
        //            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        //        }
        //        BooleanBuilder booleanBuilder = new BooleanBuilder();
        //        booleanBuilder.and(qJobEntity.isActive.eq(true));
        //        booleanBuilder.and(qJobEntity.productionStageCode.isNull());
        //        if (!StringUtils.isEmpty(common)) {
        //            booleanBuilder.and(
        //                qJobEntity.productionStageCode
        //                    .containsIgnoreCase(common)
        //                    .or(qJobEntity.jobCode.containsIgnoreCase(common))
        //                    .or(qJobEntity.jobName.containsIgnoreCase(common))
        //                    .or(qJobEntity.description.containsIgnoreCase(common))
        //                    .or(qJobEntity.id.in(propertySubQueryCommon))
        //            );
        //        }
        //        //        if (!StringUtils.isEmpty(jobDTO.getProductionStageCode())) {
        //        //            booleanBuilder.and(qJobEntity.productionStageCode.equalsIgnoreCase(jobDTO.getProductionStageCode()));
        //        //        }
        //        if (!StringUtils.isEmpty(jobDTO.getJobCode())) {
        //            booleanBuilder.and(qJobEntity.jobCode.containsIgnoreCase(jobDTO.getJobCode()));
        //        }
        //        if (!StringUtils.isEmpty(jobDTO.getJobName())) {
        //            booleanBuilder.and(qJobEntity.jobName.containsIgnoreCase(jobDTO.getJobName()));
        //        }
        //        if (!StringUtils.isEmpty(jobDTO.getJobDescription())) {
        //            booleanBuilder.and(qJobEntity.description.containsIgnoreCase(jobDTO.getJobDescription()));
        //        }
        //        if (jobDTO.getStatus() != null) {
        //            booleanBuilder.and(qJobEntity.status.eq(jobDTO.getStatus()));
        //        }
        //        if (!jobDTO.getJobMap().isEmpty()) {
        //            Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
        //                Contants.EntityType.JOB
        //            );
        //            for (String keyName : jobDTO.getJobMap().keySet()) {
        //                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
        //                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
        //                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
        //                String value = jobDTO.getJobMap().get(keyName);
        //                if (!StringUtils.isEmpty(value)) {
        //                    dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(keyName));
        //                    switch (columnPropertyEntities.get(keyName).getDataType()) {
        //                        case Contants.INT_VALUE:
        //                            dynamicBooleanBuilder.and(qKeyValueEntityV2.intValue.stringValue().containsIgnoreCase(value));
        //                            break;
        //                        case Contants.FLOAT_VALUE:
        //                            dynamicBooleanBuilder.and(qKeyValueEntityV2.doubleValue.stringValue().containsIgnoreCase(value));
        //                            break;
        //                        case Contants.STRING_VALUE:
        //                            dynamicBooleanBuilder.and(qKeyValueEntityV2.stringValue.containsIgnoreCase(value));
        //                            break;
        //                        case Contants.JSON_VALUE:
        //                            dynamicBooleanBuilder.and(qKeyValueEntityV2.jsonValue.containsIgnoreCase(value));
        //                            break;
        //                        case Contants.DATE_VALUE:
        //                            dynamicBooleanBuilder.and(qKeyValueEntityV2.dateValue.stringValue().containsIgnoreCase(value));
        //                            break;
        //                    }
        //                    propertySubQuery.where(dynamicBooleanBuilder);
        //                    booleanBuilder.and(qJobEntity.id.in(propertySubQuery));
        //                }
        //            }
        //        }
        if (!StringUtils.isEmpty(input.getSortProperty())) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.JOB);
            boolean isSortByColumn = false;
            for (ColumnPropertyEntity column : columns) {
                if (input.getSortProperty().equals(column.getKeyName())) {
                    isSortByColumn = true;
                    break;
                }
            }
            // Valid sort column name
            if (isSortByColumn) {
                for (ColumnPropertyEntity column : columns) {
                    if (input.getSortProperty().equals(column.getKeyName())) {
                        if (column.getIsFixed() == 0) {
                            // Dynamic col
                            QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                            query
                                .leftJoin(qKeyValueEntityV22)
                                .on(
                                    qKeyValueEntityV22.entityKey
                                        .eq(qJobEntity.id)
                                        .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.JOB))
                                        .and(qKeyValueEntityV22.columnPropertyEntity.eq(column))
                                );
                            switch (column.getDataType()) {
                                case Contants.INT_VALUE:
                                    query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.intValue));
                                    break;
                                case Contants.FLOAT_VALUE:
                                    query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.doubleValue));
                                    break;
                                case Contants.STRING_VALUE:
                                    query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.stringValue));
                                    break;
                                case Contants.JSON_VALUE:
                                    query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.jsonValue));
                                    break;
                                case Contants.DATE_VALUE:
                                    query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.dateValue));
                                    break;
                            }
                        } else if (column.getIsFixed() == 1) {
                            // Default col
                            Path<Object> fieldPath = Expressions.path(Object.class, qJobEntity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                if (input.getSortProperty().equals("productionStageCode")) {
                    Path<Object> fieldPath = Expressions.path(Object.class, qJobEntity, "jobCode");
                    query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                } else if (input.getSortProperty().equals("productionStageName")) {
                    Path<Object> fieldPath = Expressions.path(Object.class, qJobEntity, "jobName");
                    query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                } else {
                    // Default col
                    Path<Object> fieldPath = Expressions.path(Object.class, qJobEntity, input.getSortProperty());
                    query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                }
            }
        }
        query.where(booleanBuilder);
        List<JobEntity> result = query.fetch();
        //        long total = new JPAQueryFactory(em)
        //            .select(qJobEntity.id.count())
        //            .from(qJobEntity)
        //            .where(booleanBuilder)
        //            .fetchOne();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }

    @Override
    public List<JobEntity> getAllJobForQms(JobQmsDTO jobDTO) {
        QJobEntity qJobEntity = QJobEntity.jobEntity;
        JPAQuery<JobEntity> query = new JPAQueryFactory(em).selectFrom(qJobEntity);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qJobEntity.isActive.eq(true));

        if (!StringUtils.isEmpty(jobDTO.getProductionStageCode())) {
            booleanBuilder.and(qJobEntity.productionStageCode.equalsIgnoreCase(jobDTO.getProductionStageCode()));
        }
        if (!StringUtils.isEmpty(jobDTO.getJobCode())) {
            booleanBuilder.and(qJobEntity.jobCode.containsIgnoreCase(jobDTO.getJobCode()));
        }
        if (!StringUtils.isEmpty(jobDTO.getJobName())) {
            booleanBuilder.and(qJobEntity.jobName.containsIgnoreCase(jobDTO.getJobName()));
        }
        if (jobDTO.getStatus() != null) {
            booleanBuilder.and(qJobEntity.status.eq(jobDTO.getStatus()));
        }
        if (!StringUtils.isEmpty(jobDTO.getCreatedBy())) {
            booleanBuilder.and(qJobEntity.createdBy.eq(jobDTO.getCreatedBy()));
        }
        if (jobDTO.getCreatedAt() != null) {
            Instant createdAt = jobDTO.getCreatedAt().toInstant();
            Instant createdAt2 = jobDTO
                .getCreatedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
            booleanBuilder.and(qJobEntity.createdAt.between(createdAt, createdAt2));
        }

        query.where(booleanBuilder);
        List<JobEntity> result = query.fetch();
        //        long total = new JPAQueryFactory(em)
        //            .select(qJobEntity.id.count())
        //            .from(qJobEntity)
        //            .where(booleanBuilder)
        //            .fetchOne();
        return result;
    }

    @Override
    public List<JobEntity> getListJobCode(PageFilterInput<JobDTO> input) {
        JobDTO jobDTO = input.getFilter();
        String common = input.getCommon();
        QJobEntity qJobEntity = QJobEntity.jobEntity;

        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;
        JPAQuery<Integer> propertySubQueryCommon = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
        BooleanBuilder commonDynamicBooleanBuilder = new BooleanBuilder();
        commonDynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
        if (!StringUtils.isEmpty(input.getCommon())) {
            commonDynamicBooleanBuilder.and(
                qKeyValueEntityV2.intValue
                    .stringValue()
                    .containsIgnoreCase(input.getCommon())
                    .or(qKeyValueEntityV2.doubleValue.stringValue().containsIgnoreCase(input.getCommon()))
                    .or(qKeyValueEntityV2.dateValue.stringValue().containsIgnoreCase(input.getCommon()))
                    .or(qKeyValueEntityV2.booleanValue.stringValue().containsIgnoreCase(input.getCommon()))
                    .or(qKeyValueEntityV2.jsonValue.containsIgnoreCase(input.getCommon()))
                    .or(qKeyValueEntityV2.stringValue.containsIgnoreCase(input.getCommon()))
            );
            propertySubQueryCommon.where(commonDynamicBooleanBuilder);
        }
        JPAQuery<JobEntity> query = new JPAQueryFactory(em).selectFrom(qJobEntity);

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qJobEntity.isActive.eq(true));
        //booleanBuilder.and(qJobEntity.productionStageCode.isNull());
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qJobEntity.productionStageCode
                    .containsIgnoreCase(common)
                    .or(qJobEntity.jobCode.containsIgnoreCase(common))
                    .or(qJobEntity.jobName.containsIgnoreCase(common))
                    .or(qJobEntity.description.containsIgnoreCase(common))
                    .or(qJobEntity.id.in(propertySubQueryCommon))
            );
        }
        //        if (!StringUtils.isEmpty(jobDTO.getProductionStageCode())) {
        //            booleanBuilder.and(qJobEntity.productionStageCode.equalsIgnoreCase(jobDTO.getProductionStageCode()));
        //        }
        if (!StringUtils.isEmpty(jobDTO.getJobCode())) {
            booleanBuilder.and(qJobEntity.jobCode.containsIgnoreCase(jobDTO.getJobCode()));
        }
        if (!StringUtils.isEmpty(jobDTO.getJobName())) {
            booleanBuilder.and(qJobEntity.jobName.containsIgnoreCase(jobDTO.getJobName()));
        }
        if (!StringUtils.isEmpty(jobDTO.getJobDescription())) {
            booleanBuilder.and(qJobEntity.description.containsIgnoreCase(jobDTO.getJobDescription()));
        }
        //        if (jobDTO.getStatus() != null) {
        //            booleanBuilder.and(qJobEntity.status.eq(jobDTO.getStatus()));
        //        }
        if (!jobDTO.getJobMap().isEmpty()) {
            Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
                Contants.EntityType.PRODUCTIONSTAGE
            );
            for (String keyName : jobDTO.getJobMap().keySet()) {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                String value = jobDTO.getJobMap().get(keyName);
                if (!StringUtils.isEmpty(value)) {
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(keyName));
                    switch (columnPropertyEntities.get(keyName).getDataType()) {
                        case Contants.INT_VALUE:
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.intValue.stringValue().containsIgnoreCase(value));
                            break;
                        case Contants.FLOAT_VALUE:
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.doubleValue.stringValue().containsIgnoreCase(value));
                            break;
                        case Contants.STRING_VALUE:
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.stringValue.containsIgnoreCase(value));
                            break;
                        case Contants.JSON_VALUE:
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.jsonValue.containsIgnoreCase(value));
                            break;
                        case Contants.DATE_VALUE:
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                            String[] date = value.split(" ");

                            LocalDate startDate = Instant
                                .from(formatter.parse(date[0].trim()))
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                            LocalDate endDate = Instant.from(formatter.parse(date[1].trim())).atZone(ZoneId.systemDefault()).toLocalDate();
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.dateValue.between(startDate, endDate));
                            break;
                    }
                    propertySubQuery.where(dynamicBooleanBuilder);
                    booleanBuilder.and(qJobEntity.id.in(propertySubQuery));
                }
            }
        }
        query.where(booleanBuilder);
        List<JobEntity> result = query.fetch();
        return result;
    }
}
