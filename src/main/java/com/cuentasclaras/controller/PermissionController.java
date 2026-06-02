package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.model.dto.PermissionDto;
import com.cuentasclaras.service.PermissionService;
import com.cuentasclaras.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
public class PermissionController {

    private final PermissionService permissionService;
    private final Mapper mapper;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionDto>>> getAllPermissions() {
        List<PermissionDto> permissions = mapper.toPermissionsDto(permissionService.getAllPermissions());
        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Permisos obtenidos exitosamente",
                        permissions
                ));
    }

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
