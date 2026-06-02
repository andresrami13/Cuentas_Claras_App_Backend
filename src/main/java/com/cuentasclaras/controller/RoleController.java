package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.model.dto.PermissionDto;
import com.cuentasclaras.model.dto.RoleDto;
import com.cuentasclaras.service.RoleService;
import com.cuentasclaras.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final Mapper mapper;

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

    @PutMapping("/{roleCode}")
    public ResponseEntity<ApiResponse<RoleDto>> updateRole(@PathVariable String roleCode,
                                           @RequestBody RoleDto roleDto) {
        RoleDto response = mapper.toRoleDto(roleService.updateRole(roleCode, mapper.toRole(roleDto)));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Rol actualizado exitosamente",
                        response
                ));
    }

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
