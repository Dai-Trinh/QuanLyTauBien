package com.facenet.mdm.service;

import com.facenet.mdm.domain.*;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.CustomerRepository;
import com.facenet.mdm.repository.KeyValueV2Repository;
import com.facenet.mdm.repository.custom.CustomerCustomRepository;
import com.facenet.mdm.service.dto.CustomerDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.CustomerEntityMapper;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerEntityMapper customerEntityMapper;

    @Autowired
    BusinessLogService businessLogService;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    DTOMapper dtoMapper;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    @Autowired
    CustomerCustomRepository customerCustomRepository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    public CommonResponse getList(PageFilterInput<CustomerDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        Page<CustomerEntity> customerEntities = customerCustomRepository.searchCustomers(input, pageable);
        List<CustomerEntity> list = customerEntities.getContent();
        Map<Integer, CustomerEntity> map = list.stream().collect(Collectors.toMap(CustomerEntity::getId, Function.identity()));
        List<KeyValueEntityV2> properties = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
            Contants.EntityType.CUSTOMER,
            map.keySet()
        );
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        List<CustomerDTO> resultList = new ArrayList<>();
        for (CustomerEntity customerEntity : customerEntities.getContent()) {
            resultList.add(dtoMapper.toCustomerDTO(customerEntity, propertyMap.get(customerEntity.getId())));
        }

        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.CUSTOMER);

        return new PageResponse<List<CustomerDTO>>(customerEntities.getTotalElements())
            .success()
            .data(resultList)
            .columns(columnPropertyEntities);
    }

    @Transactional
    public CommonResponse addCustomer(CustomerDTO dto) {
        CustomerEntity customerEntity = customerRepository.findByCustomerCodeIgnoreCaseAndIsActive(dto.getCustomerCode(), true);
        if (customerEntity != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", dto.getCustomerCode());
        if (dto.getCustomerCode() == null) throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");
        customerEntity = customerEntityMapper.toEntity(dto);
        customerRepository.save(customerEntity);
        businessLogService.insertInsertionLog(
            customerEntity.getId(),
            Contants.EntityType.CUSTOMER,
            dtoMapper.toLogDetail(customerEntity, dto.getPropertiesMap())
        );
        if (dto.getPropertiesMap().isEmpty()) return new CommonResponse<>().success("Thêm mới thành công");
        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
            customerEntity.getId(),
            dto.getPropertiesMap(),
            Contants.EntityType.CUSTOMER
        );
        keyValueV2Repository.saveAll(keyValueEntities);
        return new CommonResponse<>().success("Thêm mới thành công");
    }

    @Transactional
    public CommonResponse updateCustomer(CustomerDTO dto, String customerCode) {
        CustomerEntity customerEntity = customerRepository.findByCustomerCodeIgnoreCaseAndIsActive(customerCode, true);
        if (customerEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        CustomerEntity oldValue = new CustomerEntity(customerEntity);
        customerEntity.setCustomerName(dto.getCustomerName());
        customerEntity.setCustomerEmail(dto.getCustomerEmail());
        customerEntity.setCustomerPhone(dto.getCustomerPhone());
        customerEntity.setAddress(dto.getAddress());
        customerEntity.setCustomerType(dto.getCustomerType());
        customerEntity.setStatus(dto.getStatus());
        CustomerEntity savedEntity = customerRepository.save(customerEntity);
        BusinessLogEntity logEntity = businessLogService.insertUpdateLog(
            savedEntity.getId(),
            Contants.EntityType.CUSTOMER,
            dtoMapper.toUpdateLogDetail(oldValue, customerEntity)
        );
        if (dto.getPropertiesMap().isEmpty()) return new CommonResponse<>().success("Cập nhật thành công");
        //        List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
        //            productionStageEntity.getId(),
        //            dto.getStageMap(),
        //            Contants.EntityType.PRODUCTIONSTAGE
        //        );
        //        keyValueV2Repository.saveAll(keyValueEntities);
        keyValueService.createUpdateKeyValueOfEntityWithLog(
            customerEntity.getId(),
            dto.getPropertiesMap(),
            Contants.EntityType.CUSTOMER,
            true,
            logEntity
        );
        return new CommonResponse<>().success("Cập nhật thành công");
    }

    @Transactional
    public CommonResponse deleteCustomer(String customerCode) {
        CustomerEntity customerEntity = customerRepository.findByCustomerCodeIgnoreCaseAndIsActive(customerCode, true);
        if (customerEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        customerEntity.setActive(false);
        customerRepository.save(customerEntity);
        businessLogService.insertDeleteLog(
            customerEntity.getId(),
            Contants.EntityType.CUSTOMER,
            dtoMapper.toDeletionLogDetail(customerEntity)
        );
        return new CommonResponse().success("Đã xóa " + customerCode);
    }

    @Transactional
    public CommonResponse importInfo(MultipartFile file) throws IOException, ParseException {
        List<CustomerDTO> list = xlsxExcelHandle.readCustomerInfo(file.getInputStream());
        for (CustomerDTO v : list) {
            CustomerEntity customerEntity = customerRepository.findByCustomerCodeIgnoreCaseAndIsActive(v.getCustomerCode(), true);
            if (customerEntity != null) {
                throw new CustomException(HttpStatus.CONFLICT, "duplicate", customerEntity.getCustomerCode());
            } else {
                if (v.getCustomerCode() == null || v.getCustomerCode().isEmpty()) continue;
                customerEntity = customerEntityMapper.toEntity(v);
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    customerEntity.getId(),
                    v.getPropertiesMap(),
                    Contants.EntityType.CUSTOMER
                );
            }
        }
        Map<Integer, List<BusinessLogDetailEntity>> businessLogMap = new HashMap<>();
        for (CustomerDTO v : list) {
            CustomerEntity customerEntity = customerRepository.findByCustomerCodeIgnoreCaseAndIsActive(v.getCustomerCode(), true);
            if (customerEntity == null) {
                if (v.getCustomerCode() == null || v.getCustomerCode().isEmpty()) continue;
                customerEntity = customerEntityMapper.toEntity(v);
                customerRepository.save(customerEntity);
                List<KeyValueEntityV2> keyValueEntities = keyValueService.createOrUpdateKeyValueEntity(
                    customerEntity.getId(),
                    v.getPropertiesMap(),
                    Contants.EntityType.CUSTOMER
                );
                businessLogMap.put(customerEntity.getId(), dtoMapper.toLogDetail(customerEntity, keyValueEntities));
                keyValueV2Repository.saveAll(keyValueEntities);
            }
        }
        businessLogService.insertInsertionLogByBatch(Contants.EntityType.CUSTOMER, businessLogMap);
        return new CommonResponse().success();
    }

    public List<String> autocompleteCommon(PageFilterInput<CustomerDTO> input) {
        Set<String> listAuto = new LinkedHashSet<>();
        String common = input.getCommon();
        Pageable pageable = Pageable.unpaged();
        //Get list object that it had the search.
        List<CustomerEntity> customerEntities = customerCustomRepository.searchCustomers(input, pageable).getContent();
        //Compare to filter to get the list result
        if (customerEntities != null && !customerEntities.isEmpty()) {
            customerEntities.forEach(item -> {
                List<KeyValueEntityV2> list = keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                    Contants.EntityType.CUSTOMER,
                    new ArrayList<>(Arrays.asList(item.getId()))
                );
                if (
                    item.getCustomerCode() != null &&
                    !item.getCustomerCode().isEmpty() &&
                    item.getCustomerCode().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getCustomerCode());
                if (
                    item.getCustomerName() != null &&
                    !item.getCustomerName().isEmpty() &&
                    item.getCustomerName().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getCustomerName());
                if (
                    item.getCustomerType() != null &&
                    !item.getCustomerType().isEmpty() &&
                    item.getCustomerType().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getCustomerType());
                if (
                    item.getCustomerEmail() != null &&
                    !item.getCustomerEmail().isEmpty() &&
                    item.getCustomerEmail().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getCustomerEmail());
                if (
                    item.getCustomerPhone() != null &&
                    !item.getCustomerPhone().isEmpty() &&
                    item.getCustomerPhone().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getCustomerPhone());
                if (
                    item.getAddress() != null &&
                    !item.getAddress().isEmpty() &&
                    item.getAddress().toLowerCase().contains(common.toLowerCase())
                ) listAuto.add(item.getAddress());
                for (KeyValueEntityV2 k : list) {
                    if (k.getCommonValue().toLowerCase().contains(common.toLowerCase())) listAuto.add(k.getCommonValue());
                }
            });
        }
        List<String> resultList = new ArrayList<>(listAuto);
        resultList = resultList.subList(0, Math.min(input.getPageSize(), resultList.size()));
        return resultList;
    }
}
