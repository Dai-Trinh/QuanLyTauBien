package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.SeaportCustomRepository;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.dto.SeaportDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Repository
public class SeaportCustomRepositoryImpl implements SeaportCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Override
    public Page<SeaportEntity> getAll(PageFilterInput<SeaportDTO> input, Pageable pageable) {
        SeaportDTO filter = input.getFilter();
        QSeaportEntity qSeaportEntity = QSeaportEntity.seaportEntity;
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        JPAQuery<SeaportEntity> query = new JPAQueryFactory(entityManager)
            .selectFrom(qSeaportEntity)
            .leftJoin(qKeyValueEntityV2)
            .on(qKeyValueEntityV2.entityType.eq(Contants.EntityType.SEAPORT))
            .on(qSeaportEntity.id.eq(qKeyValueEntityV2.entityKey));

        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qSeaportEntity.isActive.isTrue());

        //Check search common or not?
        if (!StringUtils.isEmpty(input.getCommon())) {
            booleanBuilder.and(
                qSeaportEntity.seaportCode
                    .containsIgnoreCase(input.getCommon())
                    .or(qSeaportEntity.seaportName.containsIgnoreCase(input.getCommon()))
                    .or(qSeaportEntity.seaportAddress.containsIgnoreCase(input.getCommon()))
                    .or(qSeaportEntity.seaportNation.like("%" + input.getCommon() + "%"))
                    .or(qSeaportEntity.latitude.like("%" + input.getCommon() + "%"))
                    .or(qSeaportEntity.longitude.like("%" + input.getCommon() + "%"))
                    .or(qSeaportEntity.note.like("%" + input.getCommon() + "%"))
                    .or(qKeyValueEntityV2.commonValue.containsIgnoreCase(input.getCommon()))
            );
        }
        if (!StringUtils.isEmpty(filter.getSeaportCode())) {
            booleanBuilder.and(qSeaportEntity.seaportCode.containsIgnoreCase(filter.getSeaportCode()));
        }
        if (!StringUtils.isEmpty(filter.getSeaportName())) {
            booleanBuilder.and(qSeaportEntity.seaportName.containsIgnoreCase(filter.getSeaportName()));
        }
        if (filter.getSeaportNation() != null) {
            booleanBuilder.and(qSeaportEntity.seaportNation.containsIgnoreCase(filter.getSeaportNation()));
        }
        if (!StringUtils.isEmpty(filter.getSeaportAddress())) {
            booleanBuilder.and(qSeaportEntity.seaportAddress.containsIgnoreCase(filter.getSeaportAddress()));
        }
        if (filter.getStatus() != null) {
            booleanBuilder.and(qSeaportEntity.status.eq(filter.getStatus()));
        }
        if (filter.getLatitude() != null) {
            booleanBuilder.and(qSeaportEntity.latitude.containsIgnoreCase(filter.getLatitude()));
        }
        if (filter.getLongitude() != null) {
            booleanBuilder.and(qSeaportEntity.longitude.eq(filter.getLongitude()));
        }
        if (filter.getNote() != null) {
            booleanBuilder.and(qSeaportEntity.note.eq(filter.getNote()));
        }
        if (filter.getUpdatedAt() != null) {
            booleanBuilder.and(qSeaportEntity.updatedAt.eq(filter.getUpdatedAt()));
        }

        if (filter.getPropertiesMap() != null || filter.getPropertiesMap().isEmpty()) {
            Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
                Contants.EntityType.SEAPORT
            );
            for (String key : filter.getPropertiesMap().keySet()) {
                if (!StringUtils.isEmpty(filter.getPropertiesMap().get(key))) {
                    if (columnPropertyEntities.get(key).getDataType() == Contants.DATE_VALUE) {
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                        String[] value = filter.getPropertiesMap().get(key).split(" ");

                        LocalDate startDate = Instant.from(formatter.parse(value[0].trim())).atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate endDate = Instant.from(formatter.parse(value[1].trim())).atZone(ZoneId.systemDefault()).toLocalDate();
                        booleanBuilder
                            .and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(key))
                            .and(qKeyValueEntityV2.dateValue.between(startDate, endDate));
                    } else {
                        booleanBuilder
                            .and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(key))
                            .and(qKeyValueEntityV2.commonValue.containsIgnoreCase(filter.getPropertiesMap().get(key)));
                    }
                }
            }
        }

        if (!StringUtils.isEmpty(input.getSortProperty())) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.SEAPORT);
            boolean isSorted = false;
            for (ColumnPropertyEntity column : columns) {
                // Valid sort column name
                if (input.getSortProperty().equals(column.getKeyName())) {
                    isSorted = true;
                    if (column.getIsFixed() == 1) {
                        // Default col
                        Path<Object> fieldPath = Expressions.path(Object.class, qSeaportEntity, input.getSortProperty());
                        query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                    } else {
                        // Dynamic col
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qSeaportEntity.id)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.SEAPORT))
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
                Path<Object> fieldPath = Expressions.path(Object.class, qSeaportEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }
        query.where(booleanBuilder).groupBy(qSeaportEntity.id);
        List<SeaportEntity> result = query.fetch();

        return new PageImpl<>(result, pageable, query.fetchCount());
    }
}
