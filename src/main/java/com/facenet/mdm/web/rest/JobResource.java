package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.JobService;
import com.facenet.mdm.service.ProductionStageService;
import com.facenet.mdm.service.dto.JobDTO;
import com.facenet.mdm.service.dto.JobDTOAPS;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobResource {

    @Autowired
    JobService jobService;

    @Autowired
    ProductionStageService productionStageService;

    @Operation(summary = "Lấy danh sách công đoạn và job")
    @PostMapping
    public CommonResponse<List<ProductionStageDTO>> search(@RequestBody PageFilterInput<JobDTO> input) {
        return jobService.getListJob(input);
    }

    @Operation(summary = "Thêm mới công đoạn av job")
    @PostMapping("/new-job-list")
    public CommonResponse addJob(@RequestBody List<JobDTO> jobDTOList) {
        return jobService.createOrUpdateJob(jobDTOList);
    }

    @PostMapping("/new-job")
    public CommonResponse createJob(@RequestBody JobDTO jobDTO) {
        return jobService.createJob(jobDTO);
    }

    @Operation(summary = "Cập nhật công đoạn và job")
    @PutMapping("/{jobCode}")
    public CommonResponse updateProductionStage(@RequestBody JobDTO jobDTO, @PathVariable String jobCode) {
        return jobService.updateJob(jobDTO, jobCode);
    }

    @Operation(summary = "Xóa công đoạn và job")
    @DeleteMapping("/{jobCode}")
    public CommonResponse deleteProdutionStage(@PathVariable String jobCode) {
        return jobService.deleteJob(jobCode);
    }

    @Operation(summary = "Lấy công đoạn cho APS")
    @PostMapping("/aps")
    public PageResponse<List<JobDTOAPS>> getJobForAPS(@RequestBody List<String> jobCodes) {
        return jobService.getJobForAPS(jobCodes);
    }
}
