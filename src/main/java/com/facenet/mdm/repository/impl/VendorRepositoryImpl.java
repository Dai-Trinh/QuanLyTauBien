package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.VendorCustomRepository;
import com.facenet.mdm.service.dto.VendorDTO;
import com.facenet.mdm.service.dto.response.PageResponse;
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
import org.springframework.data.domain.Pageable;

public class VendorRepositoryImpl implements VendorCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ColumnPropertyRepository columnPropertyRepository;

    @Override
    public PageResponse<List<VendorEntity>> getAllVendor(PageFilterInput<VendorDTO> input, Pageable pageable) {
        VendorDTO vendorDTO = input.getFilter();
        String common = input.getCommon();
        QVendorEntity qVendorEntity = QVendorEntity.vendorEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

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
        JPAQuery<VendorEntity> query = new JPAQueryFactory(em).select(qVendorEntity).from(qVendorEntity);
        if (pageable.isPaged()) {
            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        booleanBuilder.and(qVendorEntity.isActive.eq(true));
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qVendorEntity.vendorCode
                    .containsIgnoreCase(common)
                    .or(qVendorEntity.vendorName.containsIgnoreCase(common))
                    .or(qVendorEntity.otherName.containsIgnoreCase(common))
                    .or(qVendorEntity.email.containsIgnoreCase(common))
                    .or(qVendorEntity.address.containsIgnoreCase(common))
                    .or(qVendorEntity.phone.containsIgnoreCase(common))
                    .or(qVendorEntity.faxCode.containsIgnoreCase(common))
                    .or(qVendorEntity.taxCode.containsIgnoreCase(common))
                    .or(qVendorEntity.currency.containsIgnoreCase(common))
                    .or(qVendorEntity.contactId.containsIgnoreCase(common))
                    .or(qVendorEntity.contactName.containsIgnoreCase(common))
                    .or(qVendorEntity.contactEmail.containsIgnoreCase(common))
                    .or(qVendorEntity.contactGender.containsIgnoreCase(common))
                    .or(qVendorEntity.contactPhone.containsIgnoreCase(common))
                    .or(qVendorEntity.contactTitle.containsIgnoreCase(common))
                    .or(qVendorEntity.contactPosition.containsIgnoreCase(common))
                    .or(qVendorEntity.contactGender.containsIgnoreCase(common))
                    .or(qVendorEntity.contactBirthDate.stringValue().containsIgnoreCase(common))
                    .or(qVendorEntity.contactAddress.containsIgnoreCase(common))
                    .or(qVendorEntity.id.in(propertySubQueryCommon))
            );
        }
        if (!StringUtils.isEmpty(vendorDTO.getVendorCode())) {
            booleanBuilder.and(qVendorEntity.vendorCode.containsIgnoreCase(vendorDTO.getVendorCode()));
        }
        if (!StringUtils.isEmpty(vendorDTO.getVendorName())) {
            booleanBuilder.and(qVendorEntity.vendorName.containsIgnoreCase(vendorDTO.getVendorName()));
        }
        if (!StringUtils.isEmpty(vendorDTO.getOtherName())) {
            booleanBuilder.and(qVendorEntity.otherName.containsIgnoreCase(vendorDTO.getOtherName()));
        }
        if (!StringUtils.isEmpty(vendorDTO.getEmail())) {
            booleanBuilder.and(qVendorEntity.email.containsIgnoreCase(vendorDTO.getEmail()));
        }
        if (!StringUtils.isEmpty(vendorDTO.getAddress())) {
            booleanBuilder.and(qVendorEntity.address.containsIgnoreCase(vendorDTO.getAddress()));
        }
        if (vendorDTO.getStatus() != null) {
            booleanBuilder.and(qVendorEntity.status.eq(vendorDTO.getStatus()));
        }
        if (!StringUtils.isEmpty(vendorDTO.getCurrency())) booleanBuilder.and(
            qVendorEntity.currency.containsIgnoreCase(vendorDTO.getCurrency())
        );
        if (!StringUtils.isEmpty(vendorDTO.getFaxCode())) booleanBuilder.and(
            qVendorEntity.faxCode.containsIgnoreCase(vendorDTO.getFaxCode())
        );
        if (!StringUtils.isEmpty(vendorDTO.getTaxCode())) booleanBuilder.and(
            qVendorEntity.taxCode.containsIgnoreCase(vendorDTO.getTaxCode())
        );
        if (!StringUtils.isEmpty(vendorDTO.getPhone())) booleanBuilder.and(qVendorEntity.phone.containsIgnoreCase(vendorDTO.getPhone()));
        if (!StringUtils.isEmpty(vendorDTO.getContactId())) booleanBuilder.and(
            qVendorEntity.contactId.containsIgnoreCase(vendorDTO.getContactId())
        );
        if (!StringUtils.isEmpty(vendorDTO.getContactName())) booleanBuilder.and(
            qVendorEntity.contactName.containsIgnoreCase(vendorDTO.getContactName())
        );
        if (!StringUtils.isEmpty(vendorDTO.getContactEmail())) booleanBuilder.and(
            qVendorEntity.contactEmail.containsIgnoreCase(vendorDTO.getContactEmail())
        );
        if (!StringUtils.isEmpty(vendorDTO.getContactPhone())) booleanBuilder.and(
            qVendorEntity.contactPhone.containsIgnoreCase(vendorDTO.getContactPhone())
        );
        if (!StringUtils.isEmpty(vendorDTO.getContactAddress())) booleanBuilder.and(
            qVendorEntity.contactAddress.containsIgnoreCase(vendorDTO.getContactAddress())
        );
        if (!StringUtils.isEmpty(vendorDTO.getContactGender())) booleanBuilder.and(
            qVendorEntity.contactGender.containsIgnoreCase(vendorDTO.getContactGender())
        );
        if (!StringUtils.isEmpty(vendorDTO.getContactPosition())) booleanBuilder.and(
            qVendorEntity.contactPosition.containsIgnoreCase(vendorDTO.getContactPosition())
        );
        if (!StringUtils.isEmpty(vendorDTO.getContactTitle())) booleanBuilder.and(
            qVendorEntity.contactTitle.containsIgnoreCase(vendorDTO.getContactTitle())
        );
        if (vendorDTO.getContactBirthDate() != null) booleanBuilder.and(qVendorEntity.contactBirthDate.eq(vendorDTO.getContactBirthDate()));
        if (!vendorDTO.getVendorMap().isEmpty()) {
            Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
                Contants.EntityType.VENDOR
            );
            for (String keyName : vendorDTO.getVendorMap().keySet()) {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                String value = vendorDTO.getVendorMap().get(keyName);
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
                    booleanBuilder.and(qVendorEntity.id.in(propertySubQuery));
                }
            }
        }
        if (!StringUtils.isEmpty(input.getSortProperty())) {
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.VENDOR);
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
                                        .eq(qVendorEntity.id)
                                        .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.VENDOR))
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
                            Path<Object> fieldPath = Expressions.path(Object.class, qVendorEntity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                Path<Object> fieldPath = Expressions.path(Object.class, qVendorEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }
        query.where(booleanBuilder);
        List<VendorEntity> result = query.fetch();
        return new PageResponse<List<VendorEntity>>()
            .errorCode("00")
            .message("Thành công")
            .isOk(true)
            .dataCount(query.fetchCount())
            .data(result);
    }
}
