package com.facenet.mdm.web.rest;

import com.facenet.mdm.domain.CoittEntity;
import com.facenet.mdm.service.Citt1Service;
import com.facenet.mdm.service.CoittService;
import com.facenet.mdm.service.MaterialReplacementService;
import com.facenet.mdm.service.dto.CoittDTO;
import com.facenet.mdm.service.dto.VendorDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.utils.Contants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
public class ProductResource {

    @Autowired
    Citt1Service citt1Service;

    @Autowired
    CoittService coittService;

    @Autowired
    MaterialReplacementService materialReplacementService;

    @Operation(summary = "Thêm mới vật tư")
    @PostMapping("/new")
    public CommonResponse createProduct(@RequestBody CoittDTO coittDTO) {
        if (coittDTO.getItemGroupCode() == Contants.ItemGroup.NVL) {
            return citt1Service.createProduct(coittDTO);
        }
        return coittService.createProduct(coittDTO);
    }

    @Operation(summary = "Lấy danh sách vật tư")
    @PostMapping("")
    public CommonResponse<List<CoittDTO>> getList(@RequestBody PageFilterInput<CoittDTO> input) {
        return coittService.getProductList(input);
    }

    @Operation(summary = "Cập nhật vật tư")
    @PutMapping("/{productCode}")
    public CommonResponse updateProduct(@RequestBody CoittDTO coittDTO, @PathVariable("productCode") String productCode) {
        if (coittDTO.getItemGroupCode() == Contants.ItemGroup.NVL) {
            return citt1Service.updatedProduct(coittDTO, productCode);
        }
        return coittService.updatedProduct(coittDTO, productCode);
    }

    @Operation(summary = "Xóa vật tư")
    @DeleteMapping("/{productCode}")
    public CommonResponse deleteProduct(@PathVariable String productCode) {
        return coittService.deleteProduct(productCode);
    }

    @GetMapping("/material-for-product/{productCode}/{materialCode}")
    public CommonResponse addMaterialForProduct(@PathVariable String productCode, @PathVariable String materialCode) {
        return citt1Service.addMaterialForProduct(productCode, materialCode);
    }

    @DeleteMapping("/material-for-product/{productCode}/{materialCode}")
    public CommonResponse removeMaterialForProduct(@PathVariable String productCode, @PathVariable String materialCode) {
        return citt1Service.removeMaterialForProduct(productCode, materialCode);
    }

    @Operation(summary = "import vật tư")
    @PostMapping("/import-product/{itemGroup}")
    public CommonResponse importProduct(@RequestParam("file") MultipartFile file, @PathVariable Integer itemGroup)
        throws IOException, ParseException {
        if (itemGroup == Contants.ItemGroup.NVL) {
            return citt1Service.importProduct(file, itemGroup);
        }
        return coittService.importProduct(file, itemGroup);
    }

    @Operation(summary = "Thêm vật tư thay thế")
    @PostMapping("/material-replacement/{materialCode}")
    public CommonResponse addMaterialReplacement(@PathVariable String materialCode, @RequestBody List<String> replacementMaterialCode) {
        materialReplacementService.addMaterialReplacement(materialCode, replacementMaterialCode);
        return new CommonResponse().success();
    }

    @Operation(summary = "Xóa vật tư thay thế khỏi vật tư")
    @DeleteMapping("/material-replacement/{materialCode}/{materialReplacementCode}")
    public CommonResponse removeMaterialReplacement(@PathVariable String materialCode, @PathVariable String materialReplacementCode) {
        return materialReplacementService.removeMaterialReplacement(materialCode, materialReplacementCode);
    }

    @Operation(summary = "Lấy danh sách vật tư thay thế")
    @PostMapping("/material-replacement-list")
    public CommonResponse<List<CoittEntity>> getMaterialReplacementList(@RequestBody PageFilterInput<String> input) {
        return materialReplacementService.getMaterialReplacementList(input);
    }

    @Operation(summary = "Auto complete vật tư")
    @PostMapping("/auto-complete-common")
    public CommonResponse<List<String>> autoComplete(@RequestBody PageFilterInput<CoittDTO> input) {
        if (input.getFilter().getItemGroupCode() == Contants.ItemGroup.NVL) {
            return new CommonResponse<List<String>>().success().data(citt1Service.autocompleteCommon(input));
        }
        return new CommonResponse<List<String>>().success().data(coittService.autocompleteCommon(input));
    }

    @Operation(summary = "Lấy danh sách hàng hóa theo mã hàng hóa")
    @PostMapping("/get-by-merchandisegroup/{merchandiseGroup}")
    public CommonResponse<List<CoittDTO>> getByMerchandiseGroup(@PathVariable("merchandiseGroup") String merchandiseGroup) {
        return coittService.getAllCoittByMerchandiseGroupCode(merchandiseGroup);
    }

    @Operation(summary = "Cập nhật danh sách hàng hóa")
    @PutMapping("/update-list")
    public CommonResponse updateListCoitt(@RequestBody List<CoittDTO> coittDTOList) {
        return coittService.updateListCoitt(coittDTOList);
    }

    @Operation(summary = "Xóa hàng hóa khỏi nhóm hàng hóa")
    @DeleteMapping("/delete/merchandise-group/{coittCode}")
    public CommonResponse deleteCoittMerchandisGroup(
        @PathVariable("coittCode") @Parameter(name = "coittCode", description = "mã vật tư", example = "VT01") String coittCode
    ) {
        return coittService.deleteCoittMerchandiseGroup(coittCode);
    }

    @Operation(summary = "Lấy vật tư theo mã")
    @GetMapping("/{productCode}")
    public CommonResponse<CoittDTO> getProductByCode(@PathVariable("productCode") String productCode) {
        return new CommonResponse<>().data(coittService.getProductByCode(productCode)).success();
    }

    @Operation(summary = "Lấy thành phẩm, bán thành phẩm")
    @GetMapping("/get-tp-btp")
    public CommonResponse<List<CoittDTO>> getTPAndBTP() {
        return new CommonResponse<>().success().data(coittService.getTPAndBTP());
    }
}
