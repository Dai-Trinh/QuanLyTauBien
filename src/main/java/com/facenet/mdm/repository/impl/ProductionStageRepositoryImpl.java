package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.ProductionStageCustomRepository;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.StageQmsDTO;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
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

public class ProductionStageRepositoryImpl implements ProductionStageCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ColumnPropertyRepository columnPropertyRepository;

    @Override
    public Page<ProductionStageEntity> getAllStage(PageFilterInput<ProductionStageDTO> input, Pageable pageable) {
        ProductionStageDTO productionStageDTO = input.getFilter();
        String common = input.getCommon();
        QProductionStageEntity qProductionStageEntity = QProductionStageEntity.productionStageEntity;

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
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        JPAQuery<ProductionStageEntity> query = new JPAQueryFactory(em).select(qProductionStageEntity).from(qProductionStageEntity);
        if (pageable.isPaged()) {
            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        booleanBuilder.and(qProductionStageEntity.isActive.eq(true));
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qProductionStageEntity.productionStageCode
                    .containsIgnoreCase(common)
                    .or(qProductionStageEntity.productionStageName.containsIgnoreCase(common))
                    .or(qProductionStageEntity.description.containsIgnoreCase(common))
                    .or(qProductionStageEntity.id.in(propertySubQueryCommon))
            );
        }
        if (!StringUtils.isEmpty(productionStageDTO.getProductionStageCode())) {
            booleanBuilder.and(qProductionStageEntity.productionStageCode.containsIgnoreCase(productionStageDTO.getProductionStageCode()));
        }
        if (!StringUtils.isEmpty(productionStageDTO.getProductionStageName())) {
            booleanBuilder.and(qProductionStageEntity.productionStageName.containsIgnoreCase(productionStageDTO.getProductionStageName()));
        }
        if (!StringUtils.isEmpty(productionStageDTO.getDescription())) {
            booleanBuilder.and(qProductionStageEntity.description.containsIgnoreCase(productionStageDTO.getDescription()));
        }
        if (productionStageDTO.getStatus() != null) {
            booleanBuilder.and(qProductionStageEntity.status.eq(productionStageDTO.getStatus()));
        }
        if (!productionStageDTO.getStageMap().isEmpty()) {
            Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
                Contants.EntityType.PRODUCTIONSTAGE
            );
            for (String keyName : productionStageDTO.getStageMap().keySet()) {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                String value = productionStageDTO.getStageMap().get(keyName);
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
                    booleanBuilder.and(qProductionStageEntity.id.in(propertySubQuery));
                }
            }
        }
        if (!StringUtils.isEmpty(input.getSortProperty())) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.PRODUCTIONSTAGE);
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
                                        .eq(qProductionStageEntity.id)
                                        .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.PRODUCTIONSTAGE))
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
                            Path<Object> fieldPath = Expressions.path(Object.class, qProductionStageEntity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                Path<Object> fieldPath = Expressions.path(Object.class, qProductionStageEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }
        query.where(booleanBuilder);
        List<ProductionStageEntity> result = query.fetch();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }

    @Override
    public List<ProductionStageEntity> getAllStageForQms(
        StageQmsDTO productionStageDTO,
        String common,
        String sortProperty,
        Order sortOrder
    ) {
        QProductionStageEntity qProductionStageEntity = QProductionStageEntity.productionStageEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        JPAQuery<ProductionStageEntity> query = new JPAQueryFactory(em).select(qProductionStageEntity).from(qProductionStageEntity);

        booleanBuilder.and(qProductionStageEntity.isActive.eq(true));
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qProductionStageEntity.productionStageCode
                    .containsIgnoreCase(common)
                    .or(qProductionStageEntity.productionStageName.containsIgnoreCase(common))
                    .or(qProductionStageEntity.description.containsIgnoreCase(common))
                    .or(qProductionStageEntity.createdBy.containsIgnoreCase(common))
            );
        }
        if (productionStageDTO.getId() != null) {
            booleanBuilder.and(qProductionStageEntity.id.eq(productionStageDTO.getId()));
        }
        if (!StringUtils.isEmpty(productionStageDTO.getProductionStageCode())) {
            booleanBuilder.and(qProductionStageEntity.productionStageCode.containsIgnoreCase(productionStageDTO.getProductionStageCode()));
        }
        if (!StringUtils.isEmpty(productionStageDTO.getProductionStageName())) {
            booleanBuilder.and(qProductionStageEntity.productionStageName.containsIgnoreCase(productionStageDTO.getProductionStageName()));
        }
        if (!StringUtils.isEmpty(productionStageDTO.getDescription())) {
            booleanBuilder.and(qProductionStageEntity.description.containsIgnoreCase(productionStageDTO.getDescription()));
        }
        if (productionStageDTO.getStatus() != null) {
            booleanBuilder.and(qProductionStageEntity.status.eq(productionStageDTO.getStatus()));
        }
        if (!StringUtils.isEmpty(productionStageDTO.getCreatedBy())) {
            booleanBuilder.and(qProductionStageEntity.createdBy.eq(productionStageDTO.getCreatedBy()));
        }
        if (productionStageDTO.getCreatedAt() != null) {
            Instant createdAt = productionStageDTO.getCreatedAt().toInstant();
            Instant createdAt2 = productionStageDTO
                .getCreatedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
            booleanBuilder.and(qProductionStageEntity.createdAt.between(createdAt, createdAt2));
        }
        if (!StringUtils.isEmpty(sortProperty)) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.PRODUCTIONSTAGE);
            boolean isSortByColumn = false;
            for (ColumnPropertyEntity column : columns) {
                if (sortProperty.equals(column.getKeyName())) {
                    isSortByColumn = true;
                    break;
                }
            }
            // Valid sort column name
            if (isSortByColumn) {
                for (ColumnPropertyEntity column : columns) {
                    if (sortProperty.equals(column.getKeyName())) {
                        if (column.getIsFixed() == 0) {
                            // Dynamic col
                            QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                            query
                                .leftJoin(qKeyValueEntityV22)
                                .on(
                                    qKeyValueEntityV22.entityKey
                                        .eq(qProductionStageEntity.id)
                                        .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.PRODUCTIONSTAGE))
                                        .and(qKeyValueEntityV22.columnPropertyEntity.eq(column))
                                );
                            switch (column.getDataType()) {
                                case Contants.INT_VALUE:
                                    query.orderBy(new OrderSpecifier<>(sortOrder, qKeyValueEntityV22.intValue));
                                    break;
                                case Contants.FLOAT_VALUE:
                                    query.orderBy(new OrderSpecifier<>(sortOrder, qKeyValueEntityV22.doubleValue));
                                    break;
                                case Contants.STRING_VALUE:
                                    query.orderBy(new OrderSpecifier<>(sortOrder, qKeyValueEntityV22.stringValue));
                                    break;
                                case Contants.JSON_VALUE:
                                    query.orderBy(new OrderSpecifier<>(sortOrder, qKeyValueEntityV22.jsonValue));
                                    break;
                                case Contants.DATE_VALUE:
                                    query.orderBy(new OrderSpecifier<>(sortOrder, qKeyValueEntityV22.dateValue));
                                    break;
                            }
                        } else if (column.getIsFixed() == 1) {
                            // Default col
                            Path<Object> fieldPath = Expressions.path(Object.class, qProductionStageEntity, sortProperty);
                            query.orderBy(new OrderSpecifier(sortOrder, fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                if (sortProperty.equals("productionStageCode")) {
                    Path<Object> fieldPath = Expressions.path(Object.class, qProductionStageEntity, "jobCode");
                    query.orderBy(new OrderSpecifier(sortOrder, fieldPath));
                } else if (sortProperty.equals("productionStageName")) {
                    Path<Object> fieldPath = Expressions.path(Object.class, qProductionStageEntity, "jobName");
                    query.orderBy(new OrderSpecifier(sortOrder, fieldPath));
                } else {
                    // Default col
                    Path<Object> fieldPath = Expressions.path(Object.class, qProductionStageEntity, sortProperty);
                    query.orderBy(new OrderSpecifier(sortOrder, fieldPath));
                }
            }
        }

        query.where(booleanBuilder);

        //        long total = new JPAQueryFactory(em)
        //            .select(qProductionStageEntity.id.count())
        //            .from(qProductionStageEntity)
        //            .where(booleanBuilder)
        //            .fetchOne();
        return query.fetch();
    }

    @Override
    public List<String> getAutoComplete(String value, String keyName) {
        PathBuilder<ProductionStageEntity> path = new PathBuilder<>(ProductionStageEntity.class, "entity");
        return new JPAQueryFactory(em)
            .select(path.getString(keyName))
            .from(path)
            .where(path.getString(keyName).containsIgnoreCase(value))
            .limit(10)
            .fetch();
    }
}
