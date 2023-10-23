package com.facenet.mdm.service;

import com.facenet.mdm.domain.Citt1Entity;
import com.facenet.mdm.domain.CoittEntity;
import com.facenet.mdm.domain.MaterialReplacementEntity;
import com.facenet.mdm.repository.Citt1Repository;
import com.facenet.mdm.repository.CoittRepository;
import com.facenet.mdm.repository.MaterialReplacementRepository;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MaterialReplacementService {

    @Autowired
    CoittRepository coittRepository;

    @Autowired
    Citt1Repository citt1Repository;

    @Autowired
    MaterialReplacementRepository materialReplacementRepository;

    public void addMaterialReplacement(String materialCode, List<String> materialReplacementCodeList) {
        Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(materialCode);
        if (
            !coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(materialCode, true) && citt1Entity == null
        ) throw new CustomException(HttpStatus.NOT_FOUND, "not.found", materialCode);
        for (String materialReplacementCode : materialReplacementCodeList) {
            Citt1Entity citt1EntityReplace = citt1Repository.findCitt1EntitiesByCode(materialReplacementCode);
            if (
                !coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(materialReplacementCode, true) && citt1EntityReplace == null
            ) throw new CustomException(HttpStatus.NOT_FOUND, "not.found", materialReplacementCode);
        }
        for (String materialReplacementCode : materialReplacementCodeList) {
            MaterialReplacementEntity materialReplacementEntity = materialReplacementRepository.findByMaterialCodeAndMaterialReplacementCode(
                materialCode,
                materialReplacementCode
            );
            if (materialReplacementEntity == null) {
                materialReplacementEntity = new MaterialReplacementEntity(materialCode, materialReplacementCode);
                materialReplacementRepository.save(materialReplacementEntity);
            }
        }
        //         else {
        //            throw new CustomException(HttpStatus.CONFLICT, "duplicate", materialCode + "---" + materialReplacementCode);
        //        }
    }

    public CommonResponse removeMaterialReplacement(String materialCode, String materialReplacementCode) {
        Citt1Entity citt1Entity = citt1Repository.findCitt1EntitiesByCode(materialCode);
        if (
            !coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(materialCode, true) && citt1Entity == null
        ) throw new CustomException(HttpStatus.NOT_FOUND, "not.found", materialCode);
        Citt1Entity citt1EntityReplace = citt1Repository.findCitt1EntitiesByCode(materialReplacementCode);
        if (
            !coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(materialReplacementCode, true) && citt1EntityReplace == null
        ) throw new CustomException(HttpStatus.NOT_FOUND, "not.found", materialReplacementCode);
        MaterialReplacementEntity materialReplacementEntity = materialReplacementRepository.findByMaterialCodeAndMaterialReplacementCode(
            materialCode,
            materialReplacementCode
        );
        if (materialReplacementEntity != null) {
            materialReplacementRepository.delete(materialReplacementEntity);
        } else {
            throw new CustomException(HttpStatus.NOT_FOUND, "not.found", materialCode + "---" + materialReplacementCode);
        }
        return new CommonResponse().success();
    }

    public CommonResponse getMaterialReplacementList(PageFilterInput<String> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        if (
            !coittRepository.existsAllByProductCodeIgnoreCaseAndIsActive(input.getFilter(), true) &&
            citt1Repository.findCitt1EntitiesByCode(input.getFilter()) == null
        ) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        Page<MaterialReplacementEntity> materialReplacementEntity = materialReplacementRepository.findByMaterialCodeIgnoreCase(
            input.getFilter(),
            pageable
        );
        //        if (materialReplacementEntity.getContent().isEmpty()) {
        //            throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        //        }
        List<String> materialCodeList = new ArrayList<>();
        for (MaterialReplacementEntity m : materialReplacementEntity.getContent()) {
            materialCodeList.add(m.getMaterialReplacementCode());
        }
        List<CoittEntity> resultList = coittRepository.getAllByProductCodeIn(materialCodeList);
        return new PageResponse().data(resultList).dataCount(materialReplacementEntity.getTotalElements()).success();
    }
}
