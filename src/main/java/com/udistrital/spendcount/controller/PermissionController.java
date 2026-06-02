package com.udistrital.spendcount.controller;

import com.udistrital.spendcount.model.dto.ApiResponse;
import com.udistrital.spendcount.model.dto.PermissionDto;
import com.udistrital.spendcount.service.PermissionService;
import com.udistrital.spendcount.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * REST controller that exposes CRUD endpoints for managing permissions.
 *
 * <p>Base path: {@code /permissions}</p>
 * <p>Cross-origin requests are allowed from {@code http://localhost:5173}.</p>
 *
 */
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
public class PermissionController {
    /** Service layer that handles the business logic for permissions. */
    private final PermissionService permissionService;

    /** Utility component for mapping between entities and DTOs. */
    private final Mapper mapper;

    /**
     * Creates a new permission.
     *
     *
     * @param permissionDto the data transfer object containing the details of the permission to create
     * @return a ResponseEntity with HTTP 201 (Created) and the created  PermissionDto
     *         wrapped in an ApiResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionDto>> createPermission(@RequestBody PermissionDto permissionDto) {
        PermissionDto response = mapper.toPermissionDto(permissionService.createPermission(mapper.toPermission(permissionDto)));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Permiso creado exitosamente",
                        response
                ));
    }
    /**
     * Updates a permission.
     *
     *
     * @param permissionCode the unique code of the permission to update
     * @param permissionDto  the data transfer object containing the updated permission details
     * @return a ResponseEntity with HTTP 200 (OK) and the updated PermissionDto
     *         wrapped in an ApiResponse.
     */
    @PutMapping("/{permissionCode}")
    public ResponseEntity<ApiResponse<PermissionDto>> updatePermission(@PathVariable String permissionCode,
                                                       @RequestBody PermissionDto permissionDto) {
        PermissionDto response = mapper.toPermissionDto(permissionService.updatePermission(permissionCode, mapper.toPermission(permissionDto)));
        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Permiso actualizado exitosamente",
                        response
                ));
    }
    /**
     * Deletes a permission identified by its code.
     *
     *
     * @param permissionCode the unique code of the permission to delete
     * @return a ResponseEntity with HTTP 200 (OK) and a {@code null} data payload
     *         wrapped in an ApiResponse, indicating successful deletion of the permission.
     */
    @DeleteMapping("/{permissionCode}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable String permissionCode) {
        permissionService.deletePermission(permissionCode);
        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Permiso eliminado exitosamente",
                        null
                ));
    }

    /**
     * Retrieves all permissions available in the system.
     **
     * @return a ResponseEntity with HTTP 200 (OK) and a list of PermissionDto
     *         wrapped in an ApiResponse
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionDto>>>  getAllPermissions() {
        List<PermissionDto> permissions = mapper.toPermissionsDto(permissionService.getAllPermissions());
        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Permisos obtenidos exitosamente",
                        permissions
                ));
    }

    /**
     * Retrieves a single permission by its unique code.
     *
     *
     * @param permissionCode the unique code of the permission to retrieve
     * @return a ResponseEntity with HTTP 200 (OK) and the matching PermissionDto
     *         wrapped in an ApiResponse
     */
    @GetMapping("/{permissionCode}")
    public ResponseEntity<ApiResponse<PermissionDto>> getPermissionById(@PathVariable String permissionCode) {
        PermissionDto response = mapper.toPermissionDto(permissionService.getPermissionById(permissionCode));

        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Permiso obtenido exitosamente",
                        response
                )
        );
    }
}