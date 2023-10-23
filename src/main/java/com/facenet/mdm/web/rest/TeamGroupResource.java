package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.TeamGroupService;
import com.facenet.mdm.service.dto.TeamGroupDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class TeamGroupResource {

    @Autowired
    TeamGroupService teamGroupService;

    @Operation(summary = "Lấy danh sách nhóm tổ")
    @PostMapping("/team-group")
    public CommonResponse<List<TeamGroupDTO>> getAllTeamGroup(@RequestBody PageFilterInput<TeamGroupDTO> input) {
        return teamGroupService.getAllTeamGroup(input);
    }

    @Operation(summary = "Auto complete nhóm tổ")
    @PostMapping("/team-group/auto-complete")
    public CommonResponse<List<String>> getAutoCompleteTeamGroup(@RequestBody PageFilterInput<TeamGroupDTO> input) {
        return new CommonResponse<List<String>>().success().data(teamGroupService.getAutoCompleteTeamGroup(input));
    }

    @Operation(summary = "Thêm mới nhóm tổ")
    @PostMapping("/team-group/new")
    public CommonResponse createTeamGroup(@RequestBody TeamGroupDTO teamGroupDTO) {
        return teamGroupService.createTeamGroup(teamGroupDTO);
    }

    @Operation(summary = "Cập nhật nhóm tổ")
    @PutMapping("/team-group/{teamGroupCode}")
    public CommonResponse updateTeamGroup(@PathVariable("teamGroupCode") String teamGroupCode, @RequestBody TeamGroupDTO teamGroupDTO) {
        return teamGroupService.updateTeamGroup(teamGroupCode, teamGroupDTO);
    }

    @Operation(summary = "Xóa nhóm tổ")
    @DeleteMapping("team-group/delete/{teamGroupCode}")
    public CommonResponse deleteTeamGroup(@PathVariable("teamGroupCode") String teamGroupCode) {
        return teamGroupService.deleteTeamGroup(teamGroupCode);
    }

    @Operation(summary = "import nhóm tổ")
    @PostMapping("/teamgroup/import_teamgroup")
    public CommonResponse importTeamGroupFromExcel(@RequestParam("file") MultipartFile file) throws IOException {
        return teamGroupService.importTeamGroupFromExcel(file);
    }
}
