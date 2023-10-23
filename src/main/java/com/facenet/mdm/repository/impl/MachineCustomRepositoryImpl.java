package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.custom.MachineCustomRepository;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.querydsl.core.BooleanBuilder;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MachineCustomRepositoryImpl implements MachineCustomRepository {

    private final EntityManager entityManager;
    private final ColumnPropertyRepository columnPropertyRepository;

    public MachineCustomRepositoryImpl(
        EntityManager entityManager,
        @Qualifier("columnPropertyRepository") ColumnPropertyRepository columnPropertyRepository
    ) {
        this.entityManager = entityManager;
        this.columnPropertyRepository = columnPropertyRepository;
    }

    @Override
    public Page<MachineEntity> getAll(PageFilterInput<MachineDTO> input, Pageable pageable) {
        MachineDTO filter = input.getFilter();
        QMachineEntity qMachineEntity = QMachineEntity.machineEntity;
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        JPAQuery<MachineEntity> query = new JPAQueryFactory(entityManager)
            .selectFrom(qMachineEntity)
            .join(qMachineEntity.machineType)
            .fetchJoin()
            .leftJoin(qKeyValueEntityV2)
            .on(qKeyValueEntityV2.entityType.eq(Contants.EntityType.MACHINE))
            .on(qMachineEntity.id.eq(qKeyValueEntityV2.entityKey));

        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qMachineEntity.isActive.isTrue());

        //Check search common or not?
        if (!StringUtils.isEmpty(input.getCommon())) {
            booleanBuilder.and(
                qMachineEntity.machineCode
                    .containsIgnoreCase(input.getCommon())
                    .or(qMachineEntity.machineName.containsIgnoreCase(input.getCommon()))
                    .or(qMachineEntity.machineType.machineTypeName.containsIgnoreCase(input.getCommon()))
                    .or(qMachineEntity.supplier.containsIgnoreCase(input.getCommon()))
                    .or(qMachineEntity.maxProductionQuantity.like("%" + input.getCommon() + "%"))
                    .or(qMachineEntity.minProductionQuantity.like("%" + input.getCommon() + "%"))
                    .or(qMachineEntity.maintenanceTime.like("%" + input.getCommon() + "%"))
                    .or(qMachineEntity.cycleTime.like("%" + input.getCommon() + "%"))
                    .or(qMachineEntity.maxWaitingTime.like("%" + input.getCommon() + "%"))
                    .or(qMachineEntity.productivity.like("%" + input.getCommon() + "%"))
                    .or(qKeyValueEntityV2.commonValue.containsIgnoreCase(input.getCommon()))
            );
        }
        if (!StringUtils.isEmpty(filter.getMachineCode())) {
            booleanBuilder.and(qMachineEntity.machineCode.containsIgnoreCase(filter.getMachineCode()));
        }
        if (!StringUtils.isEmpty(filter.getMachineName())) {
            booleanBuilder.and(qMachineEntity.machineName.containsIgnoreCase(filter.getMachineName()));
        }
        if (filter.getMachineType().getMachineTypeName() != null) {
            booleanBuilder.and(qMachineEntity.machineType.machineTypeName.containsIgnoreCase(filter.getMachineType().getMachineTypeName()));
        }
        if (filter.getProductivity() != null) {
            booleanBuilder.and(qMachineEntity.productivity.eq(filter.getProductivity()));
        }
        if (!StringUtils.isEmpty(filter.getSupplier())) {
            booleanBuilder.and(qMachineEntity.supplier.containsIgnoreCase(filter.getSupplier()));
        }
        if (filter.getStatus() != null) {
            booleanBuilder.and(qMachineEntity.status.eq(filter.getStatus()));
        }
        if (filter.getMaintenanceTime() != null) {
            booleanBuilder.and(qMachineEntity.maintenanceTime.eq(filter.getMaintenanceTime()));
        }
        if (filter.getMinProductionQuantity() != null) {
            booleanBuilder.and(qMachineEntity.minProductionQuantity.eq(filter.getMinProductionQuantity()));
        }
        if (filter.getMaxProductionQuantity() != null) {
            booleanBuilder.and(qMachineEntity.maxProductionQuantity.eq(filter.getMaxProductionQuantity()));
        }
        if (filter.getPurchaseDate() != null) {
            booleanBuilder.and(qMachineEntity.purchaseDate.eq(filter.getPurchaseDate()));
        }

        if (filter.getPurchaseDateStart() != null && filter.getPurchaseDateEnd() != null) {
            booleanBuilder.and(qMachineEntity.purchaseDate.between(filter.getPurchaseDateStart(), filter.getPurchaseDateEnd()));
        }

        if (filter.getMaxWaitingTime() != null) {
            booleanBuilder.and(qMachineEntity.maxWaitingTime.eq(filter.getMaxWaitingTime()));
        }
        if (filter.getCycleTime() != null) {
            booleanBuilder.and(qMachineEntity.cycleTime.eq(filter.getCycleTime()));
        }
        if (filter.getPropertiesMap() != null || filter.getPropertiesMap().isEmpty()) {
            Map<String, ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
                Contants.EntityType.MACHINE
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
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.MACHINE);
            boolean isSorted = false;
            for (ColumnPropertyEntity column : columns) {
                // Valid sort column name
                if (input.getSortProperty().equals(column.getKeyName())) {
                    isSorted = true;
                    if (column.getIsFixed() == 1) {
                        // Default col
                        Path<Object> fieldPath = Expressions.path(Object.class, qMachineEntity, input.getSortProperty());
                        query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                    } else {
                        // Dynamic col
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qMachineEntity.id)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.MACHINE))
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
                Path<Object> fieldPath = Expressions.path(Object.class, qMachineEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }
        query.where(booleanBuilder).groupBy(qMachineEntity.id);
        System.err.println(query);
        List<MachineEntity> result = query.fetch();

        return new PageImpl<>(result, pageable, query.fetchCount());
    }

    @Override
    public List<String> getAutoComplete(String value, String keyName) {
        PathBuilder<MachineEntity> path = new PathBuilder<>(MachineEntity.class, "entity");
        return new JPAQueryFactory(entityManager)
            .select(path.getString(keyName))
            .from(path)
            .where(path.getString(keyName).containsIgnoreCase(value))
            .limit(10)
            .fetch();
    }
}
