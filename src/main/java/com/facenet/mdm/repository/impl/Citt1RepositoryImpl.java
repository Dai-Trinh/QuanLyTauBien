package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.Citt1Entity;
import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.QCitt1Entity;
import com.facenet.mdm.domain.QKeyValueEntityV2;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.Citt1CustomRepository;
import com.facenet.mdm.service.dto.Citt1DTO;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.querydsl.core.BooleanBuilder;
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

public class Citt1RepositoryImpl implements Citt1CustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ColumnPropertyRepository columnPropertyRepository;

    @Override
    public Page<Citt1Entity> getAll(PageFilterInput<CoittDTO> input, Pageable pageable) {
        QCitt1Entity qCitt1Entity = QCitt1Entity.citt1Entity;
        CoittDTO citt1 = input.getFilter();
        String common = input.getCommon();
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        JPAQuery<Citt1Entity> query = new JPAQueryFactory(em).selectFrom(qCitt1Entity);
        if (pageable.isPaged()) {
            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qCitt1Entity.isActive.isTrue());
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qCitt1Entity.productCode
                    .containsIgnoreCase(common)
                    .or(qCitt1Entity.materialCode.containsIgnoreCase(common))
                    .or(qCitt1Entity.proName.containsIgnoreCase(common))
                    .or(qCitt1Entity.unit.containsIgnoreCase(common))
                    .or(qCitt1Entity.version.containsIgnoreCase(common))
                    .or(qCitt1Entity.note.containsIgnoreCase(common))
                    .or(qCitt1Entity.notice.containsIgnoreCase(common))
                    .or(qCitt1Entity.techName.containsIgnoreCase(common))
            );
        }
        if (!StringUtils.isEmpty(citt1.getProductCode())) {
            booleanBuilder.and(qCitt1Entity.materialCode.containsIgnoreCase(citt1.getProductCode()));
        }
        if (!StringUtils.isEmpty(citt1.getMerchandiseGroup())) {
            booleanBuilder.and(qCitt1Entity.merchandiseGroupEntity.merchandiseGroupCode.eq(citt1.getMerchandiseGroup()));
        }
        //        if (!StringUtils.isEmpty(citt1.getProductCode())) {
        //            booleanBuilder.and(qCitt1Entity.productCode.containsIgnoreCase(citt1.getProductCode()));
        //        }
        if (!StringUtils.isEmpty(citt1.getProName())) {
            booleanBuilder.and(qCitt1Entity.proName.containsIgnoreCase(citt1.getProName()));
        }
        if (!StringUtils.isEmpty(citt1.getTechName())) {
            booleanBuilder.and(qCitt1Entity.techName.containsIgnoreCase(citt1.getTechName()));
        }
        if (!StringUtils.isEmpty(citt1.getUnit())) {
            booleanBuilder.and(qCitt1Entity.unit.containsIgnoreCase(citt1.getUnit()));
        }
        if (!StringUtils.isEmpty(citt1.getVersion())) {
            booleanBuilder.and(qCitt1Entity.version.containsIgnoreCase(citt1.getVersion()));
        }
        if (!StringUtils.isEmpty(citt1.getNote())) {
            booleanBuilder.and(qCitt1Entity.note.containsIgnoreCase(citt1.getNote()));
        }
        if (!StringUtils.isEmpty(citt1.getNotice())) {
            booleanBuilder.and(qCitt1Entity.notice.containsIgnoreCase(citt1.getNotice()));
        }
        booleanBuilder.and(qCitt1Entity.itemGroupCode.eq(Contants.ItemGroup.NVL));
        if (citt1.getTemplate() != null) {
            booleanBuilder.and(qCitt1Entity.isTemplate.eq(citt1.getTemplate()));
        }
        if (citt1.getStatus() != null) {
            booleanBuilder.and(qCitt1Entity.status.eq(citt1.getStatus()));
        }
        if (!StringUtils.isEmpty(input.getSortProperty())) {
            Path<Object> fieldPath = Expressions.path(Object.class, qCitt1Entity, input.getSortProperty());
            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
        }
        Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.NVL
        );
        if (!citt1.getPropertiesMap().isEmpty()) {
            for (String keyName : citt1.getPropertiesMap().keySet()) {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());

                String value = citt1.getPropertiesMap().get(keyName);
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
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.dateValue.stringValue().containsIgnoreCase(value));
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
                    booleanBuilder.and(qCitt1Entity.id.in(propertySubQuery));
                }
            }
        }

        if (!StringUtils.isEmpty(common)) {
            columnPropertyEntities.forEach((s, columnPropertyEntity) -> {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(common));
                propertySubQuery.where(dynamicBooleanBuilder);
                booleanBuilder.or(qCitt1Entity.id.in(propertySubQuery));
            });
        }

        if (!StringUtils.isEmpty(input.getSortProperty())) {
            Integer entityType = Contants.EntityType.NVL;

            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(entityType);
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
                                        .eq(qCitt1Entity.id)
                                        .and(qKeyValueEntityV22.entityType.eq(entityType))
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
                            Path<Object> fieldPath = Expressions.path(Object.class, qCitt1Entity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                Path<Object> fieldPath = Expressions.path(Object.class, qCitt1Entity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }

        query.where(booleanBuilder);
        query.groupBy(qCitt1Entity.materialCode);

        System.err.println(query);

        List<Citt1Entity> result = query.fetch();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }
}
