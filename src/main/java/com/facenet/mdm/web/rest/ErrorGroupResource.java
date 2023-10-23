package com.facenet.mdm.web.rest;

import com.facenet.mdm.domain.ErrorGroupEntity;
import com.facenet.mdm.service.ErrorGroupService;
import com.facenet.mdm.service.dto.ErrorGroupDTO;
import com.facenet.mdm.service.dto.ErrorGroupQMSDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.model.ErrorGroupFilter;
import com.facenet.mdm.service.model.ErrorGroupInput;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ErrorGroupResource {

    @Autowired
    ErrorGroupService errorGroupService;

    @Operation(summary = "Lấy danh sách nhóm lỗi")
    @PostMapping({ "/errorgroup" })
    public CommonResponse<List<ErrorGroupDTO>> getErrorGroupWithPaging(@RequestBody PageFilterInput<ErrorGroupFilter> errorGroupInput) {
        return errorGroupService.getErrorGroupWithPaging(errorGroupInput);
    }

    @Operation(summary = "Auto complete nhóm lỗi")
    @PostMapping("/errorgroup/auto-complete")
    public CommonResponse<List<String>> getAutoComplete(@RequestBody PageFilterInput<ErrorGroupDTO> input) {
        return new CommonResponse<List<String>>().success().data(errorGroupService.getAutoComplete(input));
    }

    @Operation(summary = "Thêm mới nhóm lỗi")
    @PostMapping("/errorgroup/new")
    public CommonResponse createErrorGroup(@RequestBody ErrorGroupDTO errorGroupDTO) {
        return errorGroupService.newErrorGroup(errorGroupDTO);
    }

    @Operation(summary = "Cập nhật nhóm lỗi")
    @PutMapping("errorgroup/{errorGroupCode}")
    public CommonResponse updateErrorGroup(
        @PathVariable("errorGroupCode") String errorGroupCode,
        @RequestBody ErrorGroupDTO errorGroupDTO
    ) {
        return errorGroupService.updateErrorGroup(errorGroupCode, errorGroupDTO);
    }

    @Operation(summary = "Xóa nhóm lỗi")
    @DeleteMapping("/delete-error-group/{erorrGroupCode}")
    public CommonResponse deleteErrorGroup(@PathVariable("erorrGroupCode") String erorrGroupCode) {
        return errorGroupService.deleteErrorGroup(erorrGroupCode);
    }

    @Operation(summary = "Import nhóm lỗi")
    @PostMapping("/error_group/import_errorgroup")
    public CommonResponse importErrorGroupFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return errorGroupService.importErrorGroupFromExcel(file);
    }

    @Operation(summary = "Lấy lỗi cho QMS")
    @PostMapping("/qms/errorlist")
    public List<ErrorGroupQMSDTO> getErrorListForQMS(@RequestBody PageFilterInput<ErrorGroupQMSDTO> errorGroupQMSDTO) {
        return errorGroupService.getListErrorForQMS(errorGroupQMSDTO);
    }
}
