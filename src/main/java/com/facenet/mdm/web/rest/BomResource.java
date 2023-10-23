package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.BomService;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.DataToFillBom;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bom")
public class BomResource {

    @Autowired
    BomService bomService;

    @Operation(summary = "lấy danh sách Bom mẫu")
    @PostMapping("/get-all")
    public PageResponse<List<CoittDTO>> getAllBom(@RequestBody PageFilterInput<CoittDTO> input) {
        return bomService.getAllBom(input);
    }

    @Operation(summary = "lấy danh sách Bom sản phẩm")
    @PostMapping("/get-bom-child/{bomCode}")
    public PageResponse<List<CoittDTO>> getAllBomByParent(
        @RequestBody PageFilterInput<CoittDTO> input,
        @PathVariable("bomCode") String bomCode
    ) {
        return bomService.getAllBomByParent(input, bomCode);
    }

    @Operation(summary = "Thêm mới Bom")
    @PostMapping("/new")
    public CommonResponse createBom(@RequestBody CoittDTO coittDTO) {
        return bomService.createBom(coittDTO);
    }

    @Operation(summary = "Cập nhật Bom")
    @PutMapping("/update/{productCode}")
    public CommonResponse updateBom(@PathVariable("productCode") String productCode, @RequestBody CoittDTO coittDTO) {
        return bomService.updateBom(coittDTO, productCode);
    }

    @Operation(summary = "Xóa vật liệu khỏi Bom")
    @DeleteMapping("/delete/{productCode}/{materialCode}/{version}")
    public CommonResponse deleteBom(
        @PathVariable("productCode") String productCode,
        @PathVariable("materialCode") String materialCode,
        @PathVariable("version") String version
    ) {
        return bomService.deleteMaterial(productCode, materialCode, version);
    }

    @Operation(summary = "Lấy data để fill item cho khi chọn vật liệu cho Bom")
    @PostMapping("/data-to-fill")
    public CommonResponse<List<DataToFillBom>> dataToFillBom() {
        return new CommonResponse<>().success().data(bomService.getDataToFillBom(null));
    }

    @Operation(summary = "import vật liệu cho Bom")
    @PostMapping("/import-data-to-fill")
    public CommonResponse<List<DataToFillBom>> importDataToFillBom(@RequestParam("file") MultipartFile file) throws IOException {
        return new CommonResponse<>().success().data(bomService.importDataToFillBom(file));
    }

    @Operation(summary = "get data theo Bom")
    @PostMapping("/get-data-bom/{bomParentCode}")
    public CommonResponse<List<DataToFillBom>> getDataByBom(@PathVariable("bomParentCode") String bomParentCode) {
        return new CommonResponse<>().success().data(bomService.getDataByBom(bomParentCode));
    }

    @Operation(summary = "Thay đổi trạng thái Bom")
    @PostMapping("/change-status-bom/{productCode}/{status}")
    public CommonResponse changeStatus(@PathVariable("productCode") String productCode, @PathVariable("status") Integer status) {
        return bomService.changeStatus(productCode, status);
    }

    @Operation(summary = "import bom mẫu")
    @PostMapping("/import-bom")
    public CommonResponse importBomFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return bomService.importBom(file);
    }
}
