package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.MerchandiseGroupEntity;
import com.facenet.mdm.service.dto.MerchandiseGroupDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MerchandiseGroupCustomRepository {
    Page<MerchandiseGroupEntity> getAllMerchandiseGroup(PageFilterInput<MerchandiseGroupDTO> input, Pageable pageable);
}
