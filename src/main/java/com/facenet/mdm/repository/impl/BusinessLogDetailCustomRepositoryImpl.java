package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.BusinessLogDetailEntity;
import com.facenet.mdm.domain.BusinessLogEntity;
import com.facenet.mdm.domain.QBusinessLogDetailEntity;
import com.facenet.mdm.domain.QColumnPropertyEntity;
import com.facenet.mdm.repository.custom.BusinessLogDetailCustomRepository;
import com.facenet.mdm.service.dto.BusinessLogDetailDTO;
import com.facenet.mdm.service.dto.QBusinessLogDetailDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class BusinessLogDetailCustomRepositoryImpl implements BusinessLogDetailCustomRepository {

    private final EntityManager entityManager;

    public BusinessLogDetailCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<BusinessLogDetailDTO> getAllByBusinessLogEntity(
        BusinessLogEntity businessLogEntity,
        PageFilterInput<BusinessLogDetailDTO> filter,
        Pageable pageable
    ) {
        QBusinessLogDetailEntity qBusinessLogDetailEntity = QBusinessLogDetailEntity.businessLogDetailEntity;
        QColumnPropertyEntity qColumnPropertyEntity = QColumnPropertyEntity.columnPropertyEntity;
        JPAQuery<BusinessLogDetailDTO> query = new JPAQueryFactory(entityManager)
            .select(
                new QBusinessLogDetailDTO(
                    qColumnPropertyEntity.keyTitle,
                    qBusinessLogDetailEntity.lastValue,
                    qBusinessLogDetailEntity.newValue
                )
            )
            .from(qBusinessLogDetailEntity)
            .join(qColumnPropertyEntity)
            .on(
                qBusinessLogDetailEntity.keyName
                    .eq(qColumnPropertyEntity.keyName)
                    .and(qColumnPropertyEntity.entityType.eq(businessLogEntity.getEntityType()))
            );

        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qBusinessLogDetailEntity.businessLog.eq(businessLogEntity));
        if (!StringUtils.isEmpty(filter.getFilter().getKeyTitle())) {
            booleanBuilder.and(qColumnPropertyEntity.keyTitle.containsIgnoreCase(filter.getFilter().getKeyTitle()));
        }
        if (!StringUtils.isEmpty(filter.getFilter().getLastValue())) {
            booleanBuilder.and(qBusinessLogDetailEntity.lastValue.containsIgnoreCase(filter.getFilter().getLastValue()));
        }
        if (!StringUtils.isEmpty(filter.getFilter().getNewValue())) {
            booleanBuilder.and(qBusinessLogDetailEntity.newValue.containsIgnoreCase(filter.getFilter().getNewValue()));
        }
        query.where(booleanBuilder);

        if (!StringUtils.isEmpty(filter.getSortProperty())) {
            switch (filter.getSortProperty()) {
                case "newValue":
                    query.orderBy(new OrderSpecifier<>(filter.getSortOrder(), qBusinessLogDetailEntity.newValue));
                    break;
                case "lastValue":
                    query.orderBy(new OrderSpecifier<>(filter.getSortOrder(), qBusinessLogDetailEntity.lastValue));
                    break;
                case "keyTitle":
                    query.orderBy(new OrderSpecifier<>(filter.getSortOrder(), qColumnPropertyEntity.keyTitle));
                    break;
            }
        }
        return new PageImpl<>(query.fetch(), pageable, query.fetchCount());
    }
}
