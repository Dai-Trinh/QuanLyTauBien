package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.VendorItemRepository;
import com.facenet.mdm.repository.custom.CoittCustomRepository;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.comparator.ComparableComparator;

public class CoittRepositoryImpl implements CoittCustomRepository {

    @Autowired
    VendorItemRepository vendorItemRepository;

    @Autowired
    private ColumnPropertyRepository columnPropertyRepository;

    @PersistenceContext
    private EntityManager em;

    private List<DataItemInVendor> paginate(List<DataItemInVendor> items, int page, int pageSize) {
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, items.size());
        return items.subList(fromIndex, toIndex);
    }

    @Override
    public Page<CoittEntity> getAll(PageFilterInput<CoittDTO> input, Pageable pageable) {
        QCoittEntity qCoittEntity = QCoittEntity.coittEntity;
        CoittDTO coittDTO = input.getFilter();
        String common = input.getCommon();
        JPAQuery<CoittEntity> query = new JPAQueryFactory(em).selectFrom(qCoittEntity);

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
        if (pageable.isPaged()) {
            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qCoittEntity.isActive.isTrue());
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qCoittEntity.productCode
                    .containsIgnoreCase(common)
                    .or(qCoittEntity.proName.containsIgnoreCase(common))
                    .or(qCoittEntity.unit.containsIgnoreCase(common))
                    .or(qCoittEntity.version.containsIgnoreCase(common))
                    .or(qCoittEntity.note.containsIgnoreCase(common))
                    .or(qCoittEntity.notice.containsIgnoreCase(common))
                    //                .or(qCoittEntity.itemGroupCode.stringValue().containsIgnoreCase(common))
                    //                .or(qCoittEntity.isTemplate.stringValue().containsIgnoreCase(common))
                    //                .or(qCoittEntity.parent.stringValue().containsIgnoreCase(common))
                    //.or(qCoittEntity.id.in(propertySubQueryCommon))
                    .or(qCoittEntity.techName.containsIgnoreCase(common))
            );
        }
        if (!StringUtils.isEmpty(coittDTO.getProductCode())) {
            booleanBuilder.and(qCoittEntity.productCode.containsIgnoreCase(coittDTO.getProductCode()));
        }
        if (!StringUtils.isEmpty(coittDTO.getProName())) {
            booleanBuilder.and(qCoittEntity.proName.containsIgnoreCase(coittDTO.getProName()));
        }
        if (!StringUtils.isEmpty(coittDTO.getUnit())) {
            booleanBuilder.and(qCoittEntity.unit.containsIgnoreCase(coittDTO.getUnit()));
        }
        if (!StringUtils.isEmpty(coittDTO.getVersion())) {
            booleanBuilder.and(qCoittEntity.version.containsIgnoreCase(coittDTO.getVersion()));
        }
        if (!StringUtils.isEmpty(coittDTO.getNote())) {
            booleanBuilder.and(qCoittEntity.note.containsIgnoreCase(coittDTO.getNote()));
        }
        if (!StringUtils.isEmpty(coittDTO.getNotice())) {
            booleanBuilder.and(qCoittEntity.notice.containsIgnoreCase(coittDTO.getNotice()));
        }
        if (coittDTO.getItemGroupCode() != null) {
            booleanBuilder.and(qCoittEntity.itemGroupCode.eq(coittDTO.getItemGroupCode()));
        }
        if (coittDTO.getTemplate() != null) {
            booleanBuilder.and(qCoittEntity.isTemplate.eq(coittDTO.getTemplate()));
        }
        if (coittDTO.getParent() != null) {
            booleanBuilder.and(qCoittEntity.parent.eq(coittDTO.getParent()));
        }
        if (coittDTO.getStatus() != null) {
            booleanBuilder.and(qCoittEntity.status.eq(coittDTO.getStatus()));
        }

        if (!StringUtils.isEmpty(coittDTO.getMerchandiseGroup())) {
            booleanBuilder.and(qCoittEntity.merchandiseGroupEntity.merchandiseGroupCode.eq(coittDTO.getMerchandiseGroup()));
        }

        if (!StringUtils.isEmpty(coittDTO.getTechName())) {
            booleanBuilder.and(qCoittEntity.techName.containsIgnoreCase(coittDTO.getTechName()));
        }

        Map<String, ColumnPropertyEntity> columnPropertyEntities = new HashMap<>();
        Map<String, ColumnPropertyEntity> btpPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.BTP
        );
        Map<String, ColumnPropertyEntity> tpPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.TP
        );
        for (String s : btpPropertyEntities.keySet()) {
            columnPropertyEntities.put(s, btpPropertyEntities.get(s));
        }
        for (String s : tpPropertyEntities.keySet()) {
            columnPropertyEntities.put(s, tpPropertyEntities.get(s));
        }

        if (!coittDTO.getPropertiesMap().isEmpty()) {
            for (String keyName : coittDTO.getPropertiesMap().keySet()) {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                String value = coittDTO.getPropertiesMap().get(keyName);
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
                    booleanBuilder.and(qCoittEntity.id.in(propertySubQuery));
                }
            }
        }

        if (!StringUtils.isEmpty(common)) {
            columnPropertyEntities.forEach((s, columnPropertyEntity) -> {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                if (coittDTO.getItemGroupCode() == Contants.ItemGroup.BTP) {
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.entityType.eq(Contants.EntityType.BTP));
                } else if (coittDTO.getItemGroupCode() == Contants.ItemGroup.TP) {
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.entityType.eq(Contants.EntityType.TP));
                }
                dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(common));
                propertySubQuery.where(dynamicBooleanBuilder);
                booleanBuilder.or(qCoittEntity.id.in(propertySubQuery));
            });
        }

        if (!StringUtils.isEmpty(input.getSortProperty())) {
            Integer entityType = Contants.EntityType.TP;
            if (coittDTO.getItemGroupCode() == Contants.ItemGroup.NVL) {
                entityType = Contants.EntityType.NVL;
            } else if (coittDTO.getItemGroupCode() == Contants.ItemGroup.BTP) {
                entityType = Contants.EntityType.BTP;
            }
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
                                        .eq(qCoittEntity.id)
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
                            Path<Object> fieldPath = Expressions.path(Object.class, qCoittEntity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                Path<Object> fieldPath = Expressions.path(Object.class, qCoittEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }
        query.where(booleanBuilder);
        List<CoittEntity> result = query.fetch();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }

    @Override
    public PageResponse<List<DataItemInVendor>> getAllItemAlongVendor(
        PageFilterInput<DataItemInVendor> input,
        String vendorCode,
        Pageable pageable
    ) {
        List<String> itemCodeList = vendorItemRepository.getAllItemByVendorCode(vendorCode);
        DataItemInVendor itemInVendor = input.getFilter();
        QCoittEntity qCoittEntity = QCoittEntity.coittEntity;
        QCitt1Entity qCitt1Entity = QCitt1Entity.citt1Entity;
        QMqqPriceEntity qMqqPriceEntity = QMqqPriceEntity.mqqPriceEntity;
        QLeadTimeEntity qLeadTimeEntity = QLeadTimeEntity.leadTimeEntity;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanBuilder booleanBuilderNVL = new BooleanBuilder();
        booleanBuilder.and(qCoittEntity.isActive.eq(true));
        booleanBuilderNVL.and(qCitt1Entity.isActive.eq(true));
        JPAQuery<DataItemInVendor> query = new JPAQueryFactory(em)
            .select(
                Projections.constructor(
                    DataItemInVendor.class,
                    qCoittEntity.productCode,
                    qCoittEntity.proName,
                    qCoittEntity.techName,
                    qCoittEntity.itemGroupCode,
                    qCoittEntity.unit,
                    qCoittEntity.version,
                    qCoittEntity.note,
                    qCoittEntity.notice,
                    qCoittEntity.quantity,
                    qCoittEntity.parent,
                    qCoittEntity.status,
                    qCoittEntity.kind,
                    qCoittEntity.createdAt
                )
            )
            .from(qCoittEntity)
            .leftJoin(qMqqPriceEntity)
            .on(qCoittEntity.productCode.eq(qMqqPriceEntity.itemCode))
            .leftJoin(qLeadTimeEntity)
            .on(qLeadTimeEntity.itemCode.eq(qCoittEntity.productCode));

        JPAQuery<DataItemInVendor> queryNVL = new JPAQueryFactory(em)
            .select(
                Projections.constructor(
                    DataItemInVendor.class,
                    qCitt1Entity.productCode,
                    qCitt1Entity.proName,
                    qCitt1Entity.techName,
                    qCitt1Entity.itemGroupCode,
                    qCitt1Entity.unit,
                    qCitt1Entity.version,
                    qCitt1Entity.note,
                    qCitt1Entity.notice,
                    qCitt1Entity.quantity,
                    qCitt1Entity.status,
                    qCitt1Entity.kind,
                    qCitt1Entity.createdAt
                )
            )
            .from(qCitt1Entity)
            .leftJoin(qMqqPriceEntity)
            .on(qCitt1Entity.materialCode.eq(qMqqPriceEntity.itemCode))
            .leftJoin(qLeadTimeEntity)
            .on(qLeadTimeEntity.itemCode.eq(qCitt1Entity.materialCode));

        //        if (pageable.isPaged()) {
        //            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        //            queryNVL = queryNVL.limit(pageable.getPageSize()).offset(pageable.getOffset());
        //        }
        booleanBuilder.and(qCoittEntity.productCode.in(itemCodeList));
        booleanBuilderNVL.and(qCitt1Entity.productCode.in(itemCodeList));
        if (!StringUtils.isEmpty(itemInVendor.getProductCode())) {
            booleanBuilder.and(qCoittEntity.productCode.containsIgnoreCase(itemInVendor.getProductCode()));
            booleanBuilderNVL.and(qCitt1Entity.materialCode.containsIgnoreCase(itemInVendor.getProductCode()));
        }
        if (!StringUtils.isEmpty(itemInVendor.getProName())) {
            booleanBuilder.and(qCoittEntity.proName.containsIgnoreCase(itemInVendor.getProName()));
            booleanBuilderNVL.and(qCitt1Entity.proName.containsIgnoreCase(itemInVendor.getProName()));
        }
        if (!StringUtils.isEmpty(itemInVendor.getTechName())) {
            booleanBuilder.and(qCoittEntity.techName.containsIgnoreCase(itemInVendor.getTechName()));
            booleanBuilderNVL.and(qCitt1Entity.techName.containsIgnoreCase(itemInVendor.getTechName()));
        }
        if (itemInVendor.getItemGroupCode() != null) {
            booleanBuilder.and(qCoittEntity.itemGroupCode.eq(itemInVendor.getItemGroupCode()));
            booleanBuilderNVL.and(qCitt1Entity.itemGroupCode.eq(itemInVendor.getItemGroupCode()));
        }
        if (itemInVendor.getStatus() != null) {
            booleanBuilder.and(qCoittEntity.status.eq(itemInVendor.getStatus()));
            booleanBuilderNVL.and(qCitt1Entity.status.eq(itemInVendor.getStatus()));
        }
        if (!StringUtils.isEmpty(itemInVendor.getUnit())) {
            booleanBuilder.and(qCoittEntity.unit.containsIgnoreCase(itemInVendor.getUnit()));
            booleanBuilderNVL.and(qCitt1Entity.unit.containsIgnoreCase(itemInVendor.getUnit()));
        }
        if (!StringUtils.isEmpty(itemInVendor.getNote())) {
            booleanBuilder.and(qCoittEntity.note.containsIgnoreCase(itemInVendor.getNote()));
            booleanBuilderNVL.and(qCitt1Entity.note.containsIgnoreCase(itemInVendor.getNote()));
        }
        if (itemInVendor.getLeadTime() != null) {
            booleanBuilder.and(qLeadTimeEntity.leadTime.eq(itemInVendor.getLeadTime()));
            booleanBuilderNVL.and(qLeadTimeEntity.leadTime.eq(itemInVendor.getLeadTime()));
        }
        if (itemInVendor.getPriceMQQ() != null) {
            booleanBuilder.and(qMqqPriceEntity.price.eq(itemInVendor.getPriceMQQ()));
            booleanBuilderNVL.and(qMqqPriceEntity.price.eq(itemInVendor.getPriceMQQ()));
        }
        if (!StringUtils.isEmpty(itemInVendor.getCurrency())) {
            booleanBuilder.and(qMqqPriceEntity.currency.containsIgnoreCase(itemInVendor.getCurrency()));
            booleanBuilderNVL.and(qMqqPriceEntity.currency.containsIgnoreCase(itemInVendor.getCurrency()));
        }

        query.where(booleanBuilder).groupBy(qCoittEntity.productCode);
        queryNVL.where(booleanBuilderNVL).groupBy(qCitt1Entity.materialCode);

        List<DataItemInVendor> resultListTP = query.fetch();
        List<DataItemInVendor> resultListNVL = queryNVL.fetch();
        List<DataItemInVendor> resultList = new ArrayList<>(resultListTP);
        for (int i = 0; i < resultListNVL.size(); i++) {
            resultList.add(resultListNVL.get(i));
        }

        Collections.sort(
            resultList,
            new Comparator<DataItemInVendor>() {
                @Override
                public int compare(DataItemInVendor o1, DataItemInVendor o2) {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
            }
        );

        if (input.getPageSize() != 0) {
            resultList = paginate(resultList, input.getPageNumber(), input.getPageSize());
        }

        return new PageResponse<List<DataItemInVendor>>().data(resultList).dataCount(query.fetchCount() + queryNVL.fetchCount());
    }

    @Override
    public Page<CoittEntity> getAllBom(PageFilterInput<CoittDTO> input, Pageable pageable, List<String> coittCodes) {
        QCoittEntity qCoittEntity = QCoittEntity.coittEntity;
        QAssemblyEntity qAssemblyEntity = QAssemblyEntity.assemblyEntity;
        CoittDTO coittDTO = input.getFilter();
        String common = input.getCommon();
        JPAQuery<CoittEntity> query = new JPAQueryFactory(em)
            .selectFrom(qCoittEntity)
            .innerJoin(qAssemblyEntity)
            .on(qCoittEntity.productCode.eq(qAssemblyEntity.parentCode));

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
        if (pageable.isPaged()) {
            query = query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qCoittEntity.isActive.isTrue());
        if (!StringUtils.isEmpty(common)) {
            booleanBuilder.and(
                qCoittEntity.productCode
                    .containsIgnoreCase(common)
                    .or(qCoittEntity.proName.containsIgnoreCase(common))
                    .or(qCoittEntity.unit.containsIgnoreCase(common))
                    .or(qCoittEntity.version.containsIgnoreCase(common))
                    .or(qCoittEntity.note.containsIgnoreCase(common))
                    .or(qCoittEntity.notice.containsIgnoreCase(common))
                    //                .or(qCoittEntity.itemGroupCode.stringValue().containsIgnoreCase(common))
                    //                .or(qCoittEntity.isTemplate.stringValue().containsIgnoreCase(common))
                    //                .or(qCoittEntity.parent.stringValue().containsIgnoreCase(common))
                    //.or(qCoittEntity.id.in(propertySubQueryCommon))
                    .or(qCoittEntity.techName.containsIgnoreCase(common))
            );
        }

        if (coittCodes != null && coittCodes.size() > 0) {
            booleanBuilder.and(qCoittEntity.productCode.in(coittCodes));
        }

        if (!StringUtils.isEmpty(coittDTO.getProductCode())) {
            booleanBuilder.and(qCoittEntity.productCode.containsIgnoreCase(coittDTO.getProductCode()));
        }
        if (!StringUtils.isEmpty(coittDTO.getProName())) {
            booleanBuilder.and(qCoittEntity.proName.containsIgnoreCase(coittDTO.getProName()));
        }
        if (!StringUtils.isEmpty(coittDTO.getUnit())) {
            booleanBuilder.and(qCoittEntity.unit.containsIgnoreCase(coittDTO.getUnit()));
        }
        if (!StringUtils.isEmpty(coittDTO.getVersion())) {
            booleanBuilder.and(qCoittEntity.version.containsIgnoreCase(coittDTO.getVersion()));
        }
        if (!StringUtils.isEmpty(coittDTO.getNote())) {
            booleanBuilder.and(qCoittEntity.note.containsIgnoreCase(coittDTO.getNote()));
        }
        if (!StringUtils.isEmpty(coittDTO.getNotice())) {
            booleanBuilder.and(qCoittEntity.notice.containsIgnoreCase(coittDTO.getNotice()));
        }
        if (coittDTO.getItemGroupCode() != null) {
            booleanBuilder.and(qCoittEntity.itemGroupCode.eq(coittDTO.getItemGroupCode()));
        }
        if (coittDTO.getTemplate() != null) {
            booleanBuilder.and(qCoittEntity.isTemplate.eq(coittDTO.getTemplate()));
        }
        if (coittDTO.getParent() != null) {
            booleanBuilder.and(qCoittEntity.parent.eq(coittDTO.getParent()));
        }
        if (coittDTO.getStatus() != null) {
            booleanBuilder.and(qCoittEntity.status.eq(coittDTO.getStatus()));
        }

        if (!StringUtils.isEmpty(coittDTO.getMerchandiseGroup())) {
            booleanBuilder.and(qCoittEntity.merchandiseGroupEntity.merchandiseGroupCode.eq(coittDTO.getMerchandiseGroup()));
        }

        if (!StringUtils.isEmpty(coittDTO.getTechName())) {
            booleanBuilder.and(qCoittEntity.techName.containsIgnoreCase(coittDTO.getTechName()));
        }

        Map<String, ColumnPropertyEntity> columnPropertyEntities = new HashMap<>();
        Map<String, ColumnPropertyEntity> btpPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.BTP
        );
        Map<String, ColumnPropertyEntity> tpPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.TP
        );
        for (String s : btpPropertyEntities.keySet()) {
            columnPropertyEntities.put(s, btpPropertyEntities.get(s));
        }
        for (String s : tpPropertyEntities.keySet()) {
            columnPropertyEntities.put(s, tpPropertyEntities.get(s));
        }

        if (!coittDTO.getPropertiesMap().isEmpty()) {
            for (String keyName : coittDTO.getPropertiesMap().keySet()) {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                String value = coittDTO.getPropertiesMap().get(keyName);
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
                    booleanBuilder.and(qCoittEntity.id.in(propertySubQuery));
                }
            }
        }

        if (!StringUtils.isEmpty(common)) {
            columnPropertyEntities.forEach((s, columnPropertyEntity) -> {
                JPAQuery<Integer> propertySubQuery = new JPAQuery<>(em).select(qKeyValueEntityV2.entityKey).from(qKeyValueEntityV2);
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.isTrue());
                dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                if (coittDTO.getItemGroupCode() == Contants.ItemGroup.BTP) {
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.entityType.eq(Contants.EntityType.BTP));
                } else if (coittDTO.getItemGroupCode() == Contants.ItemGroup.TP) {
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.entityType.eq(Contants.EntityType.TP));
                }
                dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(common));
                propertySubQuery.where(dynamicBooleanBuilder);
                booleanBuilder.or(qCoittEntity.id.in(propertySubQuery));
            });
        }

        if (!StringUtils.isEmpty(input.getSortProperty())) {
            Integer entityType = Contants.EntityType.TP;
            if (coittDTO.getItemGroupCode() == Contants.ItemGroup.NVL) {
                entityType = Contants.EntityType.NVL;
            } else if (coittDTO.getItemGroupCode() == Contants.ItemGroup.BTP) {
                entityType = Contants.EntityType.BTP;
            }
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
                                        .eq(qCoittEntity.id)
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
                            Path<Object> fieldPath = Expressions.path(Object.class, qCoittEntity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                if (input.getSortProperty().equals("createdAt")) {
                    Path<Object> fieldPath = Expressions.path(Object.class, qAssemblyEntity, input.getSortProperty());
                    query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                } else {
                    Path<Object> fieldPath = Expressions.path(Object.class, qCoittEntity, input.getSortProperty());
                    query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                }
            }
        }
        query.where(booleanBuilder);
        query.groupBy(qCoittEntity.productCode);
        List<CoittEntity> result = query.fetch();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }

    @Override
    public Page<CoittEntity> getAllBomChild(PageFilterInput<CoittDTO> input, Pageable pageable, List<String> coittCodes) {
        QCoittEntity qCoittEntity = QCoittEntity.coittEntity;
        QAssemblyEntity qAssemblyEntity = QAssemblyEntity.assemblyEntity;
        CoittDTO coittDTO = input.getFilter();
        String common = input.getCommon();
        JPAQuery<CoittEntity> query = new JPAQueryFactory(em)
            .selectFrom(qCoittEntity)
            .innerJoin(qAssemblyEntity)
            .on(qCoittEntity.productCode.eq(qAssemblyEntity.childCode));

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qCoittEntity.isActive.eq(true));

        if (coittCodes != null && coittCodes.size() > 0) {
            booleanBuilder.and(qCoittEntity.productCode.in(coittCodes));
        }
        if (!StringUtils.isEmpty(input.getSortProperty())) {
            Integer entityType = Contants.EntityType.TP;
            if (coittDTO.getItemGroupCode() == Contants.ItemGroup.NVL) {
                entityType = Contants.EntityType.NVL;
            } else if (coittDTO.getItemGroupCode() == Contants.ItemGroup.BTP) {
                entityType = Contants.EntityType.BTP;
            }
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
                                        .eq(qCoittEntity.id)
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
                            Path<Object> fieldPath = Expressions.path(Object.class, qCoittEntity, input.getSortProperty());
                            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                        }
                    }
                }
            } else {
                // Default col
                if (input.getSortProperty().equals("createdAt")) {
                    Path<Object> fieldPath = Expressions.path(Object.class, qAssemblyEntity, input.getSortProperty());
                    query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                } else {
                    Path<Object> fieldPath = Expressions.path(Object.class, qCoittEntity, input.getSortProperty());
                    query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                }
            }
        }
        query.where(booleanBuilder);
        query.groupBy(qCoittEntity.productCode);
        List<CoittEntity> result = query.fetch();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }
}
