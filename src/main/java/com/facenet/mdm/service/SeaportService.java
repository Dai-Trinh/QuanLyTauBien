package com.facenet.mdm.service;

import com.facenet.mdm.domain.ColumnPropertyEntity;
import com.facenet.mdm.domain.KeyValueEntityV2;
import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.domain.SeaportEntity;
import com.facenet.mdm.repository.ColumnPropertyRepository;
import com.facenet.mdm.repository.KeyValueV2Repository;
import com.facenet.mdm.repository.SeaportRepository;
import com.facenet.mdm.repository.custom.SeaportCustomRepository;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.dto.SeaportDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.DTOMapper;
import com.facenet.mdm.service.mapper.SeaportEntityMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeaportService {

    @Autowired
    SeaportRepository seaportRepository;

    @Autowired
    SeaportEntityMapper seaportEntityMapper;

    @Autowired
    DTOMapper dtoMapper;

    @Autowired
    KeyValueV2Repository keyValueV2Repository;

    @Autowired
    ColumnPropertyRepository columnPropertyRepository;

    @Autowired
    KeyValueService keyValueService;

    @Autowired
    SeaportCustomRepository seaportCustomRepository;

    public PageResponse<List<SeaportDTO>> getAllSeaport(PageFilterInput<SeaportDTO> filterInput) {
        Pageable pageable = filterInput.getPageSize() == 0
            ? Pageable.unpaged()
            : PageRequest.of(filterInput.getPageNumber(), filterInput.getPageSize());
        Page<SeaportEntity> resultEntity = seaportCustomRepository.getAll(filterInput, pageable);

        List<KeyValueEntityV2> properties;
        if (pageable.isPaged()) properties =
            keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.SEAPORT,
                resultEntity.getContent().stream().map(SeaportEntity::getId).collect(Collectors.toList())
            ); else properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.MACHINE);
        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        List<SeaportDTO> resultList = new ArrayList<>();
        for (SeaportEntity seaportEntity : resultEntity.getContent()) {
            resultList.add(dtoMapper.toDTO(seaportEntity, propertyMap.get(seaportEntity.getId())));
        }

        List<ColumnPropertyEntity> columns = columnPropertyRepository.getAllColumnByEntityType(Contants.EntityType.SEAPORT);

        return new PageResponse<List<SeaportDTO>>().success().data(resultList).dataCount(resultEntity.getTotalElements()).columns(columns);
    }

    public Set<String> getForCommonSearch(PageFilterInput<SeaportDTO> filterInput) {
        Set<String> listAuto = new LinkedHashSet<>();

        //Get list object that it had the search.
        Pageable pageable = filterInput.getPageSize() == 0
            ? Pageable.unpaged()
            : PageRequest.of(filterInput.getPageNumber(), filterInput.getPageSize());
        Page<SeaportEntity> resultEntity = seaportCustomRepository.getAll(filterInput, pageable);

        List<KeyValueEntityV2> properties;
        if (pageable.isPaged()) properties =
            keyValueV2Repository.findByEntityTypeAndEntityKeyInAndIsActiveTrue(
                Contants.EntityType.SEAPORT,
                resultEntity.getContent().stream().map(SeaportEntity::getId).collect(Collectors.toList())
            ); else properties = keyValueV2Repository.findByEntityTypeAndIsActiveTrue(Contants.EntityType.SEAPORT);

        Map<Integer, List<KeyValueEntityV2>> propertyMap = properties
            .stream()
            .collect(Collectors.groupingBy(KeyValueEntityV2::getEntityKey));

        if (properties != null && !properties.isEmpty()) {
            properties.forEach(prop -> {
                if (prop.getCommonValue().toLowerCase().contains(filterInput.getCommon().toLowerCase())) {
                    listAuto.add(prop.getCommonValue());
                }
            });
        }

        List<SeaportDTO> resultList = new ArrayList<>();
        for (SeaportEntity seaportEntity : resultEntity.getContent()) {
            resultList.add(dtoMapper.toDTO(seaportEntity, propertyMap.get(seaportEntity.getId())));
        }

        // Compare to filter to get the list result
        if (resultList != null && !resultList.isEmpty()) {
            System.err.println("vào hàm này");
            resultList.forEach(item -> {
                if (item.getSeaportCode() != null) {
                    if (item.getSeaportCode().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getSeaportCode()
                    );
                }

                if (item.getSeaportName() != null) {
                    if (item.getSeaportName().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getSeaportName()
                    );
                }


                if (item.getSeaportNation() != null) {
                    if (item.getSeaportNation().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getSeaportNation()
                    );
                }

                if (item.getSeaportAddress() != null) {
                    if (item.getSeaportAddress().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getSeaportAddress()
                    );
                }

                if (item.getLongitude() != null) {
                    if (item.getLongitude().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getLongitude()
                    );
                }

                if (item.getLatitude() != null) {
                    if (item.getLatitude().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getLatitude()
                    );
                }

                if (item.getNote() != null) {
                    if (item.getNote().toLowerCase().contains(filterInput.getCommon().toLowerCase())) listAuto.add(
                        item.getNote()
                    );
                }
            });
        }
        return listAuto;
    }

    @Transactional
    public CommonResponse saveSeaport(SeaportDTO seaportDTO){
        SeaportEntity seaportEntity = seaportRepository.getSeaportByCode(seaportDTO.getSeaportCode());
        if(seaportEntity != null) throw new CustomException(HttpStatus.CONFLICT, "Đã tồn tại cảng có mã: " + seaportDTO.getSeaportCode());
        seaportEntity = seaportEntityMapper.toEntity(seaportDTO);
        seaportEntity.setActive(true);
        seaportRepository.save(seaportEntity);
        if(!seaportDTO.getPropertiesMap().isEmpty()){
            keyValueService.createUpdateKeyValueOfEntity(seaportEntity.getId(), seaportDTO.getPropertiesMap(), Contants.EntityType.SEAPORT, false);
        }
        return new CommonResponse<>().success("Thêm mới thành công");
    }

    @Transactional
    public CommonResponse updateSeaport(String seaportCode ,SeaportDTO seaportDTO){
        SeaportEntity seaportEntity = seaportRepository.getSeaportByCode(seaportCode);
        if(seaportEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại cảng có mã: " + seaportCode);
        seaportEntityMapper.updateFromDTO(seaportEntity,seaportDTO);
        seaportEntity.setActive(true);
        seaportRepository.save(seaportEntity);
        if(!seaportDTO.getPropertiesMap().isEmpty()){
            keyValueService.createUpdateKeyValueOfEntity(seaportEntity.getId(), seaportDTO.getPropertiesMap(), Contants.EntityType.SEAPORT, false);
        }
        return new CommonResponse<>().success("Cập nhật thành công");
    }


    @Transactional
    public CommonResponse deleteSeaport(String seaportCode){
        SeaportEntity seaportEntity = seaportRepository.getSeaportByCode(seaportCode);
        if(seaportEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "Không tồn tại cảng có mã: " + seaportCode);
        seaportEntity.setActive(false);
        seaportRepository.save(seaportEntity);
        return new CommonResponse<>().success("Xóa thành công");
    }

}
