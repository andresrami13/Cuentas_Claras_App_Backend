package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.Permission;
import com.cuentasclaras.repository.PermissionRepository;
import com.cuentasclaras.repository.RolePermissionRepository;
import com.cuentasclaras.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Override
    public Permission createPermission(Permission permission) {

        log.info("Inicio de validaciones de negocio");
        this.applyBusinessValidations(permission);

        if (permission.getPermissionCode() == null || permission.getPermissionCode().isBlank())
            throw new BusinessException("El código del permiso es obligatorio");

        if (permissionRepository.existsById(permission.getPermissionCode()))
            throw new BusinessException("Ya existe un permiso con el código: " + permission.getPermissionCode());

        try {
            permission.setCreatedAt(new Date());
            log.info("Registrando permiso");
            permission = permissionRepository.save(permission);
        } catch (Exception e) {
            log.error("Error al intentar registrar permiso: ", e.getMessage(), e);
            throw new SystemException("Error al intentar registrar permiso");
        }

        return permission;
    }

    @Override
    public Permission updatePermission(String permissionCode, Permission permission) {

        log.info("Inicio de validaciones de negocio para actualizar permiso");
        this.applyBusinessValidations(permission);

        if (permission.getPermissionCode() != null)
            throw new BusinessException("El código del permiso debe ser null");

        Permission existingPermission = permissionRepository.findById(permissionCode)
                .orElseThrow(() -> new BusinessException("No existe el permiso con código: " + permissionCode));

        try {
            existingPermission.setPermissionName(permission.getPermissionName());
            existingPermission.setDescription(permission.getDescription());
            existingPermission.setUpdatedAt(new Date());
            log.info("Actualizando permiso");
            return permissionRepository.save(existingPermission);
        } catch (Exception e) {
            log.error("Error al intentar actualizar el permiso: ", e.getMessage(), e);
            throw new SystemException("Error al intentar actualizar permiso");
        }
    }

    @Override
    public void deletePermission(String permissionCode) {
        log.info("Inicio de validaciones de negocio para eliminar el permiso");
        if (permissionCode == null || permissionCode.isBlank()) {
            throw new BusinessException("El código del permiso es obligatorio");
        }

        Permission existingPermission = permissionRepository.findById(permissionCode)
                .orElseThrow(() -> new BusinessException("No existe el permiso con código: " + permissionCode));

        boolean permissionAssignedToRoles = rolePermissionRepository.existsByIdPermissionCode(permissionCode);
        if (permissionAssignedToRoles) {
            throw new BusinessException("No se puede eliminar el permiso porque está asociado a uno o más roles");
        }

        try {
            permissionRepository.delete(existingPermission);
        } catch (Exception e) {
            log.error("Error al intentar borrar el permiso: ", e.getMessage(), e);
            throw new SystemException("Error al intentar eliminar permiso");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        try{
            return permissionRepository.findAll();
        } catch (Exception e) {
            throw new SystemException("Error al intentar consultar lista de permisos");
        }
    }

    @Override
    public Permission getPermissionById(String permissionCode) {
        try{
            return permissionRepository.findById(permissionCode).orElseThrow(() -> new BusinessException("No se ha encontrado el permiso de código ".concat(permissionCode)));
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }catch (Exception e) {
            throw new SystemException("Error al intentar consultar permiso de id ".concat(permissionCode));
        }
    }

    private void applyBusinessValidations(Permission permission) {
        if (permission == null)
            throw new BusinessException("El permiso no puede ser nulo");

        if (permission.getPermissionName() == null || permission.getPermissionName().isBlank())
            throw new BusinessException("El nombre del permiso es obligatorio");

        if (permission.getDescription() == null || permission.getDescription().isBlank())
            throw new BusinessException("La descripción del permiso es obligatorio");

    }
}
