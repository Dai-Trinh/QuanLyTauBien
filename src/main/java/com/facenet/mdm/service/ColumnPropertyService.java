package com.facenet.mdm.service;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.KeyDictionaryRepository;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.utils.XlsxExcelHandle;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ColumnPropertyService {

    @Autowired
    KeyDictionaryRepository keyDictionaryRepository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    XlsxExcelHandle xlsxExcelHandle;

    public void updateEntryIndex(List<ColumnPropertyEntity> list) {
        int count = 1;
        for (ColumnPropertyEntity columnPropertyEntity : list) {
            columnPropertyEntity.setIndex(count);
            count++;
        }
    }

    public void createProperty(KeyDictionaryDTO input) {
        ColumnPropertyEntity columnProperty = columnPropertyRepository.getByKeyTitleAndEntityType(
            input.getKeyTitle(),
            input.getEntityType()
        );
        if (columnProperty == null) {
            columnProperty = new ColumnPropertyEntity();
            columnProperty.setKeyTitle(input.getKeyTitle());
            columnProperty.setWidth(input.getWidth());
            columnProperty.setEntityType(input.getEntityType());
            columnProperty.setCheck(input.getCheck());
            columnProperty.setKeyName(UUID.randomUUID().toString());
            columnProperty.setKeyTitle(input.getKeyTitle());
            columnProperty.setDataType(input.getDataType());
            columnProperty.setIsRequired(input.getIsRequired());
            if (input.getIsFixed() != null) {
                columnProperty.setIsFixed(input.getIsFixed());
            }
            int index = columnPropertyRepository.countByEntityTypeAndIsActiveTrue(input.getEntityType());
            List<ColumnPropertyEntity> columnPropertyEntityList = columnPropertyRepository.getAllColumnByEntityType(input.getEntityType());
            ColumnPropertyEntity columnPropertyEntity = columnPropertyEntityList.get(columnPropertyEntityList.size() - 1);
            columnPropertyEntity.setIndex(index + 1);
            columnPropertyRepository.save(columnPropertyEntity);
            columnProperty.setIndex(index);
            //            if (input.getEntryIndex() == null) {
            //                int index = columnPropertyRepository.countByEntityTypeAndIsActiveTrue(input.getEntityType());
            //                List<ColumnPropertyEntity> columnPropertyEntityList = columnPropertyRepository.getAllColumnByEntityType(
            //                    input.getEntityType()
            //                );
            //                ColumnPropertyEntity columnPropertyEntity = columnPropertyEntityList.get(columnPropertyEntityList.size() - 1);
            //                columnPropertyEntity.setIndex(index + 1);
            //                columnPropertyRepository.save(columnPropertyEntity);
            //                columnProperty.setIndex(index);
            //            } else {
            //                List<ColumnPropertyEntity> list = columnPropertyRepository.getAllByIndexGreaterThanEqualAndIsActive(
            //                    input.getEntryIndex(),
            //                    true
            //                );
            //                for (ColumnPropertyEntity columnPropertyEntity : list) {
            //                    columnPropertyEntity.setIndex(columnPropertyEntity.getIndex() + 1);
            //                }
            //                columnPropertyRepository.saveAll(list);
            //                columnProperty.setIndex(input.getEntryIndex());
            //            }
            columnPropertyRepository.save(columnProperty);
            List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(input.getEntityType());
            updateEntryIndex(columnPropertyEntities);
            columnPropertyRepository.saveAll(columnPropertyEntities);
        } else throw new CustomException(HttpStatus.CONFLICT, "duplicate", input.getKeyTitle());
    }

    public void updateProperty(String keyName, KeyDictionaryDTO input, Integer type) {
        //        KeyDictionaryEntity keyDictionaryEntity = keyDictionaryRepository.findByKeyNameAndIsActiveTrue(keyName);
        //        if (keyDictionaryEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        //        keyDictionaryEntity.setKeyTitle(input.getKeyTitle());
        //        keyDictionaryRepository.save(keyDictionaryEntity);
        ColumnPropertyEntity columnProperty = columnPropertyRepository.findByKeyName(keyName, type);
        if (columnProperty == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        columnProperty.setWidth(input.getWidth());
        columnProperty.setKeyTitle(input.getKeyTitle());
        columnProperty.setCheck(input.getCheck());
        columnProperty.setKeyName(keyName);
        columnProperty.setDataType(input.getDataType());
        columnProperty.setKeyTitle(input.getKeyTitle());
        columnProperty.setIsRequired(input.getIsRequired());
        if (input.getIsFixed() != null) {
            columnProperty.setIsFixed(input.getIsFixed());
        }
        if (input.getEntryIndex() == null) {
            int index = columnPropertyRepository.countByEntityTypeAndIsActiveTrue(type);
            columnProperty.setIndex(index + 1);
        } else {
            //            List<ColumnPropertyEntity> list = columnPropertyRepository.getAllByIndexGreaterThanEqual(input.getEntryIndex());
            //            for (ColumnPropertyEntity columnPropertyEntity : list) {
            //                columnPropertyEntity.setIndex(columnPropertyEntity.getIndex() + 1);
            //            }
            //            columnPropertyRepository.saveAll(list);
            //            columnProperty.setIndex(input.getEntryIndex());

            //Cập nhật lại entry index
            if (input.getEntryIndex() < columnProperty.getIndex()) {
                List<ColumnPropertyEntity> list = columnPropertyRepository.getAllColumnByEntityType(type);
                for (ColumnPropertyEntity columnPropertyEntity : list) {
                    if (columnPropertyEntity.getIndex() < columnProperty.getIndex()) {
                        columnPropertyEntity.setIndex(columnPropertyEntity.getIndex() + 1);
                    }
                }
                //                for (ColumnPropertyEntity columnPropertyEntity : list) {
                //                    columnPropertyEntity.setIndex(columnPropertyEntity.getIndex() + 1);
                //                }
                columnPropertyRepository.saveAll(list);
            } else if (input.getEntryIndex() > columnProperty.getIndex()) {
                List<ColumnPropertyEntity> list = columnPropertyRepository.getAllColumnByEntityType(type);
                for (ColumnPropertyEntity columnPropertyEntity : list) {
                    if (columnPropertyEntity.getIndex() > columnProperty.getIndex()) {
                        columnPropertyEntity.setIndex(columnPropertyEntity.getIndex() - 1);
                    }
                }
                columnPropertyRepository.saveAll(list);
            }
            columnProperty.setIndex(input.getEntryIndex());
        }
        columnPropertyRepository.save(columnProperty);
        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(type);
        updateEntryIndex(columnPropertyEntities);
        columnPropertyRepository.saveAll(columnPropertyEntities);
    }

    public void deleteProperty(String keyName, Integer type) {
        ColumnPropertyEntity columnPropertyEntity = columnPropertyRepository.findByKeyName(keyName, type);
        if (columnPropertyEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        columnPropertyEntity.setIsActive(false);
        columnPropertyRepository.save(columnPropertyEntity);
        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumnByEntityType(type);
        updateEntryIndex(columnPropertyEntities);
        columnPropertyRepository.saveAll(columnPropertyEntities);
    }

    public CommonResponse getColumnList(Integer entityType) {
        List<ColumnPropertyEntity> keyDictionaryDTOList = columnPropertyRepository.getAllByEntityTypeAndIsActiveTrue(entityType);
        return new CommonResponse<>().success().data(keyDictionaryDTOList);
    }

    @Transactional
    public CommonResponse importColumn(MultipartFile file, Integer entityType) throws IOException, ParseException {
        List<KeyDictionaryDTO> list = xlsxExcelHandle.readColumnFromExcel(file.getInputStream(), entityType);
        for (KeyDictionaryDTO k : list) {
            ColumnPropertyEntity columnProperty = columnPropertyRepository.getByKeyTitleAndEntityType(k.getKeyTitle(), k.getEntityType());
            if (columnProperty != null) throw new CustomException(HttpStatus.CONFLICT, "duplicate", columnProperty.getKeyTitle());
        }
        for (KeyDictionaryDTO k : list) {
            createProperty(k);
        }
        return new CommonResponse().success();
    }

    public List<ColumnPropertyEntity> search(KeyDictionaryDTO input, String common, Pageable pageable) {
        List<ColumnPropertyEntity> list = columnPropertyRepository.getAllColumn(input, common, pageable);
        return list;
    }

    public Set<String> autocompleteCommonConfig(KeyDictionaryDTO input, String common, Pageable pageable) {
        Set<String> listAuto = new LinkedHashSet<>();

        //Get list object that it had the search.
        List<ColumnPropertyEntity> columnPropertyEntities = columnPropertyRepository.getAllColumn(input, common, pageable);
        System.err.println("columnPropertyEntities: " + columnPropertyEntities.size());

        //Compare to filter to get the list result
        if (columnPropertyEntities != null && !columnPropertyEntities.isEmpty()) {
            columnPropertyEntities.forEach(item -> {
                System.err.println("item: " + item.getDataType());
                if (item.getKeyTitle().toLowerCase().contains(common.toLowerCase())) listAuto.add(item.getKeyTitle());
                if (item.getIsRequiredConvertString().toLowerCase().contains(common.toLowerCase())) listAuto.add(
                    item.getIsRequiredConvertString()
                );
                if (item.getCheckConvertString().toLowerCase().contains(common.toLowerCase())) listAuto.add(item.getCheckConvertString());
                if (item.getDataTypeConvertString().toLowerCase().contains(common.toLowerCase())) listAuto.add(
                    item.getDataTypeConvertString()
                );
            });
        }
        System.err.println(listAuto);

        return listAuto;
    }
}
