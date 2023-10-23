package com.facenet.mdm.repository;

import com.facenet.mdm.domain.Param;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParamRepository extends JpaRepository<Param, Integer> {
    @Query("select p.paramValue from Param p where p.paramCode = :paramCode")
    Set<String> findAllParamValueByCode(@org.springframework.data.repository.query.Param("paramCode") String paramCode);

    List<Param> getAllByParamCode(Object paramCode);
    Boolean existsAllByParamCodeAndParamValue(String paramCode, String paramValue);
}
