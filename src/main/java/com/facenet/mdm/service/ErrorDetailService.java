package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.ErrorDTO;
import com.facenet.mdm.service.dto.ErrorDetailDTO;
import com.facenet.mdm.service.dto.ErrorGroupDTO;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.ErrorGroupMapper;
import com.facenet.mdm.service.mapper.ErrorMapper;
import com.facenet.mdm.service.model.ErrorGroupResponse;
import com.facenet.mdm.service.model.ResultCode;
import com.facenet.mdm.service.utils.Contants;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ErrorDetailService {

    @Autowired
    ErrorDetailResponsitory errorDetailResponsitory;

    @Autowired
    ErrorResponesitory errorResponesitory;

    @Autowired
    ErrorGroupResponsitory errorGroupResponsitory;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    public CommonResponse getErrorGroupByError(String erorrGroupCode) {
        List<ErrorEntity> errorEntityList = new ArrayList<>();
        List<ErrorDTO> errorDTOS = new ArrayList<>();

        QErrorEntity qErrorEntity = QErrorEntity.errorEntity;
        QErrorDetailEntity qErrorDetailEntity = QErrorDetailEntity.errorDetailEntity;
        QErrorGroupEntity qErrorGroupEntity = QErrorGroupEntity.errorGroupEntity;
        JPAQuery query = new JPAQueryFactory(entityManager)
            .selectFrom(qErrorEntity)
            .innerJoin(qErrorDetailEntity)
            .on(qErrorEntity.errorId.eq(qErrorDetailEntity.errorId))
            .innerJoin(qErrorGroupEntity)
            .on(qErrorDetailEntity.errorGroupId.eq(qErrorGroupEntity.errorGroupId));
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qErrorEntity.isActive.eq(1));
        booleanBuilder.and(qErrorGroupEntity.errorGroupCode.eq(erorrGroupCode));
        booleanBuilder.and(qErrorGroupEntity.isActive.eq(1));
        query.where(booleanBuilder).orderBy(qErrorEntity.errorId.desc());

        errorEntityList = query.fetch();

        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.ERROR);

        Map<Integer, List<KeyValueEntityV2>> errorPropertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        for (ErrorEntity errorEntity : errorEntityList) {
            JPAQuery queryErrorGroup = new JPAQueryFactory(entityManager)
                .select(qErrorGroupEntity.errorGroupName)
                .from(qErrorGroupEntity)
                .join(qErrorDetailEntity)
                .on(qErrorGroupEntity.errorGroupId.eq(qErrorDetailEntity.errorGroupId));
            BooleanBuilder booleanBuilderErrorGroup = new BooleanBuilder();
            booleanBuilderErrorGroup.and(qErrorGroupEntity.isActive.eq(1));
            booleanBuilderErrorGroup.and(qErrorDetailEntity.errorId.eq(errorEntity.getErrorId()));
            queryErrorGroup.where(booleanBuilderErrorGroup);
            List<String> errorGroupName = queryErrorGroup.fetch();
            errorDTOS.add(ErrorMapper.entytoDTOMap(errorEntity, errorPropertyMap.get(errorEntity.getErrorId()), errorGroupName));
        }

        List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.ERROR
        );

        return new PageResponse<List<ErrorDTO>>(errorEntityList.size()).success().data(errorDTOS).columns(keyDictionaryDTOS);
    }

    @Transactional
    public CommonResponse createErrorWithErrorGroup(String errorCode, String errorGroupCode) {
        ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorCode);
        ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(errorGroupCode);
        if (errorEntity == null || errorGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại");
        } else {
            ErrorDetailEntity errorDetailEntity = errorDetailResponsitory.getErrorDetailEntitiesByIdErrorG(
                errorEntity.getErrorId(),
                errorGroupEntity.getErrorGroupId()
            );
            if (errorDetailEntity == null) {
                errorDetailEntity = new ErrorDetailEntity();
                errorDetailEntity.setErrorId(errorEntity.getErrorId());
                errorDetailEntity.setErrorGroupId(errorGroupEntity.getErrorGroupId());
                errorDetailResponsitory.save(errorDetailEntity);
            }
            return new CommonResponse().success("Thêm thành công");
        }
    }

    @Transactional
    public CommonResponse updateErrorDetail(String errorCode, String errorGroupCode, ErrorDetailDTO errorDetailDTO) {
        ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorCode);
        ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(errorGroupCode);
        if (errorEntity == null || errorGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại lỗi hoặc nhóm lỗi");
        } else {
            try {
                ErrorDetailEntity errorDetailEntity = errorDetailResponsitory.getErrorDetailEntitiesByIdErrorG(
                    errorEntity.getErrorId(),
                    errorGroupEntity.getErrorGroupId()
                );
                if (errorDetailEntity == null) {
                    throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại");
                }
                ErrorEntity errorEntityNew = errorResponesitory.getErrorEntitiesByCode(errorDetailDTO.getErrorCode());
                ErrorGroupEntity errorGroupEntityNew = errorGroupResponsitory.getErrorGroupEntitiesByCode(
                    errorDetailDTO.getErrorGroupCode()
                );
                errorDetailEntity.setErrorId(errorEntityNew.getErrorId());
                errorDetailEntity.setErrorGroupId(errorGroupEntityNew.getErrorGroupId());

                errorDetailResponsitory.save(errorDetailEntity);
                return new CommonResponse().success("Cập nhật thành công");
            } catch (Exception e) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu lỗi");
            }
        }
    }

    @Transactional
    public CommonResponse deleteErrorDetail(String errorCode, String errorGroupCode) {
        ErrorEntity errorEntity = errorResponesitory.getErrorEntitiesByCode(errorCode);
        ErrorGroupEntity errorGroupEntity = errorGroupResponsitory.getErrorGroupEntitiesByCode(errorGroupCode);

        if (errorEntity == null || errorGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy lỗi hoặc nhóm lỗi");
        } else {
            ErrorDetailEntity errorDetailEntity = errorDetailResponsitory.getErrorDetailEntitiesByIdErrorG(
                errorEntity.getErrorId(),
                errorGroupEntity.getErrorGroupId()
            );
            if (errorDetailEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại");
            errorDetailResponsitory.delete(errorDetailEntity);
            return new CommonResponse().success("Xóa thành công");
        }
    }
}
