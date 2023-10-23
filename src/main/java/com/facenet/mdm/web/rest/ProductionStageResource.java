package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.ProductionStageService;
import com.facenet.mdm.service.dto.JobDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.StageQmsDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductionStageResource {

    @Autowired
    ProductionStageService productionStageService;

    @Operation(summary = "Lấy danh sách công đoạn")
    @PostMapping("/stages")
    public CommonResponse<List<ProductionStageDTO>> search(@RequestBody PageFilterInput<ProductionStageDTO> input) {
        return productionStageService.getListProductionStage(input);
    }

    @Operation(summary = "Thêm mới công đoạn")
    @PostMapping("/stages/new")
    public CommonResponse addProductionStage(@RequestBody ProductionStageDTO productionStageDTO) {
        return productionStageService.createProductionStage(productionStageDTO);
    }

    @Operation(summary = "Cập nhật công đoạn")
    @PutMapping("/stages/{productionStageCode}")
    public CommonResponse updateProductionStage(
        @RequestBody ProductionStageDTO productionStageDTO,
        @PathVariable String productionStageCode
    ) {
        return productionStageService.updateProductionStage(productionStageDTO, productionStageCode);
    }

    @Operation(summary = "Xóa công đoạn")
    @DeleteMapping("/stages/{productionStageCode}")
    public CommonResponse deleteProdutionStage(@PathVariable String productionStageCode) {
        return productionStageService.deleteProductionStage(productionStageCode);
    }

    @Operation(summary = "Import công đoạn")
    @PostMapping("/stages/import-stage")
    public CommonResponse importStage(@RequestParam("file") MultipartFile file) throws IOException, ParseException {
        return productionStageService.importExcel(file);
    }

    @PostMapping("/qms/stages")
    public List<StageQmsDTO> stageQmsDTOList(@RequestBody PageFilterInput<StageQmsDTO> stageQmsDTO) {
        return productionStageService.getListForQms(stageQmsDTO);
    }

    @Operation(summary = "Auto complete common công đoạn")
    @PostMapping("/stages/auto-complete-common")
    public CommonResponse<List<String>> autoComplete(@RequestBody PageFilterInput<JobDTO> input) {
        return new CommonResponse<List<String>>().success().data(productionStageService.autocompleteCommon(input));
    }

    @Operation(summary = "export công đoạn")
    @PostMapping("/stages/export")
    public CommonResponse<List<ProductionStageDTO>> exportProductionStage() {
        return new CommonResponse<>().success().data(productionStageService.exportProductionStage());
    }
}
