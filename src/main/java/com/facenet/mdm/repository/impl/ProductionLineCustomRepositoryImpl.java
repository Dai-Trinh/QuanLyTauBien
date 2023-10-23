package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.ProductionLineCustomRepository;
import com.facenet.mdm.service.dto.ProductionLineDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import liquibase.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProductionLineCustomRepositoryImpl implements ProductionLineCustomRepository {

    private final EntityManager entityManager;

    private ColumnPropertyRepository columnPropertyRepository;

    public ProductionLineCustomRepositoryImpl(EntityManager entityManager, ColumnPropertyRepository columnPropertyRepository) {
        this.entityManager = entityManager;
        this.columnPropertyRepository = columnPropertyRepository;
    }

    private String formatDoubleValue(double value) {
        if (value == (int) value) {
            return String.format("%d", (int) value);
        } else {
            return String.format("%s", value);
        }
    }

    @Override
    public Page<ProductionLineEntity> getAll(PageFilterInput<ProductionLineDTO> input, Pageable pageable) {
        ProductionLineDTO filter = input.getFilter();

        QProductionLineEntity qProductionLineEntity = QProductionLineEntity.productionLineEntity;
        QProductionLineTypeEntity qProductionLineTypeEntity = QProductionLineTypeEntity.productionLineTypeEntity;
        JPAQuery<ProductionLineEntity> query = new JPAQueryFactory(entityManager)
            .selectFrom(qProductionLineEntity)
            .leftJoin(qProductionLineTypeEntity)
            .on(qProductionLineEntity.productionLineType.eq(qProductionLineTypeEntity));

        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qProductionLineEntity.isActive.isTrue());

        //lấy ra ColumnPropertyEntity theo entityType
        Map<String, ColumnPropertyEntity> columnPropertyEntityMap = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.PRODUCTION_LINE
        );
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        BooleanBuilder booleanBuilderOr = new BooleanBuilder();
        if (!StringUtils.isEmpty(input.getCommon())) {
            booleanBuilderOr
                .or(qProductionLineEntity.productionLineCode.containsIgnoreCase(input.getCommon()))
                .or(qProductionLineEntity.productionLineName.containsIgnoreCase(input.getCommon()))
                .or(qProductionLineEntity.supplier.containsIgnoreCase(input.getCommon()))
                .or(qProductionLineEntity.productivity.like("%" + input.getCommon() + "%"))
                .or(qProductionLineEntity.maintenanceTime.like("%" + input.getCommon() + "%"))
                .or(qProductionLineEntity.minProductionQuantity.like("%" + input.getCommon() + "%"))
                .or(qProductionLineEntity.maxProductionQuantity.like("%" + input.getCommon() + "%"))
                .or(qProductionLineEntity.maxWaitingTime.like("%" + input.getCommon() + "%"))
                .or(qProductionLineEntity.cycleTime.like("%" + input.getCommon() + "%"))
                .or(qProductionLineTypeEntity.productionLineTypeName.containsIgnoreCase(input.getCommon()));

            //            QProductionLineTypeEntity qProductionLineTypeEntity = QProductionLineTypeEntity.productionLineTypeEntity;
            //            JPAQuery queryCheck = new JPAQueryFactory(entityManager)
            //                .selectFrom(qProductionLineTypeEntity)
            //                .where(
            //                    qProductionLineTypeEntity.isActive
            //                        .eq(true)
            //                        .and(qProductionLineTypeEntity.productionLineTypeName.containsIgnoreCase(input.getCommon()))
            //                );
            //            List<ProductionLineEntity> lineEntities = queryCheck.fetch();
            //            if (lineEntities != null && lineEntities.size() > 0) {
            //                booleanBuilderOr.or(qProductionLineEntity.productionLineType.productionLineTypeName.containsIgnoreCase(input.getCommon()));
            //            }

            //            Lấy ra danh sách ID productionLine có chứa từ khóa tìm kiếm chung
            Map<String, List<Integer>> productionLineIdCommon = new HashMap<>();
            columnPropertyEntityMap.forEach((s, columnPropertyEntity) -> {
                if (columnPropertyEntity.getIsFixed() == 0) {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(input.getCommon()));
                    propertySubQuery.where(dynamicBooleanBuilder);
                    productionLineIdCommon.put(s, propertySubQuery.fetch());
                }
            });
            for (String key : productionLineIdCommon.keySet()) {
                booleanBuilderOr.or(qProductionLineEntity.productionLineId.in(productionLineIdCommon.get(key)));
            }
        }
        if (!StringUtils.isEmpty(filter.getProductionLineCode())) {
            booleanBuilder.and(qProductionLineEntity.productionLineCode.containsIgnoreCase(filter.getProductionLineCode()));
        }
        if (!StringUtils.isEmpty(filter.getProductionLineName())) {
            booleanBuilder.and(qProductionLineEntity.productionLineName.containsIgnoreCase(filter.getProductionLineName()));
        }
        if (filter.getProductionLineType() != null && !StringUtils.isEmpty(filter.getProductionLineType().getProductionLineTypeName())) {
            booleanBuilder.and(
                qProductionLineEntity.productionLineType.productionLineTypeName.containsIgnoreCase(
                    filter.getProductionLineType().getProductionLineTypeName()
                )
            );
        }
        if (filter.getProductivity() != null) {
            booleanBuilder.and(qProductionLineEntity.productivity.like("%" + formatDoubleValue(filter.getProductivity()) + "%"));
        }
        if (!StringUtils.isEmpty(filter.getSupplier())) {
            booleanBuilder.and(qProductionLineEntity.supplier.containsIgnoreCase(filter.getSupplier()));
        }
        if (filter.getStatus() != null) {
            booleanBuilder.and(qProductionLineEntity.status.eq(filter.getStatus()));
        }
        if (filter.getMaintenanceTime() != null) {
            booleanBuilder.and(qProductionLineEntity.maintenanceTime.like("%" + formatDoubleValue(filter.getMaintenanceTime()) + "%"));
        }
        if (filter.getMinProductionQuantity() != null) {
            booleanBuilder.and(
                qProductionLineEntity.minProductionQuantity.like("%" + formatDoubleValue(filter.getMinProductionQuantity()) + "%")
            );
        }
        if (filter.getMaxProductionQuantity() != null) {
            booleanBuilder.and(
                qProductionLineEntity.maxProductionQuantity.like("%" + formatDoubleValue(filter.getMaxProductionQuantity()) + "%")
            );
        }
        if (filter.getPurchaseDate() != null) {
            booleanBuilder.and(qProductionLineEntity.purchaseDate.eq(filter.getPurchaseDate()));
        }

        if (filter.getPurchaseDateStart() != null && filter.getPurchaseDateEnd() != null) {
            booleanBuilder.and(qProductionLineEntity.purchaseDate.between(filter.getPurchaseDateStart(), filter.getPurchaseDateEnd()));
        }

        if (filter.getMaxWaitingTime() != null) {
            booleanBuilder.and(qProductionLineEntity.maxWaitingTime.like("%" + formatDoubleValue(filter.getMaxWaitingTime()) + "%"));
        }
        if (filter.getCycleTime() != null) {
            booleanBuilder.and(qProductionLineEntity.cycleTime.like("%" + formatDoubleValue(filter.getCycleTime()) + "%"));
        }

        if (!filter.getPropertiesMap().isEmpty()) {
            Map<String, List<Integer>> productionLineIdMap = new HashMap<>();
            filter
                .getPropertiesMap()
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
                        productionLineIdMap.put(keyName, propertySubQuery.fetch());
                    }
                });
            for (String key : productionLineIdMap.keySet()) {
                booleanBuilder.and(qProductionLineEntity.productionLineId.in(productionLineIdMap.get(key)));
            }
        }

        query.where(booleanBuilder);
        query.where(booleanBuilderOr);

        if (!StringUtils.isEmpty(input.getSortProperty())) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.PRODUCTION_LINE);
            boolean isSorted = false;
            for (ColumnPropertyEntity column : columns) {
                // Valid sort column name
                if (input.getSortProperty().equals(column.getKeyName())) {
                    isSorted = true;
                    if (column.getIsFixed() == 1) {
                        // Default col
                        Path<Object> fieldPath = Expressions.path(Object.class, qProductionLineEntity, input.getSortProperty());
                        query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                    } else {
                        // Dynamic col
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qProductionLineEntity.productionLineId)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.PRODUCTION_LINE))
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
                    }
                }
            }

            if (!isSorted) {
                Path<Object> fieldPath = Expressions.path(Object.class, qProductionLineEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }

        System.err.println(query);
        //        query.orderBy(qProductionLineEntity.createdAt.desc());
        List<ProductionLineEntity> result = query.fetch();

        return new PageImpl<>(result, pageable, query.fetchCount());
    }
}
