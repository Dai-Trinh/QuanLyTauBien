package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.MachineService;
import com.facenet.mdm.service.ProductionLineService;
import com.facenet.mdm.service.dto.ProductionLineDTO;
import com.facenet.mdm.service.dto.TeamGroupDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.dto.response.Result;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.model.PageFilterInputMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.List;
import liquibase.exception.CustomChangeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/production-line")
public class ProductionLineResouce {

    @Autowired
    ProductionLineService productionLineService;

    @Operation(summary = "Lấy danh sách dây chuyển")
    @PostMapping("")
    public PageResponse<List<ProductionLineDTO>> getAllProductionLine(@RequestBody PageFilterInput<ProductionLineDTO> filterInput) {
        return productionLineService.getAllProductionLine(filterInput);
    }

    @Operation(summary = "Auto complete dây chuyền")
    @PostMapping("/auto-complete")
    public CommonResponse<List<String>> getAutoCompleteProductionLine(@RequestBody PageFilterInput<ProductionLineDTO> input) {
        return new CommonResponse<List<String>>().success().data(productionLineService.getAutoCompleteProductionLine(input));
    }

    @Operation(summary = "Thêm mới dây chuyền")
    @PostMapping("/new")
    public CommonResponse createProductionLine(@RequestBody ProductionLineDTO input) {
        productionLineService.createProductionLine(input);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "Cập nhập dây chuyền")
    @PutMapping("/{productionLineCode}")
    public CommonResponse updateProductionLine(@PathVariable String productionLineCode, @RequestBody ProductionLineDTO input) {
        productionLineService.updateProductionLine(input, productionLineCode);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "Xóa dây chuyền")
    @DeleteMapping("/{productionLineCode}")
    public CommonResponse deleteProductionLine(@PathVariable String productionLineCode) {
        productionLineService.deleteProductionLine(productionLineCode);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "import dây chuyền")
    @PostMapping("/import-excel")
    public CommonResponse importExcelProductionLine(@RequestParam("file") MultipartFile file) throws IOException {
        productionLineService.importExcel(file);
        return new CommonResponse<>().success();
    }
}
