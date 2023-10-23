package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.EmployeeMapper;
import com.facenet.mdm.service.model.EmployeeFilter;
import com.facenet.mdm.service.model.EmployeeInput;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import com.querydsl.core.BooleanBuilder;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import liquibase.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TeamGroupRepository teamGroupRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    KeyDictionaryRepository keyDictionaryRepository;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    BusinessLogService businessLogService;

    Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    public CommonResponse getAllEmployee(PageFilterInput<EmployeeDTO> employeeInput) {
        Pageable pageable = Pageable.unpaged();
        if (employeeInput.getPageSize() != 0) {
            pageable = PageRequest.of(employeeInput.getPageNumber(), employeeInput.getPageSize());
        }
        EmployeeDTO employeeFilter = employeeInput.getFilter();
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        QEmployeeEntity qEmployeeEntity = QEmployeeEntity.employeeEntity;
        JPAQuery query = new JPAQueryFactory(entityManager).selectFrom(qEmployeeEntity);
        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qEmployeeEntity.isActive.eq(true));

        if (!StringUtil.isEmpty(employeeFilter.getEmployeeCode())) {
            booleanBuilder.and(qEmployeeEntity.employeeCode.containsIgnoreCase(employeeFilter.getEmployeeCode()));
        }
        if (!StringUtil.isEmpty(employeeFilter.getEmployeeName())) {
            booleanBuilder.and(qEmployeeEntity.employeeName.containsIgnoreCase(employeeFilter.getEmployeeName()));
        }
        if (!StringUtil.isEmpty(employeeFilter.getTeamGroup())) {
            booleanBuilder.and(qEmployeeEntity.teamGroup.teamGroupName.containsIgnoreCase(employeeFilter.getTeamGroup()));
        }
        if (!StringUtil.isEmpty(employeeFilter.getEmployeePhone())) {
            booleanBuilder.and(qEmployeeEntity.employeePhone.containsIgnoreCase(employeeFilter.getEmployeePhone()));
        }
        if (!StringUtil.isEmpty(employeeFilter.getEmployeeEmail())) {
            booleanBuilder.and(qEmployeeEntity.employeeEmail.containsIgnoreCase(employeeFilter.getEmployeeEmail()));
        }
        if (!StringUtil.isEmpty(employeeFilter.getEmployeeNote())) {
            booleanBuilder.and(qEmployeeEntity.employeeNote.containsIgnoreCase(employeeFilter.getEmployeeNote()));
        }

        if (employeeFilter.getEmployeeStatus() != null) {
            booleanBuilder.and(qEmployeeEntity.employeeStatus.eq(employeeFilter.getEmployeeStatus()));
        }

        Map<String, ColumnPropertyEntity> columnPropertyEntityMap = columnPropertyRepository.getAllDynamicColumnByEntityTypeByMap(
            Contants.EntityType.EMPLOYEE
        );
        QKeyValueEntityV2 qKeyValueEntityV2 = QKeyValueEntityV2.keyValueEntityV2;

        if (!employeeFilter.getEmployeeMap().isEmpty()) {
            Map<String, List<Integer>> employeeIdMap = new HashMap<>();
            employeeFilter
                .getEmployeeMap()
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
                        employeeIdMap.put(keyName, propertySubQuery.fetch());
                    }
                });
            for (String key : employeeIdMap.keySet()) {
                booleanBuilder.and(qEmployeeEntity.employeeId.in(employeeIdMap.get(key)));
            }
        }

        query.where(booleanBuilder);

        if (!StringUtil.isEmpty(employeeInput.getCommon())) {
            BooleanBuilder booleanBuilderSearchGeneral = new BooleanBuilder();
            booleanBuilderSearchGeneral.or(qEmployeeEntity.employeeCode.containsIgnoreCase(employeeInput.getCommon()));
            booleanBuilderSearchGeneral.or(qEmployeeEntity.employeeName.containsIgnoreCase(employeeInput.getCommon()));
            booleanBuilderSearchGeneral.or(qEmployeeEntity.employeePhone.containsIgnoreCase(employeeInput.getCommon()));
            booleanBuilderSearchGeneral.or(qEmployeeEntity.employeeEmail.containsIgnoreCase(employeeInput.getCommon()));
            booleanBuilderSearchGeneral.or(qEmployeeEntity.employeeNote.containsIgnoreCase(employeeInput.getCommon()));
            QTeamGroupEntity qTeamGroupEntity = QTeamGroupEntity.teamGroupEntity;
            JPAQuery queryTeamGroup = new JPAQueryFactory(entityManager).select(qTeamGroupEntity.teamGroupId).from(qTeamGroupEntity);
            BooleanBuilder booleanBuilderTeamGroup = new BooleanBuilder();
            booleanBuilderTeamGroup.and(qTeamGroupEntity.isActive.eq(true));
            booleanBuilderTeamGroup.and(qTeamGroupEntity.teamGroupName.containsIgnoreCase(employeeInput.getCommon()));
            queryTeamGroup.where(booleanBuilderTeamGroup);
            List<Integer> teamGroupID = queryTeamGroup.fetch();
            booleanBuilderSearchGeneral.or(qEmployeeEntity.teamGroup.teamGroupId.in(teamGroupID));

            Map<String, List<Integer>> employeeIdCommon = new HashMap<>();
            columnPropertyEntityMap.forEach((s, columnPropertyEntity) -> {
                if (columnPropertyEntity.getIsFixed() == 0) {
                    JPAQuery<Integer> propertySubQuery = new JPAQueryFactory(entityManager)
                        .select(qKeyValueEntityV2.entityKey)
                        .from(qKeyValueEntityV2);
                    BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.isActive.eq(true));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.columnPropertyEntity.keyName.eq(s));
                    dynamicBooleanBuilder.and(qKeyValueEntityV2.commonValue.containsIgnoreCase(employeeInput.getCommon()));
                    propertySubQuery.where(dynamicBooleanBuilder);
                    employeeIdCommon.put(s, propertySubQuery.fetch());
                }
            });
            for (String key : employeeIdCommon.keySet()) {
                booleanBuilderSearchGeneral.or(qEmployeeEntity.employeeId.in(employeeIdCommon.get(key)));
            }

            query.where(booleanBuilderSearchGeneral);
        }

        if (!StringUtil.isEmpty(employeeInput.getSortProperty())) {
            boolean checkSort = true;
            List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.EMPLOYEE);
            for (ColumnPropertyEntity column : columns) {
                // Valid sort column name
                if (employeeInput.getSortProperty().equals(column.getKeyName())) {
                    checkSort = false;
                    if (column.getIsFixed() == 1) {
                        // Default col
                        Path<Object> fieldPath = Expressions.path(Object.class, qEmployeeEntity, employeeInput.getSortProperty());
                        query.orderBy(new OrderSpecifier(employeeInput.getSortOrder(), fieldPath));
                    } else {
                        // Dynamic col
                        QKeyValueEntityV2 qKeyValueEntityV22 = new QKeyValueEntityV2("keyValue2");
                        query
                            .leftJoin(qKeyValueEntityV22)
                            .on(
                                qKeyValueEntityV22.entityKey
                                    .eq(qEmployeeEntity.employeeId)
                                    .and(qKeyValueEntityV22.entityType.eq(Contants.EntityType.EMPLOYEE))
                                    .and(qKeyValueEntityV22.columnPropertyEntity.eq(column))
                            );
                        switch (column.getDataType()) {
                            case Contants.INT_VALUE:
                                query.orderBy(new OrderSpecifier<>(employeeInput.getSortOrder(), qKeyValueEntityV22.intValue));
                                break;
                            case Contants.FLOAT_VALUE:
                                query.orderBy(new OrderSpecifier<>(employeeInput.getSortOrder(), qKeyValueEntityV22.doubleValue));
                                break;
                            case Contants.STRING_VALUE:
                                query.orderBy(new OrderSpecifier<>(employeeInput.getSortOrder(), qKeyValueEntityV22.stringValue));
                                break;
                            case Contants.JSON_VALUE:
                                query.orderBy(new OrderSpecifier<>(employeeInput.getSortOrder(), qKeyValueEntityV22.jsonValue));
                                break;
                            case Contants.DATE_VALUE:
                                query.orderBy(new OrderSpecifier<>(employeeInput.getSortOrder(), qKeyValueEntityV22.dateValue));
                                break;
                        }
                    }
                }
            }
            if (checkSort) {
                Path<Object> fieldPath = Expressions.path(Object.class, qEmployeeEntity, employeeInput.getSortProperty());
                query.orderBy(new OrderSpecifier(employeeInput.getSortOrder(), fieldPath));
            }
        }

        List<EmployeeEntity> employeeEntities = query.fetch();
        long count = query.fetchCount();

        Page<EmployeeEntity> employeeEntityPage = new PageImpl<>(employeeEntities, pageable, count);

        List<KeyValueEntityV2> employeeProperties;
        if (pageable.isPaged()) {
            employeeProperties =
                keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    Contants.EntityType.EMPLOYEE,
                    employeeEntityPage.getContent().stream().map(EmployeeEntity::getEmployeeId).collect(Collectors.toList())
                );
        } else {
            employeeProperties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.EMPLOYEE);
        }

        Map<Integer, List<KeyValueEntityV2>> employeePropertyMap = employeeProperties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        for (EmployeeEntity employeeEntity : employeeEntityPage.getContent()) {
            employeeDTOS.add(EmployeeMapper.entityToDTOMap(employeeEntity, employeePropertyMap.get(employeeEntity.getEmployeeId())));
        }

        List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.EMPLOYEE
        );

        return new PageResponse<List<EmployeeDTO>>(employeeEntityPage.getTotalElements())
            .success()
            .data(employeeDTOS)
            .columns(keyDictionaryDTOS);
    }

    public List<String> getAutoCompleteEmployee(PageFilterInput<EmployeeDTO> input) {
        List<EmployeeDTO> employeeDTOList = (List<EmployeeDTO>) getAllEmployee(input).getData();
        String common = input.getCommon();
        List<String> searchCommon = new ArrayList<>();
        for (EmployeeDTO employeeDTO : employeeDTOList) {
            if (
                employeeDTO.getEmployeeCode().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(employeeDTO.getEmployeeCode())
            ) searchCommon.add(employeeDTO.getEmployeeCode());
            if (
                employeeDTO.getEmployeeName().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(employeeDTO.getEmployeeName())
            ) searchCommon.add(employeeDTO.getEmployeeName());
            if (
                !StringUtil.isEmpty(employeeDTO.getTeamGroup()) &&
                employeeDTO.getTeamGroup().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(employeeDTO.getTeamGroup())
            ) searchCommon.add(employeeDTO.getTeamGroup());
            if (
                !StringUtil.isEmpty(employeeDTO.getEmployeePhone()) &&
                employeeDTO.getEmployeePhone().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(employeeDTO.getEmployeePhone())
            ) searchCommon.add(employeeDTO.getEmployeePhone());
            if (
                !StringUtil.isEmpty(employeeDTO.getEmployeeEmail()) &&
                employeeDTO.getEmployeeEmail().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(employeeDTO.getEmployeeEmail())
            ) searchCommon.add(employeeDTO.getEmployeeEmail());
            if (
                !StringUtil.isEmpty(employeeDTO.getEmployeeNote()) &&
                employeeDTO.getEmployeeNote().toLowerCase().contains(common.toLowerCase()) &&
                !searchCommon.contains(employeeDTO.getEmployeeNote())
            ) searchCommon.add(employeeDTO.getEmployeeNote());
            if (employeeDTO.getEmployeeMap() != null) {
                for (String key : employeeDTO.getEmployeeMap().keySet()) {
                    if (
                        !StringUtil.isEmpty(employeeDTO.getEmployeeMap().get(key)) &&
                        employeeDTO.getEmployeeMap().get(key).toLowerCase().contains(common.toLowerCase()) &&
                        !searchCommon.contains(employeeDTO.getEmployeeMap().get(key))
                    ) {
                        searchCommon.add(employeeDTO.getEmployeeMap().get(key));
                    }
                }
            }
        }
        return searchCommon;
    }

    public CommonResponse getEmployeeByTeamGroupCode(String teamGroupCode) {
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        QEmployeeEntity qEmployeeEntity = QEmployeeEntity.employeeEntity;
        Pageable pageable = Pageable.unpaged();

        JPAQuery query = new JPAQueryFactory(entityManager)
            .selectFrom(qEmployeeEntity)
            .where(qEmployeeEntity.isActive.eq(true))
            .where(qEmployeeEntity.teamGroup.teamGroupCode.eq(teamGroupCode));

        List<EmployeeEntity> employeeEntities = query.fetch();
        long count = query.fetchCount();

        Page<EmployeeEntity> employeeEntityPage = new PageImpl<>(employeeEntities, pageable, count);

        List<KeyValueEntityV2> employeeProperties;
        if (pageable.isPaged()) {
            employeeProperties =
                keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    Contants.EntityType.EMPLOYEE,
                    employeeEntityPage.getContent().stream().map(EmployeeEntity::getEmployeeId).collect(Collectors.toList())
                );
        } else {
            employeeProperties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.EMPLOYEE);
        }

        Map<Integer, List<KeyValueEntityV2>> employeePropertyMap = employeeProperties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        for (EmployeeEntity employeeEntity : employeeEntityPage.getContent()) {
            employeeDTOS.add(EmployeeMapper.entityToDTOMap(employeeEntity, employeePropertyMap.get(employeeEntity.getEmployeeId())));
        }

        List<ColumnPropertyEntity> keyDictionaryDTOS = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(
            Contants.EntityType.EMPLOYEE
        );

        return new PageResponse<List<EmployeeDTO>>(employeeEntityPage.getTotalElements())
            .success()
            .data(employeeDTOS)
            .columns(keyDictionaryDTOS);
    }

    @Transactional
    public CommonResponse createEmployee(EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeDTO.getEmployeeCode());
        if (employeeEntity != null) {
            throw new CustomException(HttpStatus.CONFLICT, "Đã tồn tại nhân viên có mã: " + employeeDTO.getEmployeeCode());
        } else {
            employeeEntity = EmployeeMapper.dtoToEntity(employeeDTO);
            TeamGroupEntity teamGroupEntity = teamGroupRepository.getTeamGroupEntitieByCode(employeeDTO.getTeamGroup());
            employeeEntity.setTeamGroup(teamGroupEntity);
            employeeEntity.setActive(true);
            employeeRepository.save(employeeEntity);
            if (!employeeDTO.getEmployeeMap().isEmpty()) {
                keyValueService.createUpdateKeyValueOfEntity(
                    employeeEntity.getId(),
                    employeeDTO.getEmployeeMap(),
                    Contants.EntityType.EMPLOYEE,
                    false
                );
            }
            businessLogService.insertInsertionLog(
                employeeEntity.getId(),
                Contants.EntityType.EMPLOYEE,
                EmployeeMapper.toLogDetail(employeeEntity, employeeDTO.getEmployeeMap())
            );
            return new CommonResponse().success("Thêm mới nhân viên thành công");
        }
    }

    @Transactional
    public CommonResponse updateEmployee(String employeeCode, EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeCode);
        if (employeeEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại nhân viên có mã: " + employeeCode);
        } else {
            EmployeeEntity oldValue = new EmployeeEntity(employeeEntity);
            employeeEntity.setEmployeeName(employeeDTO.getEmployeeName());
            employeeEntity.setEmployeePhone(employeeDTO.getEmployeePhone());
            employeeEntity.setEmployeeEmail(employeeDTO.getEmployeeEmail());
            employeeEntity.setEmployeeStatus(employeeDTO.getEmployeeStatus());
            employeeEntity.setEmployeeNote(employeeDTO.getEmployeeNote());
            TeamGroupEntity teamGroupEntity = teamGroupRepository.getTeamGroupEntitieByCode(employeeDTO.getTeamGroup());
            employeeEntity.setTeamGroup(teamGroupEntity);
            employeeEntity.setActive(true);
            EmployeeEntity savedEntity = employeeRepository.save(employeeEntity);

            BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                savedEntity.getId(),
                Contants.EntityType.EMPLOYEE,
                EmployeeMapper.toUpdateLogDetail(oldValue, employeeEntity)
            );

            if (!employeeDTO.getEmployeeMap().isEmpty()) {
                keyValueService.createUpdateKeyValueOfEntityWithLog(
                    employeeEntity.getId(),
                    employeeDTO.getEmployeeMap(),
                    Contants.EntityType.EMPLOYEE,
                    true,
                    logEntity
                );
            }
            return new CommonResponse().success("Cập nhật nhân viên thành công");
        }
    }

    @Transactional
    public CommonResponse createOrUpdate(List<EmployeeDTO> employeeDTOList) {
        List<String> employeeCodeList = new ArrayList<>();
        List<String> employeeCodeDuplicate = new ArrayList<>();

        for (EmployeeDTO employeeDTO : employeeDTOList) {
            if (
                employeeCodeList.contains(employeeDTO.getEmployeeCode()) && !employeeCodeDuplicate.contains(employeeDTO.getEmployeeCode())
            ) {
                employeeCodeDuplicate.add(employeeDTO.getEmployeeCode());
            }
            employeeCodeList.add(employeeDTO.getEmployeeCode());

            TeamGroupEntity teamGroupEntity = teamGroupRepository.getTeamGroupEntitieByCode(employeeDTO.getTeamGroup());
            if (teamGroupEntity == null) {
                throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm tổ có mã: " + employeeDTO.getTeamGroup());
            }
            EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeDTO.getEmployeeCode());
            if (employeeEntity != null) {
                EmployeeEntity oldValue = new EmployeeEntity(employeeEntity);
                employeeEntity.setEmployeeCode(employeeDTO.getEmployeeCode());
                employeeEntity.setEmployeeName(employeeDTO.getEmployeeName());
                employeeEntity.setTeamGroup(teamGroupEntity);
                employeeEntity.setEmployeePhone(employeeDTO.getEmployeePhone());
                employeeEntity.setEmployeeEmail(employeeDTO.getEmployeeEmail());
                employeeEntity.setEmployeeNote(employeeDTO.getEmployeeNote());
                employeeEntity.setEmployeeStatus(employeeDTO.getEmployeeStatus());
                employeeEntity.setActive(true);
                EmployeeEntity savedEntity = employeeRepository.save(employeeEntity);

                BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                    savedEntity.getId(),
                    Contants.EntityType.EMPLOYEE,
                    EmployeeMapper.toUpdateLogDetail(oldValue, employeeEntity)
                );

                if (!employeeDTO.getEmployeeMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntityWithLog(
                        employeeEntity.getId(),
                        employeeDTO.getEmployeeMap(),
                        Contants.EntityType.EMPLOYEE,
                        true,
                        logEntity
                    );
                }
            } else {
                employeeEntity = EmployeeMapper.dtoToEntity(employeeDTO);
                employeeEntity.setTeamGroup(teamGroupEntity);
                employeeEntity.setActive(true);
                employeeRepository.save(employeeEntity);
                if (!employeeDTO.getEmployeeMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntity(
                        employeeEntity.getId(),
                        employeeDTO.getEmployeeMap(),
                        Contants.EntityType.EMPLOYEE,
                        false
                    );
                }
            }
        }
        if (employeeCodeDuplicate != null && employeeCodeDuplicate.size() > 0) {
            String employeeCode = "";
            for (int i = 0; i < employeeCodeDuplicate.size() - 1; i++) {
                employeeCode = employeeCode + employeeCodeDuplicate.get(i) + ", ";
            }
            employeeCode = employeeCode + employeeCodeDuplicate.get(employeeCodeDuplicate.size() - 1) + ".";
            throw new CustomException(HttpStatus.CONFLICT, "Có các mã nhân viên đang trùng nhau là: " + employeeCode);
        }

        return new CommonResponse().success("Thành công");
    }

    @Transactional
    public CommonResponse deleteEmployee(String employeeCode) {
        EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeCode);
        if (employeeEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại nhân viên có mã: " + employeeCode);
        } else {
            employeeEntity.setTeamGroup(null);
            employeeEntity.setActive(false);
            employeeRepository.save(employeeEntity);
            businessLogService.insertDeleteLog(
                employeeEntity.getId(),
                Contants.EntityType.EMPLOYEE,
                EmployeeMapper.toDeletionLogDetail(employeeEntity)
            );
            return new CommonResponse().success();
        }
    }

    @Transactional
    public CommonResponse deleteEmployeeTeamGroup(String employeeCode) {
        EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeCode);
        if (employeeEntity == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại nhân viên có mã: " + employeeCode);
        } else {
            employeeEntity.setTeamGroup(null);
            employeeRepository.save(employeeEntity);
            return new CommonResponse().success();
        }
    }

    @Transactional
    public CommonResponse importEmployeeFromExcel(MultipartFile file) throws IOException {
        List<EmployeeDTO> employeeDTOList = xlsxExcelHandle.importEmployeeFromExcel(file.getInputStream());
        TeamGroupEntity teamGroupEntity = new TeamGroupEntity();
        for (EmployeeDTO employeeDTO : employeeDTOList) {
            if (!StringUtil.isEmpty(employeeDTO.getTeamGroup())) {
                teamGroupEntity = teamGroupRepository.getTeamGroupEntityByName(employeeDTO.getTeamGroup());
                if (teamGroupEntity == null) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "Không tồn tại nhóm tổ: " + employeeDTO.getTeamGroup());
                }
            }
            EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeDTO.getEmployeeCode());
            if (employeeEntity == null) {
                employeeEntity = EmployeeMapper.dtoToEntity(employeeDTO);
                if (teamGroupEntity.getId() != null) {
                    employeeEntity.setTeamGroup(teamGroupEntity);
                }
                employeeEntity.setActive(true);
                employeeRepository.save(employeeEntity);
                if (!employeeDTO.getEmployeeMap().isEmpty()) {
                    keyValueService.createUpdateKeyValueOfEntity(
                        employeeEntity.getId(),
                        employeeDTO.getEmployeeMap(),
                        Contants.EntityType.EMPLOYEE,
                        false
                    );
                }
                businessLogService.insertInsertionLog(
                    employeeEntity.getId(),
                    Contants.EntityType.EMPLOYEE,
                    EmployeeMapper.toLogDetail(employeeEntity, employeeDTO.getEmployeeMap())
                );
            } else {
                throw new CustomException(HttpStatus.CONFLICT, "Đã tồn tại nhân viên có mã: " + employeeDTO.getEmployeeCode());
            }
        }

        return new CommonResponse().success();
    }
}
