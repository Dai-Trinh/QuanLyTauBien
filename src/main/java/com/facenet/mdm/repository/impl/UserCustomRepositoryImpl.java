package com.facenet.mdm.repository.impl;

import com.facenet.mdm.domain.Authority;
import com.facenet.mdm.domain.QUser;
import com.facenet.mdm.domain.User;
import com.facenet.mdm.repository.custom.UserCustomRepository;
import com.facenet.mdm.service.dto.AdminUserDTO;
import com.facenet.mdm.service.mapper.UserMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final EntityManager entityManager;
    private final UserMapper userMapper;

    public UserCustomRepositoryImpl(EntityManager entityManager, UserMapper userMapper) {
        this.entityManager = entityManager;
        this.userMapper = userMapper;
    }

    @Override
    public Page<User> getAllUser(PageFilterInput<AdminUserDTO> input, Pageable pageable) {
        QUser qUser = QUser.user;
        JPAQuery<User> query = new JPAQueryFactory(entityManager).selectFrom(qUser);
        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (!StringUtils.isEmpty(input.getFilter().getUsername())) {
            booleanBuilder.and(qUser.username.containsIgnoreCase(input.getFilter().getUsername()));
        }
        if (!StringUtils.isEmpty(input.getFilter().getEmail())) {
            booleanBuilder.and(qUser.username.containsIgnoreCase(input.getFilter().getUsername()));
        }
        if (!StringUtils.isEmpty(input.getFilter().getFirstName())) {
            booleanBuilder.and(qUser.firstName.containsIgnoreCase(input.getFilter().getFirstName()));
        }
        if (!StringUtils.isEmpty(input.getFilter().getLastName())) {
            booleanBuilder.and(qUser.lastName.containsIgnoreCase(input.getFilter().getLastName()));
        }
        if (!CollectionUtils.isEmpty(input.getFilter().getAuthorities())) {
            for (Authority authority : userMapper.authoritiesFromStrings(input.getFilter().getAuthorities())) {
                booleanBuilder.and(qUser.authorities.contains(authority));
            }
        }
        query.where(booleanBuilder);
        return new PageImpl<>(query.fetch(), pageable, query.fetchCount());
    }
}
