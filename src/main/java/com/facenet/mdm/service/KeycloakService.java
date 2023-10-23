package com.facenet.mdm.service;

import com.facenet.mdm.domain.EmployeeEntity;
import com.facenet.mdm.repository.AuthorityRepository;
import com.facenet.mdm.repository.EmployeeRepository;
import com.facenet.mdm.repository.UserRepository;
import com.facenet.mdm.service.dto.AdminUserDTO;
import com.facenet.mdm.service.dto.KeycloakUserDTO;
import com.facenet.mdm.service.dto.RoleDTO;
import com.facenet.mdm.service.dto.UserDetailDTO;
import com.facenet.mdm.service.exception.CustomException;
import com.facenet.mdm.service.mapper.UserMapper;
import com.facenet.mdm.service.model.PageFilterInput;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakService.class);
    private final Keycloak keycloak;

    @Value("${keycloak.app.realm}")
    private String realm;

    private final EmployeeRepository employeeRepository;

    public KeycloakService(Keycloak keycloak, EmployeeRepository employeeRepository) {
        this.keycloak = keycloak;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public void registerUser(KeycloakUserDTO user) {
        // Check employee code
        EmployeeEntity employeeEntity = null;
        if (!StringUtils.isEmpty(user.getEmployeeCode())) {
            employeeEntity = employeeRepository.getEmployeeEntitieByCode(user.getEmployeeCode());
            if (employeeEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "entity.notfound", user.getEmployeeCode());
            if (StringUtils.isEmpty(employeeEntity.getUsername())) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "exist.employee.username");
            }
            employeeEntity.setUsername(user.getUsername());
        }

        RealmResource realmResource = keycloak.realm(realm);
        // Create user
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEnabled(true);

        // Call api create user
        log.info("Creating user {}", user.getUsername());
        Response response = realmResource.users().create(userRepresentation);
        if (response.getStatus() == HttpStatus.CONFLICT.value()) {
            throw new CustomException(HttpStatus.CONFLICT, "duplicate.user");
        }

        String userId = CreatedResponseUtil.getCreatedId(response);
        log.info("Created user {} with id {}", user.getUsername(), userId);

        // Set password
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(user.getPassword());

        UserResource userResource = realmResource.users().get(userId);
        log.info("Creating password {}", user.getUsername());
        userResource.resetPassword(credentialRepresentation);

        // Assign roles
        if (!CollectionUtils.isEmpty(user.getAuthorities())) {
            List<RoleRepresentation> roles = realmResource.roles().list();
            List<RoleRepresentation> assignedRolesByUser = new ArrayList<>(user.getAuthorities().size());
            for (RoleRepresentation roleRepresentation : roles) {
                for (String inputRole : user.getAuthorities()) {
                    if (inputRole.equals(roleRepresentation.getName())) {
                        assignedRolesByUser.add(roleRepresentation);
                    }
                }
            }
            userResource.roles().realmLevel().add(assignedRolesByUser);
        }
        // Save employee username
        if (employeeEntity != null) employeeRepository.save(employeeEntity);
    }

    public void updateUser(String userId, KeycloakUserDTO user) {
        // Check employee code
        EmployeeEntity employeeEntity = null;
        if (!StringUtils.isEmpty(user.getEmployeeCode())) {
            employeeEntity = employeeRepository.getEmployeeEntitieByCode(user.getEmployeeCode());
            if (employeeEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "entity.notfound", user.getEmployeeCode());
            employeeEntity.setUsername(user.getUsername());
        }

        RealmResource realmResource = keycloak.realm(realm);
        UserResource userResource = realmResource.users().get(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmail());
        userResource.update(userRepresentation);
        log.info("Update user {}", user.getUsername());

        Set<String> assignedRoles = userResource
            .roles()
            .realmLevel()
            .listAll()
            .stream()
            .map(RoleRepresentation::getName)
            .collect(Collectors.toSet());
        Set<String> newRoles = user.getAuthorities();
        Map<String, RoleRepresentation> allRoles = getRoles()
            .stream()
            .collect(Collectors.toMap(RoleRepresentation::getName, Function.identity()));

        List<RoleRepresentation> removeRoles = new ArrayList<>();
        List<RoleRepresentation> addRoles = new ArrayList<>();

        for (String assignedRole : assignedRoles) {
            if (!newRoles.contains(assignedRole)) {
                removeRoles.add(allRoles.get(assignedRole));
            }
        }
        for (String newRole : newRoles) {
            if (!assignedRoles.contains(newRole)) {
                addRoles.add(allRoles.get(newRole));
            }
        }

        if (!CollectionUtils.isEmpty(removeRoles)) userResource.roles().realmLevel().remove(removeRoles);
        if (!CollectionUtils.isEmpty(addRoles)) userResource.roles().realmLevel().add(addRoles);

        if (employeeEntity != null) employeeRepository.save(employeeEntity);
    }

    public List<RoleRepresentation> getRoles() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.roles().list();
    }

    public Page<UserRepresentation> getUsers(PageFilterInput<AdminUserDTO> input) {
        Pageable pageable = input.getPageSize() == 0 ? Pageable.unpaged() : PageRequest.of(input.getPageNumber(), input.getPageSize());
        RealmResource realmResource = keycloak.realm(realm);
        int offset = input.getPageNumber() * input.getPageSize();
        int size = input.getPageSize() == 0 ? Integer.MAX_VALUE : input.getPageSize();

        List<UserRepresentation> users;
        if (StringUtils.isEmpty(input.getCommon())) {
            users =
                realmResource
                    .users()
                    .search(
                        StringUtils.isEmpty(input.getFilter().getUsername()) ? null : input.getFilter().getUsername(),
                        StringUtils.isEmpty(input.getFilter().getFirstName()) ? null : input.getFilter().getFirstName(),
                        StringUtils.isEmpty(input.getFilter().getLastName()) ? null : input.getFilter().getLastName(),
                        StringUtils.isEmpty(input.getFilter().getEmail()) ? null : input.getFilter().getEmail(),
                        offset,
                        size
                    );
            return new PageImpl<>(
                users,
                pageable,
                realmResource
                    .users()
                    .count(
                        StringUtils.isEmpty(input.getFilter().getUsername()) ? null : input.getFilter().getUsername(),
                        StringUtils.isEmpty(input.getFilter().getFirstName()) ? null : input.getFilter().getFirstName(),
                        StringUtils.isEmpty(input.getFilter().getLastName()) ? null : input.getFilter().getLastName(),
                        StringUtils.isEmpty(input.getFilter().getEmail()) ? null : input.getFilter().getEmail()
                    )
            );
        } else {
            users = realmResource.users().search(input.getCommon(), (int) pageable.getOffset(), pageable.getPageSize());
            return new PageImpl<>(users, pageable, realmResource.users().count(input.getCommon()));
        }
    }

    public void deleteUser(String id) {
        RealmResource realmResource = keycloak.realm(realm);
        Response response = realmResource.users().delete(id);
        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) throw new CustomException(HttpStatus.NOT_FOUND, "record.notfound");
        response.close();
    }

    public RoleDTO getAllRoles(String userId) {
        RealmResource realmResource = keycloak.realm(realm);
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setAssignedRole(
            realmResource
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listAll()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
        );
        roleDTO.setEffectiveRole(
            realmResource
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listEffective()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
        );
        roleDTO.setAvailableRole(
            realmResource
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listAvailable()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
        );
        return roleDTO;
    }

    private void updateEmployeeUsername(String employeeCode, String username) throws CustomException {
        if (!StringUtils.isEmpty(employeeCode)) {
            EmployeeEntity employeeEntity = employeeRepository.getEmployeeEntitieByCode(employeeCode);
            if (employeeEntity == null) throw new CustomException(HttpStatus.NOT_FOUND, "entity.notfound", employeeCode);
            employeeEntity.setUsername(username);
            employeeRepository.save(employeeEntity);
        }
    }

    public Set<String> getAutocompleteUser(PageFilterInput<AdminUserDTO> input) {
        Set<String> result = new HashSet<>(input.getPageSize());
        Page<UserRepresentation> users = getUsers(input);
        String query = input.getCommon().toLowerCase();
        for (UserRepresentation user : users) {
            if (!StringUtils.isEmpty(user.getUsername()) && user.getUsername().toLowerCase().contains(query)) {
                result.add(user.getUsername());
            }
            if (!StringUtils.isEmpty(user.getFirstName()) && user.getFirstName().toLowerCase().contains(query)) {
                result.add(user.getFirstName());
            }
            if (!StringUtils.isEmpty(user.getLastName()) && user.getLastName().toLowerCase().contains(query)) {
                result.add(user.getLastName());
            }
            if (!StringUtils.isEmpty(user.getEmail()) && user.getEmail().toLowerCase().contains(query)) {
                result.add(user.getEmail());
            }
        }
        return result;
    }

    public UserDetailDTO getUser(String userId) {
        RealmResource realmResource = keycloak.realm(realm);
        UserDetailDTO user = new UserDetailDTO(realmResource.users().get(userId).toRepresentation());
        EmployeeEntity employeeEntity = employeeRepository.findByUsernameEqualsIgnoreCaseAndIsActiveTrue(user.getUsername());
        if (employeeEntity != null) user.setEmployeeCode(employeeEntity.getEmployeeCode());

        user.setAssignedRole(
            realmResource
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listAll()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
        );
        user.setEffectiveRole(
            realmResource
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listEffective()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
        );
        user.setAvailableRole(
            realmResource
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listAvailable()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList())
        );
        return user;
    }
}
