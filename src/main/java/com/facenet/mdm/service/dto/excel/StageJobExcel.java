package com.facenet.mdm.service.dto.excel;

import com.facenet.mdm.service.dto.JobDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;

import java.util.List;

public class StageJobExcel {
    private List<ProductionStageDTO> productionStageDTOList;
    private List<JobDTO> jobDTOList;

    public List<ProductionStageDTO> getProductionStageDTOList() {
        return productionStageDTOList;
    }

    public void setProductionStageDTOList(List<ProductionStageDTO> productionStageDTOList) {
        this.productionStageDTOList = productionStageDTOList;
    }

    public List<JobDTO> getJobDTOList() {
        return jobDTOList;
    }

    public void setJobDTOList(List<JobDTO> jobDTOList) {
        this.jobDTOList = jobDTOList;
    }

    public StageJobExcel() {
    }
}
