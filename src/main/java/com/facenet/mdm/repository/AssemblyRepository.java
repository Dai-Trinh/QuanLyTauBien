package com.facenet.mdm.repository;

import com.facenet.mdm.domain.AssemblyEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssemblyRepository extends JpaRepository<AssemblyEntity, Integer> {
    @Query("select a.parentCode from AssemblyEntity a group by a.parentCode")
    List<String> getAllParentCode();

    @Query("select a.childCode from AssemblyEntity a where a.parentCode = :parentCode and a.childCode is not null ")
    List<String> getChildCode(@Param("parentCode") String parentCode);

    @Query("select a from AssemblyEntity a where a.parentCode = :parentCode group by a.parentCode")
    AssemblyEntity getAssemblyEntitiesByCode(@Param("parentCode") String parentCode);

    @Query("select a from AssemblyEntity a where a.parentCode = :parentCode and a.childCode = :childCode")
    AssemblyEntity getAssemblyEntities(@Param("parentCode") String parentCode, @Param("childCode") String childCode);
}
