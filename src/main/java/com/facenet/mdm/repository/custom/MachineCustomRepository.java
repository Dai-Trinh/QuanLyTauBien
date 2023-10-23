package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.MachineEntity;
import com.facenet.mdm.service.dto.MachineDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MachineCustomRepository {
    Page<MachineEntity> getAll(PageFilterInput<MachineDTO> input, Pageable pageable);
    List<String> getAutoComplete(String value, String keyName);
}
