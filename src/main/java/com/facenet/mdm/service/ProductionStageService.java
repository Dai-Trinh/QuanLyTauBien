package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.*;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.excel.StageJobExcel;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.mapper.JobEntityMapper;
import com.facenet.mdm.service.mapper.ProductionStageEntityMapper;
import com.facenet.mdm.service.mapper.qms.JobQmsMapper;
import com.facenet.mdm.service.mapper.qms.ProductionStageQmsMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import com.sun.xml.bind.Utils;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductionStageService {

    private static final Logger logger = LoggerFactory.getLogger(ProductionStageService.class);

    @Autowired
    ProductionStageRepository productionStageRepository;

    @Autowired
    ProductionStageEntityMapper productionStageEntityMapper;

    @Autowired
    DTOMapper DTOMapper;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobEntityMapper jobEntityMapper;

    @Autowired
    JobQmsMapper jobQmsMapper;

    @Autowired
    ProductionStageQmsMapper productionStageQmsMapper;

    @Autowired
    JobService jobService;

    @Autowired
    BusinessLogService businessLogService;

    public CommonResponse getListProductionStage(PageFilterInput<ProductionStageDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        Page<ProductionStageEntity> stages = productionStageRepository.getAllStage(input, pageable);
        List<ProductionStageEntity> list = stages.getContent();
        Map<Integer, ProductionStageEntity> stageMap = list
            .stream()
            .collect(Collectors.toMap(ProductionStageEntity::getId, Function.identity()));
        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.PRODUCTIONSTAGE,
            stageMap.keySet()
        );
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        List<ProductionStageDTO> resultList = new ArrayList<>();
        for (ProductionStageEntity productionStageEntity : stages.getContent()) {
            resultList.add(DTOMapper.toProductionStageDTO(productionStageEntity, propertyMap.get(productionStageEntity.getId())));
        }

        return new PageResponse<List<ProductionStageDTO>>(stages.getTotalElements()).success().data(resultList);
    }

    @Transactional
    public CommonResponse createProductionStage(ProductionStageDTO dto) {
        JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(dto.getProductionStageCode(), true);
        if (jobEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", dto.getProductionStageCode());
        if (dto.getProductionStageCode() == null) throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
        jobEntity = new JobEntity();
        jobEntity.setJobCode(dto.getProductionStageCode());
        jobEntity.setJobName(dto.getProductionStageName());
        jobEntity.setDescription(dto.getDescription());
        jobEntity.setStatus(dto.getStatus());
        jobRepository.save(jobEntity);
        businessLogService.insertInsertionLog(
            jobEntity.getId(),
            Contants.EntityType.PRODUCTIONSTAGE,
            DTOMapper.toLogDetail(jobEntity, dto.getStageMap())
        );
        if (!dto.getJobDTOList().isEmpty()) {
            for (JobDTO jobDTO : dto.getJobDTOList()) {
                jobService.saveOrUpdateListJob(jobDTO);
            }
        }
        if (dto.getStageMap().isEmpty()) return new CommonResponse<>().success("Thêm mới thành công");
        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
            jobEntity.getId(),
            dto.getStageMap(),
            Contants.EntityType.PRODUCTIONSTAGE
        );
        keyValueV2Repository.saveAll(keyValueEntities);
        return new CommonResponse<>().success("Thêm mới thành công");
    }

    @Transactional
    public CommonResponse updateProductionStage(ProductionStageDTO dto, String productionStageCode) {
        JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(productionStageCode, true);
        if (jobEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        JobEntity oldValue = new JobEntity(jobEntity);
        jobEntity.setJobName(dto.getProductionStageName());
        jobEntity.setDescription(dto.getDescription());
        jobEntity.setStatus(dto.getStatus());
        JobEntity savedEntity = jobRepository.save(jobEntity);
        BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
            savedEntity.getId(),
            Contants.EntityType.PRODUCTIONSTAGE,
            DTOMapper.toUpdateLogDetail(oldValue, jobEntity)
        );

        if (!dto.getJobDTOList().isEmpty()) {
            for (JobDTO jobDTO : dto.getJobDTOList()) {
                jobService.saveOrUpdateListJob(jobDTO);
            }
        }

        if (dto.getStageMap().isEmpty()) return new CommonResponse<>().success("Cập nhật thành công");
        //        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
        //            productionStageEntity.getId(),
        //            dto.getStageMap(),
        //            Contants.EntityType.PRODUCTIONSTAGE
        //        );
        //        keyValueV2Repository.saveAll(keyValueEntities);
        keyValueService.createUpdateKeyValueOfEntityWithLog(
            jobEntity.getId(),
            dto.getStageMap(),
            Contants.EntityType.PRODUCTIONSTAGE,
            true,
            logEntity
        );
        return new CommonResponse<>().success("Cập nhật thành công");
    }

    @Transactional
    public CommonResponse deleteProductionStage(String productionStageCode) {
        ProductionStageEntity productionStageEntity = productionStageRepository.findProductionStageEntitiesByCode(productionStageCode);
        if (productionStageEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        productionStageEntity.setIsActive(false);
        productionStageRepository.save(productionStageEntity);
        businessLogService.insertDeleteLog(
            productionStageEntity.getId(),
            Contants.EntityType.PRODUCTIONSTAGE,
            DTOMapper.toDeletionLogDetail(productionStageEntity)
        );
        return new CommonResponse().success("Đã xóa " + productionStageCode);
    }

    @Transactional
    public CommonResponse importExcel(MultipartFile file) throws IOException, ParseException {
        StageJobExcel stageJobExcel = xlsxExcelHandle.readStageAndJobFromExcel(file.getInputStream());
        //List<ProductionStageEntity> productionStageEntities = new ArrayList<>();
        List<JobEntity> jobEntities = new ArrayList<>();
        List<KeyValueEntity> keyValueEntities = new ArrayList<>();
        for (ProductionStageDTO l : stageJobExcel.getProductionStageDTOList()) {
            JobEntity jobEntityD = jobRepository.findByJobCodeIgnoreCaseAndIsActive(l.getProductionStageCode(), true);
            if (jobEntityD != null) {
                throw new CustomException(HttpStatus.CONFLICT, "duplicate", l.getProductionStageCode());
            }
            if (l.getProductionStageCode() == null || l.getProductionStageCode().isEmpty()) continue;
            JobEntity jobEntity = new JobEntity();
            jobEntity.setJobCode(l.getProductionStageCode());
            jobEntity.setJobName(l.getProductionStageName());
            jobEntity.setDescription(l.getDescription());
            jobEntity.setStatus(l.getStatus());
            jobEntity.setActive(true);
            jobEntities.add(jobEntity);
            List<KeyValueEntityV2> keyValueStages = keyValueService.createOrUpdateKeyValueEntity(
                jobEntity.getId(),
                l.getStageMap(),
                Contants.EntityType.PRODUCTIONSTAGE
            );
        }
        if (!stageJobExcel.getJobDTOList().isEmpty() && stageJobExcel.getJobDTOList() != null) {
            for (JobDTO l : stageJobExcel.getJobDTOList()) {
                JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(l.getJobCode(), true);

                if (jobEntity != null) {
                    throw new CustomException(HttpStatus.CONFLICT, "duplicate", jobEntity.getJobCode());
                }
                if (l.getProductionStageCode().isEmpty() || l.getProductionStageCode() == null) {
                    throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
                }
                if (l.getJobCode() == null || l.getJobCode().isEmpty()) continue;
                JobEntity jobEntity1 = jobEntityMapper.toEntity(l);
                jobEntities.add(jobEntity1);
                List<KeyValueEntityV2> keyValueJobs = keyValueService.createOrUpdateKeyValueEntity(
                    jobEntity1.getId(),
                    l.getJobMap(),
                    Contants.EntityType.JOB
                );
            }
        }
        Map<Integer, List<BusinessLogDetailEntity>> stageBusinessLogMap = new HashMap<>();
        Map<Integer, List<BusinessLogDetailEntity>> jobBusinessLogMap = new HashMap<>();
        for (ProductionStageDTO l : stageJobExcel.getProductionStageDTOList()) {
            JobEntity productionStageEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(l.getProductionStageCode(), true);
            if (productionStageEntity == null) {
                if (l.getProductionStageCode() == null || l.getProductionStageCode().isEmpty()) continue;
                productionStageEntity = new JobEntity();
                productionStageEntity.setJobCode(l.getProductionStageCode());
                productionStageEntity.setJobName(l.getProductionStageName());
                productionStageEntity.setDescription(l.getDescription());
                productionStageEntity.setStatus(l.getStatus());
                productionStageEntity.setActive(true);
                jobEntities.add(productionStageEntity);
                jobRepository.save(productionStageEntity);
                ProductionStageEntity productionStageEntityLog = new ProductionStageEntity();
                productionStageEntityLog.setId(productionStageEntity.getId());
                productionStageEntityLog.setProductionStageCode(productionStageEntity.getJobCode());
                productionStageEntityLog.setProductionStageName(productionStageEntity.getJobName());
                productionStageEntityLog.setDescription(productionStageEntity.getDescription());
                productionStageEntityLog.setStatus(productionStageEntity.getStatus());
                List<KeyValueEntityV2> keyValueStages = keyValueService.createOrUpdateKeyValueEntity(
                    productionStageEntity.getId(),
                    l.getStageMap(),
                    Contants.EntityType.PRODUCTIONSTAGE
                );
                stageBusinessLogMap.put(productionStageEntity.getId(), DTOMapper.toLogDetail(productionStageEntityLog, keyValueStages));
                keyValueV2Repository.saveAll(keyValueStages);
            }
        }
        if (!stageJobExcel.getJobDTOList().isEmpty() || stageJobExcel.getJobDTOList() != null) {
            for (JobDTO l : stageJobExcel.getJobDTOList()) {
                JobEntity jobEntity = jobRepository.findByJobCodeIgnoreCaseAndIsActive(l.getJobCode(), true);
                if (l.getJobCode() == null || l.getJobCode().isEmpty()) continue;
                if (jobEntity == null) {
                    //                    if (!productionStageRepository.existsAllByProductionStageCodeAndIsActive(l.getProductionStageCode(), true)) {
                    //                        throw new CustomException(HttpStatus.NOT_FOUND, "stage.notfound", l.getProductionStageCode());
                    //                    }
                    JobEntity productStage = jobRepository.findByJobCodeIgnoreCaseAndIsActive(l.getProductionStageCode(), true);
                    if (productStage == null) {
                        throw new CustomException(HttpStatus.NOT_FOUND, "not.found", l.getProductionStageCode());
                    }
                    jobEntity = new JobEntity();
                    jobEntity.setJobCode(l.getJobCode());
                    jobEntity.setProductionStageCode(l.getProductionStageCode());
                    jobEntity.setJobName(l.getJobName());
                    jobEntity.setDescription(l.getJobDescription());
                    jobEntity.setStatus(l.getStatus());
                    jobEntity.setActive(true);
                    jobEntities.add(jobEntity);
                    jobRepository.save(jobEntity);
                    List<KeyValueEntityV2> keyValues = keyValueService.createOrUpdateKeyValueEntity(
                        jobEntity.getId(),
                        l.getJobMap(),
                        Contants.EntityType.JOB
                    );
                    jobBusinessLogMap.put(jobEntity.getId(), DTOMapper.toLogDetail(jobEntity, keyValues));
                    keyValueV2Repository.saveAll(keyValues);
                }
            }
        }

        businessLogService.insertInsertionLogByBatch(Contants.EntityType.PRODUCTIONSTAGE, stageBusinessLogMap);
        businessLogService.insertInsertionLogByBatch(Contants.EntityType.JOB, jobBusinessLogMap);

        return new CommonResponse().success();
    }

    public List<StageQmsDTO> getListForQms(PageFilterInput<StageQmsDTO> input) {
        StageQmsDTO stageQmsDTO = input.getFilter();
        List<ProductionStageEntity> productionStageEntities = productionStageRepository.getAllStageForQms(
            stageQmsDTO,
            input.getCommon(),
            input.getSortProperty(),
            input.getSortOrder()
        );
        List<StageQmsDTO> stageQmsDTOList = new ArrayList<>();
        for (ProductionStageEntity p : productionStageEntities) {
            stageQmsDTOList.add(productionStageQmsMapper.toDto(p));
        }
        for (StageQmsDTO p : stageQmsDTOList) {
            JobQmsDTO jobQmsDTO = new JobQmsDTO();
            jobQmsDTO.setProductionStageCode(p.getProductionStageCode());
            List<JobQmsDTO> jobQmsDTOS = new ArrayList<>();
            List<JobEntity> jobEntityList = jobRepository.getAllJobForQms(jobQmsDTO);
            for (JobEntity j : jobEntityList) {
                jobQmsDTOS.add(jobQmsMapper.toDto(j));
            }
            p.setJobList(jobQmsDTOS);
        }
        return stageQmsDTOList;
    }

    public List<String> autocompleteCommon(PageFilterInput<JobDTO> input) {
        Set<String> listAuto = new LinkedHashSet<>();
        String common = input.getCommon();
        Pageable pageable = Pageable.unpaged();
        //Get list object that it had the search.
        List<JobEntity> jobEntities = jobRepository.getListJobCode(input);
        //Compare to filter to get the list result
        if (jobEntities != null && !jobEntities.isEmpty()) {
            jobEntities.forEach(item -> {
                List<KeyValueEntityV2> list = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    Contants.EntityType.PRODUCTIONSTAGE,
                    new ArrayList<>(Arrays.asList(item.getId()))
                );
                if (
                    item.getJobCode() != null &&
                    !item.getJobCode().isEmpty() &&
                    item.getJobCode().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getJobCode());
                if (
                    item.getDescription() != null &&
                    !item.getDescription().isEmpty() &&
                    item.getDescription().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getDescription());
                if (
                    item.getJobName() != null &&
                    !item.getJobName().isEmpty() &&
                    item.getJobName().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getJobName());
                for (KeyValueEntityV2 k : list) {
                    if (k.getCommonValue().toLowerCase().contains(common.toLowerCase())) listAuto.add(k.getCommonValue());
                }
            });
        }
        List<String> resultList = new ArrayList<>(listAuto);
        resultList = resultList.subList(0, Math.min(input.getPageSize(), resultList.size()));
        return resultList;
    }

    public List<ProductionStageDTO> exportProductionStage() {
        List<ProductionStageEntity> productionStageEntities = productionStageRepository.getAllProductionStage();

        Map<Integer, ProductionStageEntity> stageMap = productionStageEntities
            .stream()
            .collect(Collectors.toMap(ProductionStageEntity::getId, Function.identity()));
        List<KeyValueEntityV2> propertiesStage = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.PRODUCTIONSTAGE,
            stageMap.keySet()
        );
        Map<Integer, List<KeyValueEntityV2>> propertyMapStage = propertiesStage
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        List<ProductionStageDTO> resultList = new ArrayList<>();
        for (ProductionStageEntity productionStageEntity : productionStageEntities) {
            ProductionStageDTO productionStageDTO = DTOMapper.toProductionStageDTO(
                productionStageEntity,
                propertyMapStage.get(productionStageEntity.getId())
            );

            List<JobEntity> jobEntities = jobRepository.getJobEntitiesByProductionStageCode(productionStageDTO.getProductionStageCode());

            Map<Integer, JobEntity> stageJob = jobEntities.stream().collect(Collectors.toMap(JobEntity::getId, Function.identity()));
            List<KeyValueEntityV2> propertiesJob = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.JOB,
                stageJob.keySet()
            );
            Map<Integer, List<KeyValueEntityV2>> propertyMapJob = propertiesJob
                .stream()
                .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

            List<JobDTO> jobDTOList = new ArrayList<>();
            for (JobEntity jobEntity : jobEntities) {
                jobDTOList.add(DTOMapper.toJobDTO(jobEntity, propertyMapJob.get(jobEntity.getId())));
            }

            productionStageDTO.setJobDTOList(jobDTOList);
            resultList.add(productionStageDTO);
        }

        return resultList;
    }
}
