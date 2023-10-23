package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.CustomerService;
import com.facenet.mdm.service.dto.CustomerDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customers")
public class CustomerResource {

    @Autowired
    CustomerService customerService;

    @Operation(summary = "Lấy danh sách khách hàng")
    @PostMapping("")
    public CommonResponse search(@RequestBody PageFilterInput<CustomerDTO> input) {
        return customerService.getList(input);
    }

    @Operation(summary = "Thêm mới khách hàng")
    @PostMapping("/new")
    public CommonResponse addProductionStage(@RequestBody CustomerDTO customerDTO) {
        return customerService.addCustomer(customerDTO);
    }

    @Operation(summary = "Cập nhật thông tin khách hàng")
    @PutMapping("/{customerCode}")
    public CommonResponse updateProductionStage(@RequestBody CustomerDTO customerDTO, @PathVariable String customerCode) {
        return customerService.updateCustomer(customerDTO, customerCode);
    }

    @Operation(summary = "Xóa khách hàng")
    @DeleteMapping("/{customerCode}")
    public CommonResponse deleteProdutionStage(@PathVariable String customerCode) {
        return customerService.deleteCustomer(customerCode);
    }

    @Operation(summary = "import khách hàng")
    @PostMapping("/import")
    public CommonResponse importStage(@RequestParam("file") MultipartFile file) throws IOException, ParseException {
        return customerService.importInfo(file);
    }

    @Operation(summary = "auto-complete khách hàng")
    @PostMapping("/auto-complete-common")
    public CommonResponse<List<String>> autocomplete(@RequestBody PageFilterInput<CustomerDTO> input) {
        return new CommonResponse<List<String>>().success().data(customerService.autocompleteCommon(input));
    }
}
