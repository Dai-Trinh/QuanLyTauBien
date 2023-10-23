package com.facenet.mdm.service.mapper;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.service.dto.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class DTOMapper {

    private static final Logger log = LoggerFactory.getLogger(DTOMapper.class);

    @Autowired
    ProductionStageEntityMapper productionStageEntityMapper;

    @Autowired
    VendorEntityMapper vendorEntityMapper;

    @Autowired
    JobEntityMapper jobEntityMapper;

    @Autowired
    CoittEntityMapper coittEntityMapper;

    @Autowired
    CustomerEntityMapper customerEntityMapper;

    public ProductionStageDTO toProductionStageDTO(ProductionStageEntity productionStageEntity, List<KeyValueEntityV2> properties) {
        ProductionStageDTO productionStageDTO = productionStageEntityMapper.toDto(productionStageEntity);
        if (CollectionUtils.isEmpty(properties)) return productionStageDTO;

        for (KeyValueEntityV2 property : properties) {
            if (property.getBooleanValue() != null) {
                productionStageDTO
                    .getStageMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getBooleanValue()));
            }
            if (property.getIntValue() != null) {
                productionStageDTO
                    .getStageMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getIntValue()));
            }
            if (property.getDoubleValue() != null) {
                productionStageDTO
                    .getStageMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDoubleValue()));
            }

            if (property.getStringValue() != null) {
                productionStageDTO
                    .getStageMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getStringValue()));
            }

            if (property.getJsonValue() != null) {
                productionStageDTO
                    .getStageMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getJsonValue()));
            }

            if (property.getDateValue() != null) {
                productionStageDTO
                    .getStageMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDateValue()));
            }
        }
        return productionStageDTO;
    }

    public VendorDTO toVendorDTO(VendorEntity vendorEntity, List<KeyValueEntityV2> properties) {
        VendorDTO vendorDTO = vendorEntityMapper.toDto(vendorEntity);
        if (CollectionUtils.isEmpty(properties)) return vendorDTO;

        for (KeyValueEntityV2 property : properties) {
            if (property.getBooleanValue() != null) {
                vendorDTO.getVendorMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getBooleanValue()));
            }
            if (property.getIntValue() != null) {
                vendorDTO.getVendorMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getIntValue()));
            }
            if (property.getDoubleValue() != null) {
                vendorDTO.getVendorMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDoubleValue()));
            }

            if (property.getStringValue() != null) {
                vendorDTO.getVendorMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getStringValue()));
            }

            if (property.getJsonValue() != null) {
                vendorDTO.getVendorMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getJsonValue()));
            }

            if (property.getDateValue() != null) {
                vendorDTO.getVendorMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDateValue()));
            }
        }
        return vendorDTO;
    }

    public JobDTO toJobDTO(JobEntity jobEntity, List<KeyValueEntityV2> properties) {
        JobDTO jobDTO = jobEntityMapper.toDto(jobEntity);
        if (CollectionUtils.isEmpty(properties)) return jobDTO;

        for (KeyValueEntityV2 property : properties) {
            if (property.getBooleanValue() != null) {
                jobDTO.getJobMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getBooleanValue()));
            }
            if (property.getIntValue() != null) {
                jobDTO.getJobMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getIntValue()));
            }
            if (property.getDoubleValue() != null) {
                jobDTO.getJobMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDoubleValue()));
            }

            if (property.getStringValue() != null) {
                jobDTO.getJobMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getStringValue()));
            }

            if (property.getJsonValue() != null) {
                jobDTO.getJobMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getJsonValue()));
            }

            if (property.getDateValue() != null) {
                jobDTO.getJobMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDateValue()));
            }
        }
        return jobDTO;
    }

    public CoittDTO toCoittDTO(CoittEntity coittEntity, List<KeyValueEntityV2> properties) {
        CoittDTO coittDTO = coittEntityMapper.toDto(coittEntity);
        if (
            coittEntity.getMerchandiseGroupEntity() != null &&
            !StringUtils.isEmpty(coittEntity.getMerchandiseGroupEntity().getMerchandiseGroupName())
        ) {
            coittDTO.setMerchandiseGroup(coittEntity.getMerchandiseGroupEntity().getMerchandiseGroupName());
        }
        coittDTO.setCreatedAt(coittEntity.getCreatedAt());
        if (coittEntity.getMerchandiseGroupEntity() != null) {
            coittDTO.setMerchandiseGroup(coittEntity.getMerchandiseGroupEntity().getMerchandiseGroupName());
        }
        if (CollectionUtils.isEmpty(properties)) return coittDTO;

        for (KeyValueEntityV2 property : properties) {
            if (property.getBooleanValue() != null) {
                coittDTO
                    .getPropertiesMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getBooleanValue()));
            }
            if (property.getIntValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getIntValue()));
            }
            if (property.getDoubleValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDoubleValue()));
            }

            if (property.getStringValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getStringValue()));
            }

            if (property.getJsonValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getJsonValue()));
            }

            if (property.getDateValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDateValue()));
            }
        }
        return coittDTO;
    }

    public CoittDTO toCitt1DTO(Citt1Entity citt1Entity, List<KeyValueEntityV2> properties) {
        CoittDTO coittDTO = new CoittDTO();
        coittDTO.setProductCode(citt1Entity.getMaterialCode());
        coittDTO.setProName(citt1Entity.getProName());
        coittDTO.setTechName(citt1Entity.getTechName());
        coittDTO.setUnit(citt1Entity.getUnit());
        coittDTO.setNote(citt1Entity.getNote());
        coittDTO.setStatus(citt1Entity.getStatus());
        if (
            citt1Entity.getMerchandiseGroupEntity() != null &&
            !StringUtils.isEmpty(citt1Entity.getMerchandiseGroupEntity().getMerchandiseGroupName())
        ) {
            coittDTO.setMerchandiseGroup(citt1Entity.getMerchandiseGroupEntity().getMerchandiseGroupName());
        }
        coittDTO.setCreatedAt(citt1Entity.getCreatedAt());
        coittDTO.setItemGroupCode(citt1Entity.getItemGroupCode());
        if (citt1Entity.getMerchandiseGroupEntity() != null) {
            coittDTO.setMerchandiseGroup(citt1Entity.getMerchandiseGroupEntity().getMerchandiseGroupName());
        }
        if (CollectionUtils.isEmpty(properties)) return coittDTO;

        for (KeyValueEntityV2 property : properties) {
            if (property.getBooleanValue() != null) {
                coittDTO
                    .getPropertiesMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getBooleanValue()));
            }
            if (property.getIntValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getIntValue()));
            }
            if (property.getDoubleValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDoubleValue()));
            }

            if (property.getStringValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getStringValue()));
            }

            if (property.getJsonValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getJsonValue()));
            }

            if (property.getDateValue() != null) {
                coittDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDateValue()));
            }
        }
        return coittDTO;
    }

    public Citt1Entity coittToCitt1(CoittEntity coittEntity, String product) {
        Citt1Entity citt1Entity = new Citt1Entity();
        citt1Entity.setMaterialCode(coittEntity.getProductCode());
        citt1Entity.setProName(coittEntity.getProName());
        citt1Entity.setTechName(coittEntity.getTechName());
        citt1Entity.setProductCode(product);
        citt1Entity.setItemGroupCode(coittEntity.getItemGroupCode());
        citt1Entity.setUnit(coittEntity.getUnit());
        citt1Entity.setVersion(coittEntity.getVersion());
        citt1Entity.setNote(coittEntity.getNote());
        citt1Entity.setKind(coittEntity.getKind());
        citt1Entity.setNotice(coittEntity.getNotice());
        citt1Entity.setTemplate(coittEntity.getTemplate());
        citt1Entity.setQuantity(coittEntity.getQuantity());
        citt1Entity.setStatus(coittEntity.getStatus());
        return citt1Entity;
    }

    public <T> List<BusinessLogDetailEntity> toLogDetail(T entity, Map<String, String> dynamicProperties) {
        List<BusinessLogDetailEntity> result = toLogDetail(entity);
        dynamicProperties.forEach((key, value) -> result.add(new BusinessLogDetailEntity().keyName(key).newValue(value)));
        return result;
    }

    public <T> List<BusinessLogDetailEntity> toUpdateLogDetail(T oldValue, T newValue) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : newValue.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if ("id".equals(field.getName()) || "isActive".equals(field.getName())) continue;
            try {
                Field oldValueField = oldValue.getClass().getDeclaredField(field.getName());
                oldValueField.setAccessible(true);
                if (field.get(newValue) != null) {
                    BusinessLogDetailEntity businessLogDetailEntity = new BusinessLogDetailEntity()
                        .keyName(field.getName())
                        .newValue(field.get(newValue).toString());
                    if (oldValueField.get(oldValue) != null) {
                        businessLogDetailEntity.lastValue(oldValueField.get(oldValue).toString());
                    }
                    result.add(businessLogDetailEntity);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                log.error("Error when reading reflection", e);
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public <T> List<BusinessLogDetailEntity> toLogDetail(T entity, List<KeyValueEntityV2> keyValueEntities) {
        List<BusinessLogDetailEntity> result = toLogDetail(entity);
        if (CollectionUtils.isEmpty(keyValueEntities)) return result;

        keyValueEntities.forEach(keyValueEntityV2 ->
            result.add(
                new BusinessLogDetailEntity()
                    .keyName(keyValueEntityV2.getColumnPropertyEntity().getKeyName())
                    .newValue(keyValueEntityV2.getCommonValue())
            )
        );
        return result;
    }

    public <T> List<BusinessLogDetailEntity> toLogDetail(T entity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(entity) != null && !"id".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).newValue(field.get(entity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public <T> List<BusinessLogDetailEntity> toDeletionLogDetail(T entity) {
        List<BusinessLogDetailEntity> result = new ArrayList<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (field.get(entity) != null && !"id".equals(field.getName()) && !"isActive".equals(field.getName())) {
                    result.add(new BusinessLogDetailEntity().keyName(field.getName()).lastValue(field.get(entity).toString()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public CustomerDTO toCustomerDTO(CustomerEntity customerEntity, List<KeyValueEntityV2> properties) {
        CustomerDTO customerDTO = customerEntityMapper.toDto(customerEntity);
        if (CollectionUtils.isEmpty(properties)) return customerDTO;

        for (KeyValueEntityV2 property : properties) {
            if (property.getBooleanValue() != null) {
                customerDTO
                    .getPropertiesMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getBooleanValue()));
            }
            if (property.getIntValue() != null) {
                customerDTO.getPropertiesMap().put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getIntValue()));
            }
            if (property.getDoubleValue() != null) {
                customerDTO
                    .getPropertiesMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDoubleValue()));
            }

            if (property.getStringValue() != null) {
                customerDTO
                    .getPropertiesMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getStringValue()));
            }

            if (property.getJsonValue() != null) {
                customerDTO
                    .getPropertiesMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getJsonValue()));
            }

            if (property.getDateValue() != null) {
                customerDTO
                    .getPropertiesMap()
                    .put(property.getColumnPropertyEntity().getKeyName(), String.valueOf(property.getDateValue()));
            }
        }
        return customerDTO;
    }

    public ProductionStageDTO jobToProductionStage(JobDTO jobDTO) {
        ProductionStageDTO productionStageDTO = new ProductionStageDTO();
        productionStageDTO.setProductionStageCode(jobDTO.getJobCode());
        productionStageDTO.setProductionStageName(jobDTO.getJobName());
        productionStageDTO.setDescription(jobDTO.getJobDescription());
        productionStageDTO.setStatus(jobDTO.getStatus());
        productionStageDTO.setMap(jobDTO.getJobMap());
        productionStageDTO.setJobDTOList(jobDTO.getJobDTOList());
        productionStageDTO.setLevel(jobDTO.getLevel());
        return productionStageDTO;
    }

    public JobDTO entityToJobDTO(JobEntity jobEntity) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setJobCode(jobEntity.getJobCode());
        jobDTO.setJobName(jobEntity.getJobName());
        jobDTO.setJobDescription(jobDTO.getJobDescription());
        jobDTO.setStatus(jobDTO.getStatus());
        return jobDTO;
    }

    public Citt1Entity entityToCitt1(Citt1Entity citt1Entity) {
        Citt1Entity citt1EntityNew = new Citt1Entity();
        citt1EntityNew.setMaterialCode(citt1Entity.getMaterialCode());
        citt1EntityNew.setProName(citt1Entity.getProName());
        //        citt1EntityNew.setTechName(citt1Entity.getTechName());
        //        citt1EntityNew.setItemGroupCode(citt1Entity.getItemGroupCode());
        //        citt1EntityNew.setUnit(citt1Entity.getUnit());
        //        citt1EntityNew.setKind(citt1Entity.getKind());
        //        citt1EntityNew.se
        return citt1EntityNew;
    }

    public Citt1Entity entityToCoitt(CoittEntity coittEntity) {
        Citt1Entity citt1EntityNew = new Citt1Entity();
        citt1EntityNew.setMaterialCode(coittEntity.getProductCode());
        citt1EntityNew.setProName(coittEntity.getProName());
        //        citt1EntityNew.setTechName(citt1Entity.getTechName());
        citt1EntityNew.setItemGroupCode(coittEntity.getItemGroupCode());
        //        citt1EntityNew.setUnit(citt1Entity.getUnit());
        //        citt1EntityNew.setKind(citt1Entity.getKind());
        //        citt1EntityNew.se
        return citt1EntityNew;
    }
}
