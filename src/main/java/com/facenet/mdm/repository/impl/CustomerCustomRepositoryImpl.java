package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.CustomerCustomRepository;
import com.facenet.mdm.service.dto.CustomerDTO;
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
import org.springframework.stereotype.Repository;

@Repository
public class CustomerCustomRepositoryImpl implements CustomerCustomRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Override
    public Page<CustomerEntity> searchCustomers(PageFilterInput<CustomerDTO> input, Pageable pageable) {
        CustomerDTO filter = input.getFilter();
        String common = input.getCommon();
        QCustomerEntity qCustomerEntity = QCustomerEntity.customerEntity;
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        JPAQuery<Integer> propertySubQueryCommon = new JPAQuery<>(entityManager)
            .select(qKeyValueEntityV2.entityKey)
            .from(qKeyValueEntityV2);
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

        JPAQuery<CustomerEntity> query = new JPAQueryFactory(entityManager).select(qCustomerEntity).from(qCustomerEntity);
        if (pageable.isPaged()) {
            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        booleanBuilder.and(qCustomerEntity.isActive.eq(true));
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qCustomerEntity.customerCode
                    .containsIgnoreCase(common)
                    .or(qCustomerEntity.customerName.containsIgnoreCase(common))
                    .or(qCustomerEntity.customerPhone.containsIgnoreCase(common))
                    .or(qCustomerEntity.customerEmail.containsIgnoreCase(common))
                    .or(qCustomerEntity.address.containsIgnoreCase(common))
                    .or(qCustomerEntity.id.in(propertySubQueryCommon))
                    .or(qCustomerEntity.customerType.containsIgnoreCase(common))
            );
        }
        if (!StringUtils.isEmpty(filter.getCustomerCode())) {
            booleanBuilder.and(qCustomerEntity.customerCode.containsIgnoreCase(filter.getCustomerCode()));
        }
        if (!StringUtils.isEmpty(filter.getCustomerName())) {
            booleanBuilder.and(qCustomerEntity.customerName.containsIgnoreCase(filter.getCustomerName()));
        }
        if (!StringUtils.isEmpty(filter.getCustomerEmail())) {
            booleanBuilder.and(qCustomerEntity.customerEmail.containsIgnoreCase(filter.getCustomerEmail()));
        }
        if (!StringUtils.isEmpty(filter.getCustomerPhone())) {
            booleanBuilder.and(qCustomerEntity.customerPhone.containsIgnoreCase(filter.getCustomerPhone()));
        }
        if (!StringUtils.isEmpty(filter.getAddress())) {
            booleanBuilder.and(qCustomerEntity.address.containsIgnoreCase(filter.getAddress()));
        }
        if (!StringUtils.isEmpty(filter.getCustomerType())) {
            booleanBuilder.and(qCustomerEntity.customerType.containsIgnoreCase(filter.getCustomerType()));
        }
        if (filter.getStatus() != null) {
            booleanBuilder.and(qCustomerEntity.status.eq(filter.getStatus()));
        }
        if (!filter.getPropertiesMap().isEmpty()) {
            Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
                Contants.EntityType.CUSTOMER
            );
            for (String keyName : filter.getPropertiesMap().keySet()) {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(entityManager)
                    .select(qKeyValueEntityV2.entityKey)
                    .from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                String value = filter.getPropertiesMap().get(keyName);
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
                    booleanBuilder.and(qCustomerEntity.id.in(propertySubQuery));
                }
            }
        }
        if (!StringUtils.isEmpty(input.getSortProperty())) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.CUSTOMER);
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
                                        .eq(qCustomerEntity.id)
                                        .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.CUSTOMER))
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
                            Path<Object> fieldPath = Expressions.path(Object.class, qCustomerEntity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                Path<Object> fieldPath = Expressions.path(Object.class, qCustomerEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }
        query.where(booleanBuilder);
        List<CustomerEntity> result = query.fetch();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }
}
