package com.udistrital.spendcount.controller;

import com.udistrital.spendcount.model.dto.ApiResponse;
import com.udistrital.spendcount.model.dto.PermissionDto;
import com.udistrital.spendcount.model.dto.RoleDto;
import com.udistrital.spendcount.service.RoleService;
import com.udistrital.spendcount.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * REST controller that exposes CRUD endpoints for managing roles.
 *
 * <p>Base path: {@code /roles}</p>
 * <p>Cross-origin requests are allowed from {@code http://localhost:5173}.</p>
 *
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
public class RoleController {
    /** Service layer that handles the business logic for roles. */
    private final RoleService roleService;

    /** Utility component for mapping between entities and DTOs. */
    private final Mapper mapper;


    /**
     * Creates a new role.
     *
     *
     * @param roleDto the data transfer object containing the details of the permission to create
     * @return a ResponseEntity with HTTP 201 (Created) and the created  roleDto
     *         wrapped in an ApiResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RoleDto>> createRole(@RequestBody RoleDto roleDto) {
        RoleDto response = mapper.toRoleDto(roleService.createRole(mapper.toRole(roleDto)));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Rol creado exitosamente",
                        response
                ));
    }
    /**
     * Updates a role.
     *
     *
     * @param roleCode the unique code of the role to update
     * @param roleDto  the data transfer object containing the updated role details
     * @return a ResponseEntity with HTTP 200 (OK) and the updated RoleDto
     *         wrapped in an ApiResponse.
     */
    @PutMapping("/{roleCode}")
    public ResponseEntity<ApiResponse<RoleDto>>  updateRole(@PathVariable String roleCode,
                                           @RequestBody RoleDto roleDto) {
        RoleDto response = mapper.toRoleDto(roleService.updateRole(roleCode, mapper.toRole(roleDto)));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Rol actualizado exitosamente",
                        response
                ));
    }

    /**
     * Deletes a role identified by its code.
     *
     *
     * @param roleCode the unique code of the role to delete
     * @return a ResponseEntity with HTTP 200 (OK) and a {@code null} data payload
     *         wrapped in an ApiResponse, indicating successful deletion of the role.
     */
    @DeleteMapping("/{roleCode}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String roleCode) {
        roleService.deleteRole(roleCode);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Rol eliminado exitosamente",
                        null
                ));
    }

    /**
     * Retrieves all roles available in the system.
     **
     * @return a ResponseEntity with HTTP 200 (OK) and a list of RolDto
     *         wrapped in an ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles() {
        List<RoleDto> roles = mapper.toRolesDto(roleService.getAllRoles());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Lista de roles obtenida exitosamente",
                        roles
                ));
    }

    /**
     * Retrieves a single role by its unique code.
     *
     *
     * @param roleCode the unique code of the role to retrieve
     * @return a ResponseEntity with HTTP 200 (OK) and the matching RoleDto
     *         wrapped in an ApiResponse
     */
    @GetMapping("/{roleCode}")
    public ResponseEntity<ApiResponse<RoleDto>> getRoleById(@PathVariable String roleCode) {
        RoleDto roleDto = mapper.toRoleDto(roleService.getRoleById(roleCode));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Rol obtenido exitosamente",
                        roleDto
                ));
    }

    /**
     * Retrieves the list of permissions assigned to a role
     *
     *
     * @param roleCode the unique code of the role to retrieve permissions for
     * @return a ResponseEntity with HTTP 200 (OK) and the list of PermissionDto corresponding to the permissions assigned to the role
     *         wrapped in an ApiResponse
     */
    @GetMapping("/{roleCode}/permissions")
    public ResponseEntity<ApiResponse<List<PermissionDto>>> getPermissionsByRole(@PathVariable String roleCode) {
        List<PermissionDto> permissions = mapper.toPermissionsDto(roleService.getPermissionsByRoleCode(roleCode));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Lista de permisos asignados al rol obtenidos exitosamente",
                        permissions
                ));
    }
}
