package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.JobEntityAPSMapper;
import com.facenet.mdm.service.mapper.JobEntityMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

    private static final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobEntityMapper jobEntityMapper;

    @Autowired
    ProductionStageRepository productionStageRepository;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    com.facenet.mdm.service.mapper.DTOMapper DTOMapper;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    BusinessLogService businessLogService;

    @Autowired
    JobEntityAPSMapper jobEntityAPSMapper;

    public void getListJobCod(List<String> listJobCode, JobEntity jobEntity) {
        if (jobEntity.getProductionStageCode() == null) {
            listJobCode.add(jobEntity.getJobCode());
        } else {
            JobEntity jobEntityStage = jobRepository.findByJobCodeIgnoreCaseAndIsActive(jobEntity.getProductionStageCode(), true);
            getListJobCod(listJobCode, jobEntityStage);
        }
    }

    public void getJob(JobDTO jobDTO, Map<Integer, List<KeyValueEntityV2>> propertyMap) {
        List<JobEntity> jobEntities = jobRepository.getJobEntitiesByProductionStageCode(jobDTO.getJobCode());
        if (jobEntities == null || jobEntities.size() <= 0) {
            return;
        }
        int level = jobDTO.getLevel();
        List<JobDTO> jobDTOS = new ArrayList<>();
        for (JobEntity jobEntity : jobEntities) {
            JobDTO jobInJobDTO = (DTOMapper.toJobDTO(jobEntity, propertyMap.get(jobEntity.getId())));
            jobInJobDTO.setLevel(level + 1);
            jobDTOS.add(jobInJobDTO);
            getJob(jobInJobDTO, propertyMap);
        }
        jobDTO.setJobDTOList(jobDTOS);
    }

    public void saveOrUpdateListJob(JobDTO jobDTO) {
        JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(jobDTO.getJobCode(), true);
        if (jobEntity != null) {
            jobEntity.setJobCode(jobDTO.getJobCode());
            jobEntity.setProductionStageCode(jobDTO.getProductionStageCode());
            jobEntity.setJobName(jobDTO.getJobName());
            jobEntity.setStatus(jobDTO.getStatus());
        } else {
            jobEntity = new JobEntity();
            jobEntity.setJobCode(jobDTO.getJobCode());
            jobEntity.setProductionStageCode(jobDTO.getProductionStageCode());
            jobEntity.setJobName(jobDTO.getJobName());
            jobEntity.setStatus(jobDTO.getStatus());
        }

        jobRepository.save(jobEntity);
        businessLogService.insertInsertionLog(
            jobEntity.getId(),
            Contants.EntityType.JOB,
            DTOMapper.toLogDetail(jobEntity, jobDTO.getJobMap())
        );
        if (!jobDTO.getJobMap().isEmpty()) {
            List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                jobEntity.getId(),
                jobDTO.getJobMap(),
                Contants.EntityType.JOB
            );
            keyValueV2Repository.saveAll(keyValueEntities);
        }
        businessLogService.insertInsertionLog(
            jobEntity.getId(),
            Contants.EntityType.PRODUCTIONSTAGE,
            DTOMapper.toLogDetail(jobEntity, jobDTO.getJobMap())
        );
        if (jobDTO.getJobDTOList() != null) {
            for (JobDTO jobDTOGetJob : jobDTO.getJobDTOList()) {
                saveOrUpdateListJob(jobDTOGetJob);
            }
        }
    }

    public CommonResponse getListJob(PageFilterInput<JobDTO> input) {
        List<String> jobCodes = new ArrayList<>();
        List<JobEntity> getListJobCode = jobRepository.getListJobCode(input);
        for (JobEntity jobEntity : getListJobCode) {
            getListJobCod(jobCodes, jobEntity);
        }
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        Page<JobEntity> jobs = jobRepository.getAllJob(input, pageable, jobCodes);
        List<JobEntity> list = jobs.getContent();
        Map<Integer, JobEntity> map = list.stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));
        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.PRODUCTIONSTAGE,
            map.keySet()
        );
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        Map<Integer, JobEntity> mapJob = getListJobCode.stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));
        List<KeyValueEntityV2> propertiesJob = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.JOB,
            mapJob.keySet()
        );
        Map<Integer, List<KeyValueEntityV2>> propertyMapJob = propertiesJob
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        List<ProductionStageDTO> resultList = new ArrayList<>();

        List<ColumnPropertyEntity> keyDTOList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(Contants.EntityType.JOB);
        for (JobEntity jobEntity : jobs.getContent()) {
            if (StringUtils.isEmpty(jobEntity.getProductionStageCode())) {
                JobDTO jobDTO = DTOMapper.toJobDTO(jobEntity, propertyMap.get(jobEntity.getId()));
                jobDTO.setLevel(1);
                getJob(jobDTO, propertyMapJob);
                ProductionStageDTO productionStageDTO = DTOMapper.jobToProductionStage(jobDTO);
                resultList.add(productionStageDTO);
            }
        }

        return new PageResponse<List<ProductionStageDTO>>()
            .success()
            .data(resultList)
            .dataCount(jobs.getTotalElements())
            .columns(keyDTOList);
    }

    public CommonResponse createOrUpdateJob(List<JobDTO> dtoList) {
        List<JobEntity> jobEntityList = new ArrayList<>();
        List<KeyValueEntityV2> keyValueEntityV2List = new ArrayList<>();
        for (JobDTO dto : dtoList) {
            if (dto.getId() != null) {
                JobEntity jobEntity = jobRepository.findById(dto.getId()).orElse(null);
                if (jobEntity != null) {
                    if (!dto.getProductionStageCode().equalsIgnoreCase(jobEntity.getProductionStageCode())) throw new CustomException(
                        HttpStatus.CONFLICT,
                        "must.not.change.stage.code"
                    );
                    if (!dto.getJobMap().isEmpty()) {
                        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                            jobEntity.getId(),
                            dto.getJobMap(),
                            Contants.EntityType.JOB
                        );
                    }
                } else throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
            } else {
                JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(dto.getJobCode(), true);
                if (jobEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", jobEntity.getJobCode());
                if (!productionStageRepository.existsAllByProductionStageCodeAndIsActive(dto.getProductionStageCode(), true)) {
                    throw new CustomException(HttpStatus.NOT_FOUND, "stage.notfound", dto.getProductionStageCode());
                }
                if (dto.getProductionStageCode().isEmpty() || dto.getProductionStageCode() == null) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                }
                jobEntity = jobEntityMapper.toEntity(dto);
                Map<String, JobEntity> jobEntityMap = jobEntityList
                    .stream()
                    .collect(Collectors.toMap(JobEntity::getJobCode, Function.identity()));
                if (jobEntityMap.keySet().contains(jobEntity.getJobCode())) {
                    throw new CustomException(HttpStatus.CONFLICT, "must.not.add.duplicate.codes", jobEntity.getJobCode());
                }
                if (!dto.getJobMap().isEmpty()) {
                    List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                        jobEntity.getId(),
                        dto.getJobMap(),
                        Contants.EntityType.JOB
                    );
                }
            }
        }
        for (JobDTO dto : dtoList) {
            if (dto.getId() != null) {
                JobEntity jobEntity = jobRepository.findById(dto.getId()).orElse(null);
                if (jobEntity != null) {
                    if (!dto.getProductionStageCode().equalsIgnoreCase(jobEntity.getProductionStageCode())) throw new CustomException(
                        HttpStatus.CONFLICT,
                        "must.not.change.stage.code"
                    );
                    JobEntity oldValue = new JobEntity(jobEntity);
                    jobEntity.setJobName(dto.getJobName());
                    jobEntity.setStatus(dto.getStatus());
                    JobEntity savedEntity = jobRepository.save(jobEntity);
                    BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
                        savedEntity.getId(),
                        Contants.EntityType.JOB,
                        DTOMapper.toUpdateLogDetail(oldValue, jobEntity)
                    );
                    if (!dto.getJobMap().isEmpty()) {
                        keyValueService.createUpdateKeyValueOfEntityWithLog(
                            jobEntity.getId(),
                            dto.getJobMap(),
                            Contants.EntityType.JOB,
                            true,
                            logEntity
                        );
                    }
                } else throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
            } else {
                JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(dto.getJobCode(), true);
                if (jobEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", jobEntity.getJobCode());
                if (!productionStageRepository.existsAllByProductionStageCodeAndIsActive(dto.getProductionStageCode(), true)) {
                    throw new CustomException(HttpStatus.NOT_FOUND, "stage.notfound", dto.getProductionStageCode());
                }
                if (dto.getProductionStageCode().isEmpty() || dto.getProductionStageCode() == null) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                }
                jobEntity = jobEntityMapper.toEntity(dto);
                Map<String, JobEntity> jobEntityMap = jobEntityList
                    .stream()
                    .collect(Collectors.toMap(JobEntity::getJobCode, Function.identity()));
                if (jobEntityMap.keySet().contains(jobEntity.getJobCode())) {
                    throw new CustomException(HttpStatus.CONFLICT, "must.not.add.duplicate.codes", jobEntity.getJobCode());
                } else {
                    jobRepository.save(jobEntity);
                    businessLogService.insertInsertionLog(
                        jobEntity.getId(),
                        Contants.EntityType.JOB,
                        DTOMapper.toLogDetail(jobEntity, dto.getJobMap())
                    );
                }
                if (!dto.getJobMap().isEmpty()) {
                    List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                        jobEntity.getId(),
                        dto.getJobMap(),
                        Contants.EntityType.JOB
                    );
                    keyValueV2Repository.saveAll(keyValueEntities);
                }
            }
        }
        return new CommonResponse<>().success("Thành công");
    }

    @Transactional
    public CommonResponse updateJob(JobDTO dto, String jobCode) {
        JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(jobCode, true);
        if (jobEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        jobEntity.setJobName(dto.getJobName());
        jobEntity.setProductionStageCode(dto.getProductionStageCode());
        jobEntity.setStatus(dto.getStatus());
        jobRepository.save(jobEntity);
        if (!dto.getJobMap().isEmpty()) {
            if (StringUtils.isEmpty(dto.getProductionStageCode())) {
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    jobEntity.getId(),
                    dto.getJobMap(),
                    Contants.EntityType.PRODUCTIONSTAGE
                );
                keyValueV2Repository.saveAll(keyValueEntities);
            } else {
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    jobEntity.getId(),
                    dto.getJobMap(),
                    Contants.EntityType.JOB
                );
                keyValueV2Repository.saveAll(keyValueEntities);
            }
        }
        if (dto.getJobDTOList() != null) {
            for (JobDTO jobDTO : dto.getJobDTOList()) {
                saveOrUpdateListJob(jobDTO);
            }
        }
        return new CommonResponse<>().success("Cập nhật thành công");
    }

    @Transactional
    public CommonResponse createJob(JobDTO dto) {
        JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(dto.getJobCode(), true);
        if (jobEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", dto.getJobCode());
        if (dto.getJobCode() == null) throw new CustomException(HttpStatus.NOT_FOUND, "invalid.param");
        jobEntity = new JobEntity();
        jobEntity.setJobCode(dto.getJobCode());
        jobEntity.setJobName(dto.getJobName());
        jobEntity.setProductionStageCode(dto.getProductionStageCode());
        jobEntity.setStatus(dto.getStatus());
        jobRepository.save(jobEntity);
        if (!dto.getJobMap().isEmpty()) {
            if (StringUtils.isEmpty(dto.getProductionStageCode())) {
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    jobEntity.getId(),
                    dto.getJobMap(),
                    Contants.EntityType.PRODUCTIONSTAGE
                );
                keyValueV2Repository.saveAll(keyValueEntities);
            } else {
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    jobEntity.getId(),
                    dto.getJobMap(),
                    Contants.EntityType.JOB
                );
                keyValueV2Repository.saveAll(keyValueEntities);
            }
        }
        businessLogService.insertInsertionLog(
            jobEntity.getId(),
            Contants.EntityType.JOB,
            DTOMapper.toLogDetail(jobEntity, dto.getJobMap())
        );
        if (dto.getJobDTOList() != null) {
            for (JobDTO jobDTO : dto.getJobDTOList()) {
                saveOrUpdateListJob(jobDTO);
            }
        }
        return new CommonResponse<>().success("Thêm mới thành công");
    }

    @Transactional
    public CommonResponse deleteJob(String jobCode) {
        JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(jobCode, true);
        if (jobEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        jobEntity.setActive(false);
        jobRepository.save(jobEntity);
        businessLogService.insertDeleteLog(jobEntity.getId(), Contants.EntityType.JOB, DTOMapper.toDeletionLogDetail(jobEntity));
        List<JobEntity> jobEntityList = jobRepository.getJobEntitiesByProductionStageCode(jobEntity.getJobCode());
        for (JobEntity job : jobEntityList) {
            deleteJobChild(job);
        }
        return new CommonResponse().success("Đã xóa " + jobCode);
    }

    public void deleteJobChild(JobEntity jobEntity) {
        if (jobEntity == null) return;
        jobEntity.setActive(false);
        jobRepository.save(jobEntity);
        businessLogService.insertDeleteLog(jobEntity.getId(), Contants.EntityType.JOB, DTOMapper.toDeletionLogDetail(jobEntity));
        List<JobEntity> jobEntityList = jobRepository.getJobEntitiesByProductionStageCode(jobEntity.getProductionStageCode());
        for (JobEntity job : jobEntityList) {
            deleteJobChild(job);
        }
    }

    public PageResponse<List<JobDTOAPS>> getJobForAPS(List<String> jobCodes) {
        List<JobEntity> jobEntities = jobRepository.findListByJobCodeIgnoreCaseAndIsActive(jobCodes, true);
        List<JobDTOAPS> result = new ArrayList<>();
        for (JobEntity jobEntity : jobEntities) {
            JobDTOAPS jobDTOAPS = jobEntityAPSMapper.toDto(jobEntity);
            if (jobEntity.getProductionStageCode() != null) {
                JobEntity jobEntityParent = jobRepository.findByJobCodeIgnoreCaseAndIsActive(jobEntity.getProductionStageCode(), true);
                jobDTOAPS.setProductionStageName(jobEntityParent.getJobName());
            }
            result.add(jobDTOAPS);
        }
        return new PageResponse<List<JobDTOAPS>>().success().data(result).dataCount(result.size());
    }
}
