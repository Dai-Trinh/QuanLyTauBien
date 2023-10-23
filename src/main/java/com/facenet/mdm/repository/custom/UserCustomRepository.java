package com.facenet.mdm.repository.custom;

import com.facenet.mdm.domain.User;
import com.facenet.mdm.service.dto.AdminUserDTO;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
    Page<User> getAllUser(PageFilterInput<AdminUserDTO> input, Pageable pageable);
}
