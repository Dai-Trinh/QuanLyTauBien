package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.ErrorDetailService;
import com.facenet.mdm.service.dto.ErrorDTO;
import com.facenet.mdm.service.dto.ErrorDetailDTO;
import com.facenet.mdm.service.dto.ErrorGroupDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.model.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ErrorDetailResource {

    @Autowired
    ErrorDetailService errorDetailService;

    @Operation(summary = "Lấy danh sách lỗi theo nhóm lỗi")
    @PostMapping("/error_detail/{erorrGroupCode}")
    public CommonResponse<List<ErrorDTO>> getErrorDetail(@PathVariable("erorrGroupCode") String erorrGroupCode) {
        return errorDetailService.getErrorGroupByError(erorrGroupCode);
    }

    @Operation(summary = "Map error với errorGroup")
    @PostMapping("/error_detail/new/{errorCode}/{errorGroupCode}")
    public CommonResponse createErrorDetail(
        @PathVariable("errorCode") String errorCode,
        @PathVariable("errorGroupCode") String errorGroupCode
    ) {
        return errorDetailService.createErrorWithErrorGroup(errorCode, errorGroupCode);
    }

    @PutMapping("/error_detail/update/{errorCode}/{errorGroupCode}")
    public CommonResponse updateErrorDetail(
        @PathVariable("errorCode") String errorCode,
        @PathVariable("errorGroupCode") String errorGroupCode,
        @RequestBody ErrorDetailDTO errorDetailDTO
    ) {
        return errorDetailService.updateErrorDetail(errorCode, errorGroupCode, errorDetailDTO);
    }

    @Operation(summary = "Xóa lỗi khỏi nhóm lỗi")
    @DeleteMapping("/error_group_detail/delete/{errorCode}/{errorGroupCode}")
    public CommonResponse deleteErrorDetail(
        @PathVariable("errorCode") String errorCode,
        @PathVariable("errorGroupCode") String errorGroupCode
    ) {
        return errorDetailService.deleteErrorDetail(errorCode, errorGroupCode);
    }
}
