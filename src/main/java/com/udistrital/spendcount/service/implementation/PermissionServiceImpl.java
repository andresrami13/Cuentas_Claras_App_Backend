package com.udistrital.spendcount.service.implementation;

import com.udistrital.spendcount.exception.BusinessException;
import com.udistrital.spendcount.exception.SystemException;
import com.udistrital.spendcount.model.entity.Permission;
import com.udistrital.spendcount.repository.PermissionRepository;
import com.udistrital.spendcount.repository.RolePermissionRepository;
import com.udistrital.spendcount.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Class that implements the permission management logic
 */
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PermissionServiceImpl.class);
    /**
     * Allow to create a new permission
     *
     * @param permission    permission info to save
     * @return Permission   object saved
     */
    @Override
    public Permission createPermission(Permission permission) {

        //Business validations
        log.info("Inicio de validaciones de negocio");
        this.applyBusinessValidations(permission);

        if (permission.getPermissionCode() == null || permission.getPermissionCode().isBlank())
            throw new BusinessException("El código del permiso es obligatorio");

        if (permissionRepository.existsById(permission.getPermissionCode()))
            throw new BusinessException("Ya existe un permiso con el código: " + permission.getPermissionCode());

        try {
            //To create the new permission
            permission.setCreatedAt(new Date());
            log.info("Registrando permiso");
            permission = permissionRepository.save(permission);
        } catch (Exception e) {
            log.error("Error al intentar registrar permiso: ".concat(e.getMessage()));
            throw new SystemException("Error al intentar registrar permiso");
        }

        return permission;
    }
    /**
     * Allow to update a permission
     *
     * @param permission    permission info to save
     * @param permissionCode    code of the permission to update
     * @return Permission   object saved
     */
    @Override
    public Permission updatePermission(String permissionCode, Permission permission) {

        //Business validations
        log.info("Inicio de validaciones de negocio para actualizar permiso");
        this.applyBusinessValidations(permission);

        if (permission.getPermissionCode() != null)
            throw new  BusinessException("El código del permiso debe ser null");

        Permission existingPermission = permissionRepository.findById(permissionCode)
                .orElseThrow(() -> new  BusinessException("No existe el permiso con código: " + permissionCode));

        try {
            existingPermission.setPermissionName(permission.getPermissionName());
            existingPermission.setDescription(permission.getDescription());
            existingPermission.setUpdatedAt(new Date());
            log.info("Actualizando permiso");
            return permissionRepository.save(existingPermission);
        } catch (Exception e) {
            log.error("Error al intentar actualizar el permiso: ".concat(e.getMessage()));

            throw new SystemException("Error al intentar actualizar permiso");
        }
    }
    /**
     * Allows to delete a permission, but only if it is not associated to any role
     * @param permissionCode    permission code of the permission to delete
     */
    @Override
    public void deletePermission(String permissionCode) {
        //Business validations
        log.info("Inicio de validaciones de negocio para eliminar el permiso");
        if (permissionCode == null || permissionCode.isBlank()) {
            throw new  BusinessException("El código del permiso es obligatorio");
        }

        Permission existingPermission = permissionRepository.findById(permissionCode)
                .orElseThrow(() -> new  BusinessException("No existe el permiso con código: " + permissionCode));

        boolean permissionAssignedToRoles = rolePermissionRepository.existsByIdPermissionCode(permissionCode);
        if (permissionAssignedToRoles) {
            throw new BusinessException("No se puede eliminar el permiso porque está asociado a uno o más roles");
        }

        try {
            permissionRepository.delete(existingPermission);
        } catch (Exception e) {
            log.error("Error al intentar borrar el permiso: ".concat(e.getMessage()));
            throw new SystemException("Error al intentar eliminar permiso");
        }

    }
    /**
     * Allows to get all the permissions in the system
     *
     * @return List<Permission>   list of found permissions
     */
    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        try{
            return permissionRepository.findAll();
        } catch (Exception e) {
            throw new SystemException("Error al intentar consultar lista de permisos");
        }
    }
    /**
     * Allows to get a permission by its code
     *
     * @param permissionCode    code of the permission to find
     * @return Permission      object  found
     */
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
    /**
     * Apply business validations for the permission entity, such as required fields, field formats, etc.
     *
     * @param permission    permission to validate
     */
    private void applyBusinessValidations(Permission permission) {
        //Business validations
        if (permission == null)
            throw new BusinessException("El permiso no puede ser nulo");

        if (permission.getPermissionName() == null || permission.getPermissionName().isBlank())
            throw new BusinessException("El nombre del permiso es obligatorio");

        if (permission.getDescription() == null || permission.getDescription().isBlank())
            throw new  BusinessException("La descripción del permiso es obligatorio");

    }
}