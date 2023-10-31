package com.facenet.mdm.repository;

import com.facenet.mdm.domain.SeaportEntity;
import com.facenet.mdm.repository.custom.SeaportCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeaportRepository extends JpaRepository<SeaportEntity, Integer> {

    @Query("select s from SeaportEntity s where s.seaportCode = :seaportCode and s.isActive = true")
    SeaportEntity getSeaportByCode(@Param("seaportCode") String seaportCode);

}
