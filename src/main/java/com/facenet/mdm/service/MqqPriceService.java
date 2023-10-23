package com.facenet.mdm.service;

import com.facenet.mdm.domain.LeadTimeEntity;
import com.facenet.mdm.domain.MqqPriceEntity;
import com.facenet.mdm.repository.LeadTimeRepository;
import com.facenet.mdm.repository.MqqPriceRepository;
import com.facenet.mdm.service.dto.MqqLeadTimeDTO;
import com.facenet.mdm.service.dto.MqqPriceDTO;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.LeadTimeMapper;
import com.facenet.mdm.service.mapper.MqqPriceMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MqqPriceService {
    private final Logger log = LogManager.getLogger(MqqPriceService.class);
    private final MqqPriceRepository mqqPriceRepository;
    private final LeadTimeRepository leadTimeRepository;
    private final MqqPriceMapper mqqPriceMapper;
    private final LeadTimeMapper leadTimeMapper;

    public MqqPriceService(MqqPriceRepository mqqPriceRepository, LeadTimeRepository leadTimeRepository, MqqPriceMapper mqqPriceMapper, LeadTimeMapper leadTimeMapper) {
        this.mqqPriceRepository = mqqPriceRepository;
        this.leadTimeRepository = leadTimeRepository;
        this.mqqPriceMapper = mqqPriceMapper;
        this.leadTimeMapper = leadTimeMapper;
    }

    public void createMqqPrice (String vendorCode, String itemCode, MqqLeadTimeDTO dto){

        LeadTimeEntity leadtimeEntity;
        MqqPriceEntity priceEntity;

        leadtimeEntity = leadTimeMapper.dtoToEntity(vendorCode,itemCode,dto);
        log.info("-----leadtimeEntity:"+leadtimeEntity.getItemCode()+"/"+leadtimeEntity.getLeadTime());
        leadTimeRepository.save(leadtimeEntity);
        for (MqqPriceDTO mqqPriceDTO : dto.getMqqPriceList()) {
            priceEntity = mqqPriceMapper.DTOToEntity(mqqPriceDTO,vendorCode,itemCode,dto.getDueDate());
            priceEntity.setTimeEnd(dto.getDueDate());
            priceEntity.setPromotion(false);
            Integer id = priceEntity.getId();
            mqqPriceRepository.save(priceEntity);
        }
    }

    public MqqLeadTimeDTO getAllLeadTimeAndMqqPrice (String vendorCode, String itemCode){
        MqqLeadTimeDTO dto = mqqPriceRepository.findMqqPriceAndLeadTime(vendorCode, itemCode);
        return dto;
    }

    public void deleteMqqPrice(String id){
        MqqPriceEntity entity = mqqPriceRepository.findById(Integer.valueOf(id)).orElse(null);
        if (!ObjectUtils.isEmpty(entity)){
            entity.setIsActive(Byte.valueOf("2"));
            try {
                mqqPriceRepository.save(entity);
            }catch (Exception e){
                throw new CustomException("internal.error");
            }

        }else {
            log.info("Không tim thấy mqqPriceID = {}",id);
            throw new CustomException("record.notfound");
        }
    }
}
