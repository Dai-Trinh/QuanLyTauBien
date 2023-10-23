package com.facenet.mdm.repository;

import com.facenet.mdm.domain.ColumnPropertyEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facenet.mdm.repository.custom.ColumnPropertyCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ColumnPropertyRepository extends JpaRepository<ColumnPropertyEntity, Integer>, ColumnPropertyCustomRepository {
    List<ColumnPropertyEntity> findByKeyNameInAndEntityTypeAndIsActiveTrue(Collection<String> keyName, Integer entityType);
    int countByEntityTypeAndIsActiveTrue(Integer entityType);

    @Query("select c from ColumnPropertyEntity c where c.keyName = :keyName and c.entityType = :entityType and c.isActive = true")
    ColumnPropertyEntity findByKeyName(@Param("keyName") String keyName, @Param("entityType") Integer entityType);

    @Query(
        "select c from ColumnPropertyEntity c where c.entityType = :entityType and c.check = true and c.isActive = true  order by c.index asc"
    )
    List<ColumnPropertyEntity> getAllVisiblePropertyByEntityType(@Param("entityType") Integer entityType);

    @Query("select c from ColumnPropertyEntity c where c.entityType = :entityType and c.isActive = true order by c.index asc ")
    List<ColumnPropertyEntity> getAllByEntityTypeAndIsActiveTrue(@Param("entityType") Integer entityType);

    @Query("select c from ColumnPropertyEntity c where c.entityType = :entityType and c.isActive = true order by c.index asc")
    List<ColumnPropertyEntity> getAllColumnByEntityType(@Param("entityType") Integer entityType);

    default Map<String, ColumnPropertyEntity> getAllColumnByEntityTypeByMap(Integer entityType) {
        return getAllColumnByEntityType(entityType)
            .stream()
            .collect(Collectors.toMap(ColumnPropertyEntity::getKeyName, Function.identity()));
    }

    @Query("select c from ColumnPropertyEntity c where c.entityType = :entityType and c.isActive = true and c.isFixed = 0 ")
    List<ColumnPropertyEntity> getAllDynamicColumnByEntityType(@Param("entityType") Integer entityType);

    default Map<String, ColumnPropertyEntity> getAllDynamicColumnByEntityTypeByMap(Integer entityType) {
        return getAllDynamicColumnByEntityType(entityType)
            .stream()
            .collect(Collectors.toMap(ColumnPropertyEntity::getKeyName, Function.identity()));
    }

    List<ColumnPropertyEntity> getAllByIndexGreaterThanEqualAndIsActive(Integer index, Boolean isActive);

    @Query(
        "select c from ColumnPropertyEntity c where c.entityType = :entityType and lower(replace(c.keyTitle, ' ', '')) like lower(replace(:keyTitle, ' ', '')) and c.isActive = true"
    )
    ColumnPropertyEntity getByKeyTitleAndEntityType(@Param("keyTitle") String keyTitle, @Param("entityType") Integer entityType);
    //    Page<ColumnPropertyEntity>
}
