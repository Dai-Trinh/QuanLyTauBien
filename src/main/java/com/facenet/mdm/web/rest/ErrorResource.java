package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.ErrorService;
import com.facenet.mdm.service.dto.ErrorDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.model.*;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/")
public class ErrorResource {

    @Autowired
    ErrorService errorService;

    @Operation(summary = "Lấy danh sách lỗi")
    @PostMapping({ "/errors" })
    public CommonResponse getErrorWithPaging(@RequestBody PageFilterInput<ErrorFilter> errorInput) {
        return errorService.getErrorWithPaging(errorInput);
    }

    @Operation(summary = "Auto complete lỗi")
    @PostMapping({ "/errors/auto-complete" })
    public CommonResponse<List<String>> getAutoComplete(@RequestBody PageFilterInput<ErrorFilter> errorInput) {
        return new CommonResponse<List<String>>().success().data(errorService.getAutoComplete(errorInput));
    }

    @Operation(summary = "Thêm mới lỗi")
    @PostMapping("error/new")
    public CommonResponse createError(@RequestBody ErrorDTO errorDTO) {
        return errorService.createError(errorDTO);
    }

    @Operation(summary = "Cập nhật lỗi")
    @PutMapping("error/{erorrCode}")
    public CommonResponse updateError(@PathVariable("erorrCode") String errorCode, @RequestBody ErrorDTO errorDTO) {
        return errorService.updateError(errorCode, errorDTO);
    }

    @Operation(summary = "Thêm mới cập nhật danh sách lỗi")
    @PostMapping("/error/add-list-error")
    public CommonResponse addListError(@RequestBody(required = false) List<ErrorDTO> errorDTOList) {
        return errorService.saveorUpdateListError(errorDTOList);
    }

    @Operation(summary = "Xóa lỗi")
    @DeleteMapping("/delete-error/{errorCode}")
    public CommonResponse deleteError(@PathVariable("errorCode") String errorCode) {
        return errorService.deleteError(errorCode);
    }

    @Operation(summary = "Import lỗi")
    @PostMapping({ "/error/import-error" })
    public CommonResponse importErrorFromExcel(@RequestParam("file") MultipartFile file) throws IOException, ParseException {
        return errorService.importErrorFromExcel(file);
    }
}
