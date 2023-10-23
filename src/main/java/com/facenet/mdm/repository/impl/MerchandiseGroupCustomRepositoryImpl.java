package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.MerchandiseGroupEntity;
import com.facenet.mdm.domain.QKeyValueEntityV2;
import com.facenet.mdm.domain.QMerchandiseGroupEntity;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.MerchandiseGroupRepository;
import com.facenet.mdm.repository.custom.MerchandiseGroupCustomRepository;
import com.facenet.mdm.service.dto.MerchandiseGroupDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class MerchandiseGroupCustomRepositoryImpl implements MerchandiseGroupCustomRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    MerchandiseGroupRepository merchandiseGroupRepository;

    @Override
    public Page<MerchandiseGroupEntity> getAllMerchandiseGroup(PageFilterInput<MerchandiseGroupDTO> input, Pageable pageable) {
        MerchandiseGroupDTO merchandiseGroupDTO = input.getFilter();

        QMerchandiseGroupEntity qMerchandiseGroupEntity = QMerchandiseGroupEntity.merchandiseGroupEntity;

        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qMerchandiseGroupEntity);

        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qMerchandiseGroupEntity.isActive.eq(true));

        //Điều kiện tìm kiếm MerchandiseGroup theo cột
        if (!StringUtils.isEmpty(merchandiseGroupDTO.getMerchandiseGroupCode())) {
            booleanBuilder.and(
                qMerchandiseGroupEntity.merchandiseGroupCode.containsIgnoreCase(merchandiseGroupDTO.getMerchandiseGroupCode())
            );
        }

        if (!StringUtils.isEmpty(merchandiseGroupDTO.getMerchandiseGroupName())) {
            booleanBuilder.and(
                qMerchandiseGroupEntity.merchandiseGroupName.containsIgnoreCase(merchandiseGroupDTO.getMerchandiseGroupName())
            );
        }

        if (!StringUtils.isEmpty(merchandiseGroupDTO.getMerchandiseGroupDescription())) {
            booleanBuilder.and(
                qMerchandiseGroupEntity.merchandiseGroupDescription.containsIgnoreCase(merchandiseGroupDTO.getMerchandiseGroupDescription())
            );
        }

        if (!StringUtils.isEmpty(merchandiseGroupDTO.getMerchandiseGroupNote())) {
            booleanBuilder.and(
                qMerchandiseGroupEntity.merchandiseGroupNote.containsIgnoreCase(merchandiseGroupDTO.getMerchandiseGroupNote())
            );
        }

        if (merchandiseGroupDTO.getMerchandiseGroupStatus() != null) {
            booleanBuilder.and(qMerchandiseGroupEntity.merchandiseGroupStatus.eq(merchandiseGroupDTO.getMerchandiseGroupStatus()));
        }

        //Tìm kiếm trường động theo cột
        Map<String, ColumnPropertyEntity> columnPropertyEntityMap = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.MERCHANDISE_GROUP
        );
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        if (merchandiseGroupDTO != null && !merchandiseGroupDTO.getPropertiesMap().isEmpty()) {
            Map<String, List<Integer>> merchandiseGroupIdMap = new HashMap<>();
            merchandiseGroupDTO
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
                        merchandiseGroupIdMap.put(keyName, propertySubQuery.fetch());
                    }
                });
            for (String key : merchandiseGroupIdMap.keySet()) {
                booleanBuilder.and(qMerchandiseGroupEntity.merchandiseGroupId.in(merchandiseGroupIdMap.get(key)));
            }
        }

        query.where(booleanBuilder);

        if (!StringUtils.isEmpty(input.getCommon())) {
            BooleanBuilder booleanBuilderCommon = new BooleanBuilder();
            booleanBuilderCommon.or(qMerchandiseGroupEntity.merchandiseGroupCode.containsIgnoreCase(input.getCommon()));
            booleanBuilderCommon.or(qMerchandiseGroupEntity.merchandiseGroupName.containsIgnoreCase(input.getCommon()));
            booleanBuilderCommon.or(qMerchandiseGroupEntity.merchandiseGroupDescription.containsIgnoreCase(input.getCommon()));
            booleanBuilderCommon.or(qMerchandiseGroupEntity.merchandiseGroupNote.containsIgnoreCase(input.getCommon()));
            Map<String, List<Integer>> merchandiseIdCommon = new HashMap<>();
            columnPropertyEntityMap.forEach((s, columnPropertyEntity) -> {
                if (columnPropertyEntity.getIsFixed() == 0) {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(input.getCommon()));
                    propertySubQuery.where(dynamicBooleanBuilder);
                    merchandiseIdCommon.put(s, propertySubQuery.fetch());
                }
            });
            for (String key : merchandiseIdCommon.keySet()) {
                booleanBuilderCommon.or(qMerchandiseGroupEntity.merchandiseGroupId.in(merchandiseIdCommon.get(key)));
            }
            query.where(booleanBuilderCommon);
        }

        if (!org.apache.commons.lang3.StringUtils.isEmpty(input.getSortProperty())) {
            boolean checkSort = true;
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.MERCHANDISE_GROUP);

            for (ColumnPropertyEntity column : columns) {
                if (input.getSortProperty().equals(column.getKeyName())) {
                    checkSort = false;
                    if (column.getIsFixed() == 1) {
                        Path<Object> filePath = Expressions.path(Object.class, qMerchandiseGroupEntity, input.getSortProperty());
                        query.orderBy(new OrderSpecifier(input.getSortOrder(), filePath));
                    } else {
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qMerchandiseGroupEntity.merchandiseGroupId)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.MERCHANDISE_GROUP))
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

            if (checkSort) {
                Path<Object> fieldPath = Expressions.path(Object.class, qMerchandiseGroupEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }
        List<MerchandiseGroupEntity> merchandiseGroupEntityList = query.fetch();

        return new PageImpl<>(merchandiseGroupEntityList, pageable, query.fetchCount());
    }
}
