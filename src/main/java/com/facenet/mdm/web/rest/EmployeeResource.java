package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.EmployeeService;
import com.facenet.mdm.service.dto.EmployeeDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.model.EmployeeFilter;
import com.facenet.mdm.service.model.EmployeeInput;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class EmployeeResource {

    @Autowired
    EmployeeService employeeService;

    @Operation(summary = "Lấy danh sách nhân viên")
    @PostMapping("/employee")
    public CommonResponse getAllEmployee(@RequestBody PageFilterInput<EmployeeDTO> employeeInput) {
        return employeeService.getAllEmployee(employeeInput);
    }

    @Operation(summary = "auto complete nhân viên")
    @PostMapping("/employee/auto-complete")
    public CommonResponse<List<String>> getAutoCompleteEmployee(@RequestBody PageFilterInput<EmployeeDTO> input) {
        return new CommonResponse().success().data(employeeService.getAutoCompleteEmployee(input));
    }

    @Operation(summary = "Lấy danh sách nhân viên theo nhóm tổ")
    @PostMapping("/employee/team-group/{teamGroupCode}")
    public CommonResponse<List<EmployeeDTO>> getAllEmployeeByTeamGroupCode(@PathVariable("teamGroupCode") String teamGroupCode) {
        return employeeService.getEmployeeByTeamGroupCode(teamGroupCode);
    }

    @Operation(summary = "Thêm mới nhân viên")
    @PostMapping("/employee/new")
    public CommonResponse createEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return employeeService.createEmployee(employeeDTO);
    }

    @Operation(summary = "Thêm mới danh sách nhân viên")
    @PostMapping("/employee/new-list")
    public CommonResponse createOrUpdate(@RequestBody List<EmployeeDTO> employeeDTOList) {
        return employeeService.createOrUpdate(employeeDTOList);
    }

    @Operation(summary = "Cập nhật nhân viên")
    @PutMapping("/employee/{employeeCode}")
    public CommonResponse updateEmployee(@PathVariable("employeeCode") String employeeCode, @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.updateEmployee(employeeCode, employeeDTO);
    }

    @Operation(summary = "Xóa nhân viên")
    @DeleteMapping("/employee/delete/{employeeCode}")
    public CommonResponse deleteEmployee(@PathVariable("employeeCode") String employeeCode) {
        return employeeService.deleteEmployee(employeeCode);
    }

    @Operation(summary = "Xóa nhân viên khỏi nhóm tổ")
    @DeleteMapping("/employee/delete-team-group/{employeeCode}")
    public CommonResponse deleteEmployeeTeamGroup(@PathVariable("employeeCode") String employeeCode) {
        return employeeService.deleteEmployeeTeamGroup(employeeCode);
    }

    @Operation(summary = "Import nhân viên")
    @PostMapping("/employee/import_employee")
    public CommonResponse importEmployeeFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return employeeService.importEmployeeFromExcel(file);
    }
}
