package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.CustomerEntity;
import com.facenet.mdm.service.dto.CustomerDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerCustomRepository {
    Page<CustomerEntity> searchCustomers(PageFilterInput<CustomerDTO> input, Pageable pageable);
}
