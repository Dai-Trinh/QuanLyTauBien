package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.MachineService;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import com.facenet.mdm.service.model.PageFilterInputMap;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/machines")
public class MachineResource {

    private final MachineService machineService;

    public MachineResource(MachineService machineService) {
        this.machineService = machineService;
    }

    @Operation(summary = "Lấy danh sách máy móc")
    @PostMapping("")
    public PageResponse<List<MachineDTO>> getAllMachine(@RequestBody PageFilterInput<MachineDTO> filterInput) {
        return machineService.getAllMachine(filterInput);
    }

    @GetMapping("/auto-complete/{keyName}")
    public CommonResponse<List<String>> getAutoCompleteMachine(@PathVariable String keyName, @RequestParam String value) {
        return new CommonResponse<List<String>>().success().data(machineService.getAutoComplete(keyName, value));
    }

    @Operation(summary = "Autocomplete máy móc")
    @PostMapping("/common-autocomplete")
    public CommonResponse<Set<String>> getAutoCompleteMachine(@RequestBody PageFilterInput<MachineDTO> filterInput) {
        return new CommonResponse<Set<String>>().success().data(machineService.getForCommonSearch(filterInput));
    }

    @Operation(summary = "Thêm mới máy móc")
    @PostMapping("/new")
    public CommonResponse createMachine(@RequestBody MachineDTO input) {
        machineService.createMachine(input);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "Cập nhật máy móc")
    @PutMapping("/{machineCode}")
    public CommonResponse updateMachine(@PathVariable String machineCode, @RequestBody MachineDTO input) {
        machineService.updateMachine(input, machineCode);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "Xóa máy móc")
    @DeleteMapping("/{machineCode}")
    public CommonResponse deleteMachine(@PathVariable String machineCode) {
        machineService.deleteMachine(machineCode);
        return new CommonResponse<>().success();
    }

    @Operation(summary = "Import máy móc")
    @PostMapping("/import-excel")
    public CommonResponse importExcelMachine(@RequestParam("file") MultipartFile file) throws IOException {
        machineService.importExcel(file);
        return new CommonResponse<>().success();
    }
}
