package com.facenet.mdm.web.rest;

import com.facenet.mdm.domain.BusinessLogDetailEntity;
import com.facenet.mdm.domain.BusinessLogEntity;
import com.facenet.mdm.service.BusinessLogService;
import com.facenet.mdm.service.dto.BusinessLogDTO;
import com.facenet.mdm.service.dto.BusinessLogDetailDTO;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/change-log")
public class BusinessLogResource {

    private final BusinessLogService businessLogService;

    public BusinessLogResource(BusinessLogService businessLogService) {
        this.businessLogService = businessLogService;
    }

    @PostMapping("")
    public PageResponse<List<BusinessLogEntity>> getAllChangeLog(@RequestBody PageFilterInput<BusinessLogDTO> filter) {
        Page<BusinessLogEntity> result = businessLogService.getAllChangeLog(filter);
        return new PageResponse<List<BusinessLogEntity>>().success().data(result.getContent()).dataCount(result.getTotalElements());
    }

    @PostMapping("/{id}")
    public PageResponse<List<BusinessLogDetailDTO>> getAllChangeLogDetail(
        @RequestBody PageFilterInput<BusinessLogDetailDTO> filter,
        @PathVariable Long id
    ) {
        Page<BusinessLogDetailDTO> result = businessLogService.getChangeLogDetail(id, filter);
        return new PageResponse<List<BusinessLogDetailDTO>>().success().data(result.getContent()).dataCount(result.getTotalElements());
    }

    @PostMapping("/common-autocomplete")
    public CommonResponse getAutocomplete(@RequestBody PageFilterInput<BusinessLogDTO> input) {
        return new CommonResponse().success().data(businessLogService.getCommonAutocomplete(input));
    }
}
