package com.facenet.mdm.repository;

import com.facenet.mdm.domain.MerchandiseGroupEntity;
import com.facenet.mdm.service.dto.MerchandiseGroupDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MerchandiseGroupRepository extends JpaRepository<MerchandiseGroupEntity, Integer> {
    @Query("select m from MerchandiseGroupEntity m where m.isActive = true and m.merchandiseGroupCode = :merchandiseGroupCode")
    MerchandiseGroupEntity getMerchandiseGroupEntitiesByCode(@Param("merchandiseGroupCode") String merchandiseGroupCode);

    @Query("select m from MerchandiseGroupEntity m where m.isActive = true ")
    List<MerchandiseGroupEntity> getAllMerchandiseGroup();
}
