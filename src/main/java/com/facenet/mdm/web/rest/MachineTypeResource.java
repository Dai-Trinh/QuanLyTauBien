package com.facenet.mdm.web.rest;

import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.domain.MachineTypeEntity;
import com.facenet.mdm.repository.MachineTypeRepository;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machine-types")
public class MachineTypeResource {

    private final MachineTypeRepository machineTypeEntityRepository;

    public MachineTypeResource(MachineTypeRepository machineTypeEntityRepository) {
        this.machineTypeEntityRepository = machineTypeEntityRepository;
    }

    @Operation(summary = "Lấy danh sách loại máy móc")
    @GetMapping("")
    public CommonResponse<List<MachineEntity>> getAll() {
        return new CommonResponse<>().success().data(machineTypeEntityRepository.findAll());
    }

    @Operation(summary = "Thêm mới loại máy móc")
    @PostMapping("")
    public CommonResponse createType(@RequestBody MachineTypeEntity machineType) {
        if (
            machineTypeEntityRepository.findByMachineTypeNameIgnoreCase(machineType.getMachineTypeName()) != null
        ) throw new CustomException(HttpStatus.CONFLICT, "duplicate", machineType.getMachineTypeName());
        machineTypeEntityRepository.save(machineType);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "Cập nhật loại máy móc")
    @PutMapping("/{id}")
    public CommonResponse createType(@PathVariable Integer id, @RequestBody MachineTypeEntity input) {
        MachineTypeEntity machineTypeEntity = machineTypeEntityRepository.findById(id).orElse(null);
        if (machineTypeEntity == null) throw new CustomException(HttpStatus.BAD_REQUEST, "record.notfound");
        machineTypeEntity.setMachineTypeName(input.getMachineTypeName());
        machineTypeEntityRepository.save(machineTypeEntity);
        return new CommonResponse<>().success();
    }
}
