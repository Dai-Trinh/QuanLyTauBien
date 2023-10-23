package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.MerchandiseGroupService;
import com.facenet.mdm.service.dto.MerchandiseGroupDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/merchandise_group")
public class MerchandiseGroupResource {

    @Autowired
    MerchandiseGroupService merchandiseGroupService;

    @Operation(summary = "Lấy danh sách hàng hóa")
    @PostMapping("")
    public PageResponse<List<MerchandiseGroupDTO>> getAllMerchandiseGroup(@RequestBody PageFilterInput<MerchandiseGroupDTO> input) {
        return merchandiseGroupService.getAllMerchandiseGroup(input);
    }

    @Operation(summary = "auto-complete hàng hóa")
    @PostMapping("/auto_complete")
    public List<String> getAutoComplete(@RequestBody PageFilterInput<MerchandiseGroupDTO> input) {
        return merchandiseGroupService.getAutoCompleteMerchandiseGroup(input);
    }

    @Operation(summary = "Thêm mới hàng hóa")
    @PostMapping("/new")
    public CommonResponse createMerchandisGroup(@RequestBody MerchandiseGroupDTO merchandiseGroupDTO) {
        return merchandiseGroupService.createMerchandiseGroup(merchandiseGroupDTO);
    }

    @Operation(summary = "Cập nhật hàng hóa")
    @PutMapping("/{merchandiseGroupCode}")
    public CommonResponse updateMerchandiseGroup(
        @PathVariable("merchandiseGroupCode") @Parameter(
            name = "merchandiseGroupCode",
            description = "mã nhóm hàng hóa",
            example = "HH01"
        ) String merchandiseGroupCode,
        @RequestBody MerchandiseGroupDTO merchandiseGroupDTO
    ) {
        return merchandiseGroupService.updateMerchandiseGroup(merchandiseGroupCode, merchandiseGroupDTO);
    }

    @Operation(summary = "Xóa nhóm hàng hóa")
    @DeleteMapping("/delete/{merchandiseGroupCode}")
    public CommonResponse deleteMerchandiseGroup(
        @PathVariable("merchandiseGroupCode") @Parameter(
            name = "merchandiseGroupCode",
            description = "mã nhóm hàng hóa",
            example = "HH01"
        ) String merchandiseGroupCode
    ) {
        return merchandiseGroupService.deleteMerchandiseGroup(merchandiseGroupCode);
    }

    @Operation(summary = "export nhóm hàng hóa")
    @PostMapping("/export")
    public CommonResponse<List<MerchandiseGroupDTO>> exportMerchandiseGroup() {
        return new CommonResponse<List<MerchandiseGroupDTO>>().success().data(merchandiseGroupService.exportMerchandiseGroup());
    }

    @Operation(summary = "import hàng hóa")
    @PostMapping("/import_excel")
    public CommonResponse importMerchandiseGroupFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return merchandiseGroupService.importMerchandiseGroupFromExcel(file);
    }
}
