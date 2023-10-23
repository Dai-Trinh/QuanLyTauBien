package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.JobEntity;
import com.facenet.mdm.domain.ProductionStageEntity;
import com.facenet.mdm.service.dto.JobDTO;
import com.facenet.mdm.service.dto.JobQmsDTO;
import com.facenet.mdm.service.dto.ProductionStageDTO;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobCustomRepository {
    Page<JobEntity> getAllJob(PageFilterInput<JobDTO> input, Pageable pageable, List<String> jobCodes);
    List<JobEntity> getAllJobForQms(JobQmsDTO jobQmsDTO);

    List<JobEntity> getListJobCode(PageFilterInput<JobDTO> input);
}
