package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.BusinessLogEntity;
import com.facenet.mdm.domain.QBusinessLogEntity;
import com.facenet.mdm.repository.custom.BusinessLogCustomRepository;
import com.facenet.mdm.service.dto.BusinessLogDTO;
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
public class BusinessLogCustomRepositoryImpl implements BusinessLogCustomRepository {

    private final EntityManager entityManager;

    public BusinessLogCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<BusinessLogEntity> getAllLog(PageFilterInput<BusinessLogDTO> input, Pageable pageable) {
        QBusinessLogEntity qBusinessLogEntity = QBusinessLogEntity.businessLogEntity;
        JPAQuery<BusinessLogEntity> query = new JPAQueryFactory(entityManager).selectFrom(qBusinessLogEntity);
        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(input.getFilter().getUserName())) {
            booleanBuilder.and(qBusinessLogEntity.userName.containsIgnoreCase(input.getFilter().getUserName()));
        }
        if (!StringUtils.isEmpty(input.getFilter().getActionName())) {
            booleanBuilder.and(qBusinessLogEntity.actionName.containsIgnoreCase(input.getFilter().getActionName()));
        }
        if (!StringUtils.isEmpty(input.getFilter().getFunctionName())) {
            booleanBuilder.and(qBusinessLogEntity.functionName.containsIgnoreCase(input.getFilter().getFunctionName()));
        }
        if (input.getFilter().getStartCreatedAt() != null && input.getFilter().getEndCreatedAt() != null) {
            booleanBuilder.and(qBusinessLogEntity.createdAt.goe(input.getFilter().getStartCreatedAt()));
            booleanBuilder.and(qBusinessLogEntity.createdAt.loe(input.getFilter().getEndCreatedAt()));
        }
        if (!StringUtils.isEmpty(input.getCommon())) {
            booleanBuilder.and(
                qBusinessLogEntity.userName
                    .containsIgnoreCase(input.getCommon())
                    .or(qBusinessLogEntity.actionName.containsIgnoreCase(input.getCommon()))
                    .or(qBusinessLogEntity.functionName.containsIgnoreCase(input.getCommon()))
            );
        }
        query.where(booleanBuilder);
        if (!StringUtils.isEmpty(input.getSortProperty())) {
            Path<Object> fieldPath = Expressions.path(Object.class, qBusinessLogEntity, input.getSortProperty());
            query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
        }
        return new PageImpl<>(query.fetch(), pageable, query.fetchCount());
    }
}
