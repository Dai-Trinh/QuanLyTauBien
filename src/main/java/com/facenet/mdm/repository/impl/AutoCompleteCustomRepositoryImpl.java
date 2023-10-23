package com.facenet.mdm.repository.impl;

import com.facenet.mdm.repository.custom.AutoCompleteCustomRepository;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class AutoCompleteCustomRepositoryImpl<T> implements AutoCompleteCustomRepository<T> {

    private final EntityManager entityManager;

    public AutoCompleteCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<String> getAutoComplete(String keyName, String value, Class<? extends T> type) {
        PathBuilder<? extends T> path = new PathBuilder<>(type, "entity");
        return new JPAQueryFactory(entityManager)
            .selectDistinct(path.getString(keyName))
            .from(path)
            .where(path.getString(keyName).containsIgnoreCase(value))
            .limit(10)
            .fetch();
    }
}
