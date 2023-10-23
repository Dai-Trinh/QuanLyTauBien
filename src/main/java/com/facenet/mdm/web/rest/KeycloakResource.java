package com.facenet.mdm.web.rest;

import com.facenet.mdm.domain.Authority;
import com.facenet.mdm.domain.User;
import com.facenet.mdm.service.KeycloakService;
import com.facenet.mdm.service.dto.*;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.List;
import java.util.Set;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class KeycloakResource {

    private final KeycloakService keycloakService;

    public KeycloakResource(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/registration/users")
    public CommonResponse registerUser(@RequestBody KeycloakUserDTO user) {
        keycloakService.registerUser(user);
        return new CommonResponse<>().success();
    }

    @PutMapping("/registration/users/{id}")
    public CommonResponse updateUser(@RequestBody KeycloakUserDTO user, @PathVariable String id) {
        keycloakService.updateUser(id, user);
        return new CommonResponse<>().success();
    }

    @GetMapping("/registration/roles")
    public CommonResponse getAllRoles() {
        List<RoleRepresentation> roles = keycloakService.getRoles();
        return new CommonResponse<>().success().data(roles);
    }

    @GetMapping("/roles/{userId}")
    public CommonResponse getAllEffectiveRoles(@PathVariable String userId) {
        RoleDTO roles = keycloakService.getAllRoles(userId);
        return new CommonResponse<>().success().data(roles);
    }

    @PostMapping("/users")
    public PageResponse<List<UserRepresentation>> getAllUsers(@RequestBody PageFilterInput<AdminUserDTO> input) {
        Page<UserRepresentation> users = keycloakService.getUsers(input);
        return new PageResponse<List<UserRepresentation>>().success().data(users.getContent()).dataCount(users.getTotalElements());
    }

    @GetMapping("/users/{userId}")
    public CommonResponse<UserDetailDTO> getUser(@PathVariable String userId) {
        return new CommonResponse<UserDetailDTO>().success().data(keycloakService.getUser(userId));
    }

    @PostMapping("users/auto-complete")
    public CommonResponse<Set<String>> getAutocompleteUser(@RequestBody PageFilterInput<AdminUserDTO> input) {
        return new CommonResponse<>().success().data(keycloakService.getAutocompleteUser(input));
    }

    @DeleteMapping("/users/{id}")
    public CommonResponse deleteUser(@PathVariable String id) {
        keycloakService.deleteUser(id);
        return new CommonResponse().success();
    }
}
