package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.DetailVendorService;
import com.facenet.mdm.service.VendorService;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/vendors")
public class VendorResource {

    @Autowired
    VendorService vendorService;

    @Autowired
    DetailVendorService detailVendorService;

    @Operation(summary = "Lấy danh sách nhà cung cấp")
    @PostMapping("")
    public CommonResponse<VendorDTO> getVendorList(@RequestBody PageFilterInput<VendorDTO> input) {
        return vendorService.getVendorList(input);
    }

    @Operation(summary = "Thêm mới nhà cung cấp")
    @PostMapping("/new")
    public CommonResponse createVendor(@RequestBody VendorDTO vendorDTO) {
        return vendorService.createVendor(vendorDTO);
    }

    @Operation(summary = "Cập nhật nhà cung cấp")
    @PutMapping("/{vendorCode}")
    public CommonResponse updateVendor(@RequestBody VendorDTO vendorDTO, @PathVariable String vendorCode) {
        return vendorService.updateVendor(vendorDTO, vendorCode);
    }

    @Operation(summary = "Xóa nhà cung cấp")
    @DeleteMapping("/{vendorCode}")
    public CommonResponse delete(@PathVariable String vendorCode) {
        return vendorService.deleteVendor(vendorCode);
    }

    @Operation(summary = "Import nhà cung cấp")
    @PostMapping("/import-vendors")
    public CommonResponse importVendorInfo(@RequestParam("file") MultipartFile file) throws IOException, ParseException {
        return vendorService.importInfo(file);
    }

    @Operation(summary = "Thêm mới vật tư cho nhà cung cấp")
    @GetMapping("/{vendorCode}/{itemCode}")
    public CommonResponse addItemForVendor(@PathVariable String vendorCode, @PathVariable String itemCode) {
        return detailVendorService.addItemForVendor(vendorCode, itemCode);
    }

    @Operation(summary = "Lấy danh sách vật tư cho nhà cung cấp")
    @PostMapping("/item-list/{vendorCode}")
    public CommonResponse<List<DataItemInVendor>> getItemListOfVendor(
        @RequestBody PageFilterInput<DataItemInVendor> pageForm,
        @PathVariable String vendorCode
    ) throws JsonProcessingException {
        return detailVendorService.getAllData(pageForm, vendorCode);
    }

    @Operation(summary = "Xóa vật tư khỏi nhà cung cấp")
    @DeleteMapping("/{vendorCode}/{itemCode}")
    public CommonResponse removeItem(@PathVariable String vendorCode, @PathVariable String itemCode) {
        return detailVendorService.removeItem(vendorCode, itemCode);
    }

    @Operation(summary = "Import vật tư")
    @PostMapping("/import-vendor-item")
    public CommonResponse importVendorItem(@RequestParam("file") MultipartFile file) throws IOException, ParseException {
        return detailVendorService.importPrice(file);
    }

    @Operation(summary = "Auto complete nhà cung cấp")
    @PostMapping("/auto-complete-common")
    public CommonResponse<List<String>> autoComplete(@RequestBody PageFilterInput<VendorDTO> input) {
        return new CommonResponse<List<String>>().success().data(vendorService.autocompleteCommon(input));
    }

    @PostMapping("/item-lists")
    public PageResponse<List<CoittDTO>> getAllItemForVendor() {
        return vendorService.getAllItemForVendor();
    }
}
