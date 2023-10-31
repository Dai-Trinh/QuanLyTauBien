package com.facenet.mdm.repository.custom;


import com.facenet.mdm.domain.SeaportEntity;
import com.facenet.mdm.service.dto.SeaportDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SeaportCustomRepository {

    Page<SeaportEntity> getAll(PageFilterInput<SeaportDTO> input, Pageable pageable);

}
