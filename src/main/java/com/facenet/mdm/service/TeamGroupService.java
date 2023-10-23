package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.EmployeeDTO;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.TeamGroupDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.EmployeeMapper;
import com.facenet.mdm.service.mapper.TeamGroupMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import liquibase.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TeamGroupService {

    @Autowired
    TeamGroupRepository teamGroupRepository;

    @Autowired
    KeyDictionaryRepository keyDictionaryRepository;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    KeyValueService keyValueService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    BusinessLogService businessLogService;

    public CommonResponse getAllTeamGroup(PageFilterInput<TeamGroupDTO> input) {
        List<TeamGroupDTO> teamGroupDTOList = new ArrayList<>();

        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != null && input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        TeamGroupDTO teamGroupDTO = new TeamGroupDTO();
        teamGroupDTO = input.getFilter();

        QTeamGroupEntity qTeamGroupEntity = QTeamGroupEntity.teamGroupEntity;

        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qTeamGroupEntity);

        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qTeamGroupEntity.isActive.eq(true));

        if (teamGroupDTO != null) {
            if (!StringUtil.isEmpty(teamGroupDTO.getTeamGroupCode())) {
                booleanBuilder.and(qTeamGroupEntity.teamGroupCode.containsIgnoreCase(teamGroupDTO.getTeamGroupCode()));
            }

            if (!StringUtil.isEmpty(teamGroupDTO.getTeamGroupName())) {
                booleanBuilder.and(qTeamGroupEntity.teamGroupName.containsIgnoreCase(teamGroupDTO.getTeamGroupName()));
            }

            if (teamGroupDTO.getTeamGroupQuota() != null) {
                try {
                    booleanBuilder.and(
                        qTeamGroupEntity.teamGroupQuota.like("%" + Integer.parseInt(teamGroupDTO.getTeamGroupQuota()) + "%")
                    );
                } catch (Exception exception) {}
            }

            if (teamGroupDTO.getNumberOfEmployee() != null) {
                booleanBuilder.and(qTeamGroupEntity.employeeEntitySet.size().like("%" + teamGroupDTO.getNumberOfEmployee() + "%"));
            }

            if (!StringUtil.isEmpty(teamGroupDTO.getTeamGroupNote())) {
                booleanBuilder.and(qTeamGroupEntity.teamGroupNote.containsIgnoreCase(teamGroupDTO.getTeamGroupNote()));
            }

            if (teamGroupDTO.getTeamGroupStatus() != null) {
                booleanBuilder.and(qTeamGroupEntity.teamGroupStatus.eq(teamGroupDTO.getTeamGroupStatus()));
            }
        }

        Map<String, ColumnPropertyEntity> columnPropertyEntityMap = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.TEAM_GROUP
        );
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        if (teamGroupDTO != null && !teamGroupDTO.getTeamGroupMap().isEmpty()) {
            Map<String, List<Integer>> teamGroupIdMap = new HashMap<>();
            teamGroupDTO
                .getTeamGroupMap()
                .forEach((keyName, value) -> {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));
                    if (!StringUtil.isEmpty(value)) {
                        dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(keyName));
                        if (columnPropertyEntityMap.get(keyName).getDataType() == Contants.DATE_VALUE) {
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                            String[] date = value.split(" ");

                            LocalDate startDate = Instant
                                .from(formatter.parse(date[0].trim()))
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                            LocalDate endDate = Instant.from(formatter.parse(date[1].trim())).atZone(ZoneId.systemDefault()).toLocalDate();
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.dateValue.between(startDate, endDate));
                        } else {
                            dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(value));
                        }
                        propertySubQuery.where(dynamicBooleanBuilder);
                        teamGroupIdMap.put(keyName, propertySubQuery.fetch());
                    }
                });
            for (String key : teamGroupIdMap.keySet()) {
                booleanBuilder.and(qTeamGroupEntity.teamGroupId.in(teamGroupIdMap.get(key)));
            }
        }

        query.where(booleanBuilder);

        if (!StringUtil.isEmpty(input.getCommon())) {
            BooleanBuilder booleanBuilderCommon = new BooleanBuilder();
            booleanBuilderCommon.or(qTeamGroupEntity.teamGroupCode.containsIgnoreCase(input.getCommon()));
            booleanBuilderCommon.or(qTeamGroupEntity.teamGroupName.containsIgnoreCase(input.getCommon()));
            try {
                booleanBuilderCommon.or(qTeamGroupEntity.teamGroupQuota.like("%" + Integer.parseInt(input.getCommon()) + "%"));
            } catch (Exception ex) {}
            try {
                booleanBuilderCommon.or(qTeamGroupEntity.employeeEntitySet.size().like("%" + Integer.parseInt(input.getCommon()) + "%"));
            } catch (Exception exception) {}
            booleanBuilderCommon.or(qTeamGroupEntity.teamGroupNote.containsIgnoreCase(input.getCommon()));
            try {
                booleanBuilderCommon.or(qTeamGroupEntity.teamGroupStatus.eq(Integer.parseInt(input.getCommon())));
            } catch (Exception exception) {}

            Map<String, List<Integer>> teamGroupIdCommon = new HashMap<>();
            columnPropertyEntityMap.forEach((s, columnPropertyEntity) -> {
                if (columnPropertyEntity.getIsFixed() == 0) {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(input.getCommon()));
                    propertySubQuery.where(dynamicBooleanBuilder);
                    teamGroupIdCommon.put(s, propertySubQuery.fetch());
                }
            });
            for (String key : teamGroupIdCommon.keySet()) {
                booleanBuilderCommon.or(qTeamGroupEntity.teamGroupId.in(teamGroupIdCommon.get(key)));
            }
            query.where(booleanBuilderCommon);
        }

        if (!StringUtil.isEmpty(input.getSortProperty())) {
            boolean checkSort = true;
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.TEAM_GROUP);
            for (ColumnPropertyEntity column : columns) {
                // Valid sort column name
                if (input.getSortProperty().equals(column.getKeyName())) {
                    checkSort = false;
                    if (column.getIsFixed() == 1 && !column.getKeyName().equals("numberOfEmployee")) {
                        // Default col
                        Path<Object> fieldPath = Expressions.path(Object.class, qTeamGroupEntity, input.getSortProperty());
                        query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
                    } else if (column.getKeyName().equals("numberOfEmployee")) {
                        query.orderBy(new OrderSpecifier(input.getSortOrder(), qTeamGroupEntity.employeeEntitySet.size()));
                    } else {
                        // Dynamic col
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qTeamGroupEntity.teamGroupId)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.TEAM_GROUP))
                                    .and(qKeyValueEntityV22.columnPropertyEntity.eq(column))
                            );
                        switch (column.getDataType()) {
                            case Contants.INT_VALUE:
                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.intValue));
                                break;
                            case Contants.FLOAT_VALUE:
                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.doubleValue));
                                break;
                            case Contants.STRING_VALUE:
                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.stringValue));
                                break;
                            case Contants.JSON_VALUE:
                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.jsonValue));
                                break;
                            case Contants.DATE_VALUE:
                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qKeyValueEntityV22.dateValue));
                                break;
                        }
                    }
                }
            }
            if (checkSort) {
                Path<Object> fieldPath = Expressions.path(Object.class, qTeamGroupEntity, input.getSortProperty());
                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
            }
        }

        List<TeamGroupEntity> teamGroupEntities = query.fetch();
        long count = query.fetchCount();
        Page<TeamGroupEntity> teamGroupEntityPage = new PageImpl<>(teamGroupEntities, pageable, count);

        List<KeyValueEntityV2> teamGroupProperties;
        if (pageable.isPaged()) {
            teamGroupProperties =
                keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    Contants.EntityType.TEAM_GROUP,
                    teamGroupEntityPage.getContent().stream().map(TeamGroupEntity::getTeamGroupId).collect(Collectors.toList())
                );
        } else {
            teamGroupProperties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.TEAM_GROUP);
        }

        Map<Integer, List<KeyValueEntityV2>> teamGroupPropertyMap = teamGroupProperties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        for (TeamGroupEntity teamGroupEntity : teamGroupEntityPage.getContent()) {
            teamGroupDTOList.add(
                TeamGroupMapper.entityToDTOMap(teamGroupEntity, teamGroupPropertyMap.get(teamGroupEntity.getTeamGroupId()))
            );
        }

        List<ColumnPropertyEntity> keyDictionaryDTOList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.TEAM_GROUP
        );
        return new PageResponse<List<TeamGroupDTO>>(teamGroupEntityPage.getTotalElements())
            .success()
            .data(teamGroupDTOList)
            .columns(keyDictionaryDTOList);
    }

    public List<String> getAutoCompleteTeamGroup(PageFilterInput<TeamGroupDTO> input) {
        List<TeamGroupDTO> teamGroupDTOList = (List<TeamGroupDTO>) getAllTeamGroup(input).getData();
        String common = input.getCommon();
        List<String> searchAutoComplete = new ArrayList<>();
        for (TeamGroupDTO teamGroupDTO : teamGroupDTOList) {
            if (
                teamGroupDTO.getTeamGroupCode().toLowerCase().contains(common.toLowerCase()) &&
                !searchAutoComplete.contains(teamGroupDTO.getTeamGroupCode())
            ) searchAutoComplete.add(teamGroupDTO.getTeamGroupCode());
            if (
                teamGroupDTO.getTeamGroupName().toLowerCase().contains(common.toLowerCase()) &&
                !searchAutoComplete.contains(teamGroupDTO.getTeamGroupName())
            ) searchAutoComplete.add(teamGroupDTO.getTeamGroupName());
            if (
                teamGroupDTO.getTeamGroupQuota() != null &&
                teamGroupDTO.getTeamGroupQuota().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(teamGroupDTO.getTeamGroupQuota().toString())
            ) searchAutoComplete.add(teamGroupDTO.getTeamGroupQuota().toString());
            if (
                teamGroupDTO.getNumberOfEmployee() != null &&
                teamGroupDTO.getNumberOfEmployee().toString().contains(common.toString()) &&
                !searchAutoComplete.contains(teamGroupDTO.getNumberOfEmployee().toString())
            ) searchAutoComplete.add(teamGroupDTO.getNumberOfEmployee().toString());
            if (
                !StringUtil.isEmpty(teamGroupDTO.getTeamGroupNote()) &&
                teamGroupDTO.getTeamGroupNote().toLowerCase().contains(common.toLowerCase()) &&
                !searchAutoComplete.contains(teamGroupDTO.getTeamGroupNote())
            ) searchAutoComplete.add(teamGroupDTO.getTeamGroupNote());
            if (teamGroupDTO.getTeamGroupMap() != null) {
                for (String key : teamGroupDTO.getTeamGroupMap().keySet()) {
                    if (
                        !StringUtil.isEmpty(teamGroupDTO.getTeamGroupMap().get(key)) &&
                        teamGroupDTO.getTeamGroupMap().get(key).toLowerCase().contains(common.toLowerCase()) &&
                        !searchAutoComplete.contains(teamGroupDTO.getTeamGroupMap().get(key))
                    ) {
                        searchAutoComplete.add(teamGroupDTO.getTeamGroupMap().get(key));
                    }
                }
            }
            if (searchAutoComplete.size() >= 10) break;
        }
        return searchAutoComplete;
    }

    @Transactional
    public CommonResponse createTeamGroup(TeamGroupDTO teamGroupDTO) {
        TeamGroupEntity teamGroupEntity = teamGroupRepository.getTeamGroupEntitieByCode(teamGroupDTO.getTeamGroupCode());
        if (teamGroupEntity != null) {
            throw new CustomException(HttpStatus.CONFLICT, "Đã tồn tại nhóm tổ có mã: " + teamGroupEntity.getTeamGroupCode());
        } else {
            try {
                teamGroupEntity = TeamGroupMapper.dtoToEntity(teamGroupDTO);
                teamGroupEntity.setActive(true);
                teamGroupRepository.save(teamGroupEntity);
                if (!teamGroupDTO.getTeamGroupMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntity(
                        teamGroupEntity.getTeamGroupId(),
                        teamGroupDTO.getTeamGroupMap(),
                        Contants.EntityType.TEAM_GROUP,
                        false
                    );
                }
            } catch (Exception ex) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
            }
        }
        businessLogService.insertInsertionLog(
            teamGroupEntity.getId(),
            Contants.EntityType.TEAM_GROUP,
            TeamGroupMapper.toLogDetail(teamGroupEntity, teamGroupDTO.getTeamGroupMap())
        );
        return new CommonResponse().success("Thêm mới thành công");
    }

    @Transactional
    public CommonResponse updateTeamGroup(String teamGroupCode, TeamGroupDTO teamGroupDTO) {
        TeamGroupEntity teamGroupEntity = teamGroupRepository.getTeamGroupEntitieByCode(teamGroupCode);
        if (teamGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại nhóm tổ");
        } else {
            try {
                TeamGroupEntity oldValue = new TeamGroupEntity(teamGroupEntity);
                teamGroupEntity.setTeamGroupCode(teamGroupDTO.getTeamGroupCode());
                teamGroupEntity.setTeamGroupName(teamGroupDTO.getTeamGroupName());
                if (teamGroupDTO.getTeamGroupQuota() == null) {
                    teamGroupEntity.setTeamGroupQuota(null);
                } else {
                    try {
                        teamGroupEntity.setTeamGroupQuota(Integer.parseInt(teamGroupDTO.getTeamGroupQuota()));
                    } catch (Exception exception) {
                        throw new CustomException(HttpStatus.BAD_REQUEST, "Loại dữ liệu không hợp lệ");
                    }
                }
                teamGroupEntity.setTeamGroupStatus(teamGroupDTO.getTeamGroupStatus());
                teamGroupEntity.setTeamGroupNote(teamGroupDTO.getTeamGroupNote());
                teamGroupEntity.setActive(true);
                TeamGroupEntity savedEntity = teamGroupRepository.save(teamGroupEntity);

                BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                    savedEntity.getId(),
                    Contants.EntityType.TEAM_GROUP,
                    TeamGroupMapper.toUpdateLogDetail(oldValue, teamGroupEntity)
                );

                if (!teamGroupDTO.getTeamGroupMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntityWithLog(
                        teamGroupEntity.getId(),
                        teamGroupDTO.getTeamGroupMap(),
                        Contants.EntityType.TEAM_GROUP,
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
    public CommonResponse deleteTeamGroup(String teamGroupCode) {
        TeamGroupEntity teamGroupEntity = teamGroupRepository.getTeamGroupEntitieByCode(teamGroupCode);
        if (teamGroupEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm tổ có mã: " + teamGroupCode);
        } else {
            List<EmployeeEntity> employeeEntities = employeeRepository.getEmployeeEntityByTeamGroupCode(teamGroupCode);
            for (EmployeeEntity employeeEntity : employeeEntities) {
                employeeEntity.setTeamGroup(null);
            }
            employeeRepository.saveAll(employeeEntities);
            teamGroupEntity.setActive(false);
            teamGroupRepository.save(teamGroupEntity);
            businessLogService.insertDeleteLog(
                teamGroupEntity.getId(),
                Contants.EntityType.TEAM_GROUP,
                TeamGroupMapper.toDeletionLogDetail(teamGroupEntity)
            );
            return new CommonResponse().success("Xóa thành công");
        }
    }

    @Transactional
    public CommonResponse importTeamGroupFromExcel(MultipartFile file) throws IOException {
        Map<TeamGroupDTO, List<EmployeeDTO>> teamGroupDTOListMap = xlsxExcelHandle.importTeamGroupFromExcel(file.getInputStream());
        for (TeamGroupDTO teamGroupDTO : teamGroupDTOListMap.keySet()) {
            createTeamGroup(teamGroupDTO);
            for (EmployeeDTO employeeDTO : teamGroupDTOListMap.get(teamGroupDTO)) {
                EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeDTO.getEmployeeCode());
                if (employeeEntity != null) {
                    EmployeeEntity oldValue = new EmployeeEntity(employeeEntity);
                    TeamGroupEntity teamGroupEntity = teamGroupRepository.getTeamGroupEntitieByCode(teamGroupDTO.getTeamGroupCode());
                    employeeEntity.setTeamGroup(teamGroupEntity);
                    EmployeeEntity savedEntity = employeeRepository.save(employeeEntity);

                    businessLogService.insertUpdateLog(
                        savedEntity.getId(),
                        Contants.EntityType.EMPLOYEE,
                        EmployeeMapper.toUpdateLogDetail(oldValue, employeeEntity)
                    );
                } else {
                    throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có mã: " + employeeDTO.getEmployeeCode());
                }
            }
        }
        return new CommonResponse().success();
    }
}
