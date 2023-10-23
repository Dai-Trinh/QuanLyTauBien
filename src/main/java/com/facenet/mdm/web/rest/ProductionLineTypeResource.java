package com.facenet.mdm.web.rest;

import com.facenet.mdm.domain.ProductionLineTypeEntity;
import com.facenet.mdm.repository.ProductionLineTypeRepository;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production-line-types")
public class ProductionLineTypeResource {

    private final ProductionLineTypeRepository productionLineTypeRepository;

    public ProductionLineTypeResource(ProductionLineTypeRepository productionLineTypeRepository) {
        this.productionLineTypeRepository = productionLineTypeRepository;
    }

    @Operation(summary = "Lấy danh sách loại dây chuyền")
    @GetMapping("")
    public CommonResponse<ProductionLineTypeEntity> getAll() {
        return new CommonResponse<>().success().data(productionLineTypeRepository.findAll());
    }

    @Operation(summary = "Thêm mới loại dây chuyền")
    @PostMapping("")
    public CommonResponse createType(@RequestBody ProductionLineTypeEntity input) {
        if (
            productionLineTypeRepository.findByProductionLineTypeNameIgnoreCase(input.getProductionLineTypeName()) != null
        ) throw new CustomException(HttpStatus.CONFLICT, "duplicate", input.getProductionLineTypeName());
        productionLineTypeRepository.save(input);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "Cập nhật loại dây chuyền")
    @PutMapping("/{id}")
    public CommonResponse createType(@PathVariable Integer id, @RequestBody ProductionLineTypeEntity input) {
        ProductionLineTypeEntity productionLineTypeEntity = productionLineTypeRepository.findById(id).orElse(null);
        if (productionLineTypeEntity == null) throw new CustomException(HttpStatus.BAD_REQUEST, "record.notfound");
        productionLineTypeEntity.setProductionLineTypeName(input.getProductionLineTypeName());
        productionLineTypeRepository.save(productionLineTypeEntity);
        return new CommonResponse<>().success();
    }
}
