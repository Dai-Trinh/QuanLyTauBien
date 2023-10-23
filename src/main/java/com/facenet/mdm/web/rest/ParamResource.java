package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.ParamService;
import com.facenet.mdm.service.dto.InputParamDto;
import com.facenet.mdm.service.dto.ParamDto;
import com.facenet.mdm.service.dto.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ParamResource {

    private final ParamService paramService;

    public ParamResource(ParamService paramService) {
        this.paramService = paramService;
    }

    @Operation(summary = "Lấy danh sách param")
    @PostMapping("/params")
    public ResponseEntity<?> getParams(@RequestBody InputParamDto params) {
        return paramService.getParams(params);
    }

    @Operation(summary = "Thêm mới param")
    @PostMapping("/params/new/{paramCode}")
    public CommonResponse addParams(@PathVariable String paramCode, @RequestBody List<ParamDto> dtoList) {
        return paramService.addParams(paramCode, dtoList);
    }

    @Operation(summary = "Xóa param")
    @DeleteMapping("/params/{paramCode}/{paramValue}")
    public CommonResponse deleteParams(@PathVariable String paramCode, @PathVariable String paramValue) {
        return paramService.deleteParam(paramCode, paramValue);
    }
}
