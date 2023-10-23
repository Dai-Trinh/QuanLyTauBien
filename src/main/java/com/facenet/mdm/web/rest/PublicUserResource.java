package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.UserService;
import com.facenet.mdm.service.dto.AdminUserDTO;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private final Logger log = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserService userService;

    public PublicUserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code GET /users} : get all users with only the public informations - calling this are allowed for anyone.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @PostMapping("/userss")
    public PageResponse<List<AdminUserDTO>> getAllPublicUsers(
        @org.springdoc.api.annotations.ParameterObject @RequestBody PageFilterInput<AdminUserDTO> input
    ) {
        log.debug("REST request to get all public User names");

        final Page<AdminUserDTO> page = userService.getAllPublicUsers(input);
        return new PageResponse<List<AdminUserDTO>>().success().data(page.getContent()).dataCount(page.getTotalElements());
    }

    /**
     * Gets a list of all roles.
     * @return a string list of all roles.
     */
    @GetMapping("/authorities")
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }
}
