package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.CoittRepository;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.KeyValueV2Repository;
import com.facenet.mdm.repository.MerchandiseGroupRepository;
import com.facenet.mdm.repository.custom.MerchandiseGroupCustomRepository;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.MerchandiseGroupMapper;
import com.facenet.mdm.service.mapper.TeamGroupMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MerchandiseGroupService {

    @Autowired
    MerchandiseGroupCustomRepository merchandiseGroupCustomRepository;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    MerchandiseGroupRepository merchandiseGroupRepository;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    CoittService coittService;

    @Autowired
    CoittRepository coittRepository;

    @Autowired
    BusinessLogService businessLogService;

    public PageResponse<List<MerchandiseGroupDTO>> getAllMerchandiseGroup(PageFilterInput<MerchandiseGroupDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }

        Page<MerchandiseGroupEntity> merchandiseGroupEntities = merchandiseGroupCustomRepository.getAllMerchandiseGroup(input, pageable);

        List<KeyValueEntityV2> properties;
        if (pageable.isPaged()) properties =
            keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.MERCHANDISE_GROUP,
                merchandiseGroupEntities.stream().map(MerchandiseGroupEntity::getId).collect(Collectors.toList())
            ); else properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.MERCHANDISE_GROUP);

        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));

        List<MerchandiseGroupDTO> resultList = new ArrayList<>();
        for (MerchandiseGroupEntity merchandiseGroupEntity : merchandiseGroupEntities.getContent()) {
            resultList.add(MerchandiseGroupMapper.entityToDTOMap(merchandiseGroupEntity, propertyMap.get(merchandiseGroupEntity.getId())));
        }

        List<ColumnPropertyEntity> keyDTOList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.MERCHANDISE_GROUP
        );

        return new PageResponse<List<MerchandiseGroupDTO>>()
            .success()
            .data(resultList)
            .dataCount(merchandiseGroupEntities.getTotalElements())
            .columns(keyDTOList);
    }

    public List<String> getAutoCompleteMerchandiseGroup(PageFilterInput<MerchandiseGroupDTO> input) {
        List<MerchandiseGroupDTO> merchandiseGroupDTOList = getAllMerchandiseGroup(input).getData();
        String common = input.getCommon();
        List<String> searchAutoComplete = new ArrayList<>();
        for (MerchandiseGroupDTO merchandiseGroupDTO : merchandiseGroupDTOList) {
            if (
                merchandiseGroupDTO.getMerchandiseGroupCode().toLowerCase().contains(common.toLowerCase()) &&
                !searchAutoComplete.contains(merchandiseGroupDTO.getMerchandiseGroupCode())
            ) searchAutoComplete.add(merchandiseGroupDTO.getMerchandiseGroupCode());
            if (
                merchandiseGroupDTO.getMerchandiseGroupName().toLowerCase().contains(common.toLowerCase()) &&
                !searchAutoComplete.contains(merchandiseGroupDTO.getMerchandiseGroupName())
            ) searchAutoComplete.add(merchandiseGroupDTO.getMerchandiseGroupName());
            if (
                merchandiseGroupDTO.getMerchandiseGroupNote() != null &&
                merchandiseGroupDTO.getMerchandiseGroupNote().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(merchandiseGroupDTO.getMerchandiseGroupNote().toString())
            ) searchAutoComplete.add(merchandiseGroupDTO.getMerchandiseGroupNote().toString());
            if (
                merchandiseGroupDTO.getMerchandiseGroupDescription() != null &&
                merchandiseGroupDTO.getMerchandiseGroupDescription().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(merchandiseGroupDTO.getMerchandiseGroupDescription().toString())
            ) searchAutoComplete.add(merchandiseGroupDTO.getMerchandiseGroupDescription().toString());

            if (merchandiseGroupDTO.getPropertiesMap() != null) {
                for (String key : merchandiseGroupDTO.getPropertiesMap().keySet()) {
                    if (
                        !StringUtil.isEmpty(merchandiseGroupDTO.getPropertiesMap().get(key)) &&
                        merchandiseGroupDTO.getPropertiesMap().get(key).toLowerCase().contains(common.toLowerCase()) &&
                        !searchAutoComplete.contains(merchandiseGroupDTO.getPropertiesMap().get(key))
                    ) {
                        searchAutoComplete.add(merchandiseGroupDTO.getPropertiesMap().get(key));
                    }
                }
            }
            if (searchAutoComplete.size() >= 10) break;
        }
        return searchAutoComplete;
    }

    @Transactional
    public CommonResponse createMerchandiseGroup(MerchandiseGroupDTO merchandiseGroupDTO) {
        MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(
            merchandiseGroupDTO.getMerchandiseGroupCode()
        );
        if (merchandiseGroupEntity != null) {
            throw new CustomException(
                HttpStatus.CONFLICT,
                "Đã tồn tại nhóm hàng hoá có mã: " + merchandiseGroupEntity.getMerchandiseGroupCode()
            );
        } else {
            try {
                merchandiseGroupEntity = MerchandiseGroupMapper.convertToEntity(merchandiseGroupDTO);
                merchandiseGroupEntity.setActive(true);
                merchandiseGroupRepository.save(merchandiseGroupEntity);

                if (!merchandiseGroupDTO.getPropertiesMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntity(
                        merchandiseGroupEntity.getId(),
                        merchandiseGroupDTO.getPropertiesMap(),
                        Contants.EntityType.MERCHANDISE_GROUP,
                        false
                    );
                }
            } catch (Exception ex) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
            }
        }
        businessLogService.insertInsertionLog(
            merchandiseGroupEntity.getId(),
            Contants.EntityType.MERCHANDISE_GROUP,
            MerchandiseGroupMapper.toLogDetail(merchandiseGroupEntity, merchandiseGroupDTO.getPropertiesMap())
        );
        return new CommonResponse().success("Thêm mới thành công");
    }

    @Transactional
    public CommonResponse updateMerchandiseGroup(String merchandiseGroupCode, MerchandiseGroupDTO merchandiseGroupDTO) {
        MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(merchandiseGroupCode);
        if (merchandiseGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại nhóm hàng hóa");
        } else {
            try {
                MerchandiseGroupEntity oldValue = new MerchandiseGroupEntity(merchandiseGroupEntity);
                merchandiseGroupEntity.setMerchandiseGroupCode(merchandiseGroupDTO.getMerchandiseGroupCode());
                merchandiseGroupEntity.setMerchandiseGroupName(merchandiseGroupDTO.getMerchandiseGroupName());
                merchandiseGroupEntity.setMerchandiseGroupDescription(merchandiseGroupDTO.getMerchandiseGroupDescription());
                merchandiseGroupEntity.setMerchandiseGroupNote(merchandiseGroupDTO.getMerchandiseGroupNote());
                merchandiseGroupEntity.setMerchandiseGroupStatus(merchandiseGroupDTO.getMerchandiseGroupStatus());
                merchandiseGroupEntity.setActive(true);
                MerchandiseGroupEntity savedEntity = merchandiseGroupRepository.save(merchandiseGroupEntity);

                BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                    savedEntity.getId(),
                    Contants.EntityType.MERCHANDISE_GROUP,
                    MerchandiseGroupMapper.toUpdateLogDetail(oldValue, merchandiseGroupEntity)
                );

                if (!merchandiseGroupDTO.getPropertiesMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntityWithLog(
                        merchandiseGroupEntity.getId(),
                        merchandiseGroupDTO.getPropertiesMap(),
                        Contants.EntityType.MERCHANDISE_GROUP,
                        true,
                        logEntity
                    );
                }
            } catch (Exception exception) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
            }
        }
        return new CommonResponse().success("Cập nhật thành công");
    }

    @Transactional
    public CommonResponse deleteMerchandiseGroup(String merchandiseGroupCode) {
        MerchandiseGroupEntity merchandiseGroupEntity = merchandiseGroupRepository.getMerchandiseGroupEntitiesByCode(merchandiseGroupCode);
        if (merchandiseGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm hàng hóa có mã: " + merchandiseGroupCode);
        } else {
            merchandiseGroupEntity.setActive(false);
            merchandiseGroupRepository.save(merchandiseGroupEntity);
            businessLogService.insertDeleteLog(
                merchandiseGroupEntity.getId(),
                Contants.EntityType.MERCHANDISE_GROUP,
                MerchandiseGroupMapper.toDeletionLogDetail(merchandiseGroupEntity)
            );
            return new CommonResponse().success("Xóa thành công");
        }
    }

    @Transactional
    public CommonResponse importMerchandiseGroupFromExcel(MultipartFile file) throws IOException {
        Map<MerchandiseGroupDTO, List<CoittDTO>> merchandiseGroupDTOListMap = xlsxExcelHandle.importMerchandiseGroupFromExcel(
            file.getInputStream()
        );

        for (MerchandiseGroupDTO merchandiseGroupDTO : merchandiseGroupDTOListMap.keySet()) {
            createMerchandiseGroup(merchandiseGroupDTO);
            if (!CollectionUtils.isEmpty(merchandiseGroupDTOListMap.get(merchandiseGroupDTO))) {
                coittService.updateListCoitt(merchandiseGroupDTOListMap.get(merchandiseGroupDTO));
            }
        }
        return new CommonResponse().success("Thành công");
    }

    public List<MerchandiseGroupDTO> exportMerchandiseGroup() {
        List<MerchandiseGroupEntity> merchandiseGroupEntities = merchandiseGroupRepository.getAllMerchandiseGroup();

        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.MERCHANDISE_GROUP);

        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(keyValueEntityV2 -> keyValueEntityV2.getEntityKey()));

        List<MerchandiseGroupDTO> resultList = new ArrayList<>();

        for (MerchandiseGroupEntity merchandiseGroupEntity : merchandiseGroupEntities) {
            MerchandiseGroupDTO merchandiseGroupDTO = MerchandiseGroupMapper.entityToDTOMap(
                merchandiseGroupEntity,
                propertyMap.get(merchandiseGroupEntity.getId())
            );
            merchandiseGroupDTO.setCoittDTOList(
                coittRepository.getCoittDTOByMerchadiseGroup(merchandiseGroupEntity.getMerchandiseGroupCode())
            );
            resultList.add(merchandiseGroupDTO);
        }

        return resultList;
    }
}
