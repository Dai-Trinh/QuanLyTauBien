package com.facenet.mdm.service;

import com.facenet.mdm.domain.Param;
import com.facenet.mdm.repository.ParamRepository;
import com.facenet.mdm.service.dto.InputParamDto;
import com.facenet.mdm.service.dto.ParamDto;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.ParamMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ParamService {

    private static final Logger logger = LoggerFactory.getLogger(ParamService.class);
    private final ParamRepository paramRepository;
    private final ParamMapper paramMapper;

    public ParamService(ParamRepository paramRepository, ParamMapper paramMapper) {
        this.paramRepository = paramRepository;
        this.paramMapper = paramMapper;
    }

    /**
     * Get params in params table
     * @param inputParamDto users' params
     * @return
     */
    public ResponseEntity<?> getParams(InputParamDto inputParamDto) {
        List<String> paramList = inputParamDto.getParams();
        if (paramList == null) throw new CustomException(HttpStatus.BAD_REQUEST, "invalid.param");

        try {
            Map<Object, List<ParamDto>> result = new HashMap<>(paramList.size());
            for (Object param : paramList) {
                List<Param> params = paramRepository.getAllByParamCode(param);
                logger.info("[{}] values: [{}]", param, params);
                result.put(param, paramMapper.toDtoList(params));
            }
            return ResponseEntity.ok(new CommonResponse<>().errorCode("00").message("Thành công").isOk(true).data(result));
        } catch (Exception e) {
            logger.error("Error when query for params", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "internal.error");
        }
    }

    public CommonResponse addParams(String paramCode, List<ParamDto> dtoList) {
        for (ParamDto p : dtoList) {
            if (!paramRepository.existsAllByParamCodeAndParamValue(paramCode, p.getParamValue())) {
                Param param = new Param();
                param.setParamCode(paramCode);
                param.setParamValue(p.getParamValue());
                param.setParamDesc(p.getParamDesc());
                paramRepository.save(param);
            }
        }
        return new CommonResponse().success();
    }

    public CommonResponse deleteParam(String paramCode, String paramValue) {
        List<Param> params = paramRepository.getAllByParamCode(paramCode);
        for (Param p : params) {
            if (p.getParamValue().equals(paramValue) || p.getParamValue() == paramValue) {
                paramRepository.delete(p);
                return new CommonResponse().success();
            }
        }
        throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
    }
}
