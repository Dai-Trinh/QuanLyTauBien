package com.facenet.mdm.repository;

import com.facenet.mdm.domain.TeamGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamGroupRepository extends JpaRepository<TeamGroupEntity, Integer> {

    @Query("select t from TeamGroupEntity t where t.isActive = true and t.teamGroupCode = :teamGroupCode")
    TeamGroupEntity getTeamGroupEntitieByCode(@Param("teamGroupCode") String teamGroupCode);

    @Query("select t from TeamGroupEntity t where  t.isActive = true and t.teamGroupName = :teamGroupName")
    TeamGroupEntity getTeamGroupEntityByName(@Param("teamGroupName") String teamGroupName);

}
