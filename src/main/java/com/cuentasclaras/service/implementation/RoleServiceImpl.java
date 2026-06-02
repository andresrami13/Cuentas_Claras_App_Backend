package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.Permission;
import com.cuentasclaras.model.entity.Role;
import com.cuentasclaras.model.entity.RolePermission;
import com.cuentasclaras.model.entity.RolePermissionId;
import com.cuentasclaras.repository.PermissionRepository;
import com.cuentasclaras.repository.RolePermissionRepository;
import com.cuentasclaras.repository.RoleRepository;
import com.cuentasclaras.repository.UserRepository;
import com.cuentasclaras.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RoleServiceImpl.class);

    @Override
    public Role createRole(Role role) {

        log.info("Inicio de validaciones de negocio");
        this.applyBusinessValidations(role);

        if (role.getRoleCode() == null || role.getRoleName().isBlank())
            throw new BusinessException("El código del rol es obligatorio");

        if (roleRepository.existsById(role.getRoleCode()))
            throw new BusinessException("Ya existe un rol con el código: " + role.getRoleCode());

        try {
            role.setCreatedAt(new Date());

            if (role.getRolePermissions() != null && !role.getRolePermissions().isEmpty()) {
                for (RolePermission rolePermission : role.getRolePermissions()) {

                    String permissionCode = rolePermission.getId().getPermissionCode();

                    Permission permission = permissionRepository.findById(permissionCode)
                            .orElseThrow(() -> new BusinessException(
                                    "No existe el permiso con código: " + permissionCode
                            ));

                    rolePermission.setRole(role);
                    rolePermission.setPermission(permission);
                    rolePermission.setCreatedAt(new Date());

                    rolePermission.setId(new RolePermissionId(
                            role.getRoleCode(),
                            permission.getPermissionCode()
                    ));
                }
            }
            log.info("Registrando rol");
            role = roleRepository.save(role);
        } catch (Exception e) {
            log.error("Error al intentar registrar el rol: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar registrar role");
        }

        return role;
    }

    @Override
    public Role updateRole(String roleCode, Role role) {
        log.info("Inicio de validaciones de negocio para actualizar rol");
        this.applyBusinessValidations(role);

        if (role.getRoleCode() != null) {
            throw new BusinessException("El código del rol debe ser null");
        }

        Role existingRole = roleRepository.findById(roleCode)
                .orElseThrow(() -> new BusinessException("No existe el rol con código: " + roleCode));

        try {
            existingRole.setRoleName(role.getRoleName());
            existingRole.setDescription(role.getDescription());
            existingRole.setUpdatedAt(new Date());

            existingRole.getRolePermissions().clear();

            if (role.getRolePermissions() != null && !role.getRolePermissions().isEmpty()) {
                for (RolePermission rolePermission : role.getRolePermissions()) {

                    if (rolePermission.getId() == null ||
                            rolePermission.getId().getPermissionCode() == null ||
                            rolePermission.getId().getPermissionCode().isBlank()) {
                        throw new BusinessException("El código del permiso es obligatorio");
                    }

                    String permissionCode = rolePermission.getId().getPermissionCode();

                    Permission permission = permissionRepository.findById(permissionCode)
                            .orElseThrow(() -> new BusinessException(
                                    "No existe el permiso con código: " + permissionCode
                            ));

                    RolePermission newRolePermission = RolePermission.builder()
                            .id(RolePermissionId.builder()
                                    .roleCode(existingRole.getRoleCode())
                                    .permissionCode(permission.getPermissionCode())
                                    .build())
                            .role(existingRole)
                            .permission(permission)
                            .createdAt(new Date())
                            .updatedAt(new Date())
                            .build();

                    existingRole.getRolePermissions().add(newRolePermission);
                }
            }
            log.info("Actualizando rol");
            return roleRepository.save(existingRole);

        } catch (BusinessException e) {
            log.error("Ha ocurrido un error de negocio al actualizar el rol: {}", e.getMessage());
            throw new BusinessException("Error de negocio encontrado");
        } catch (Exception e) {
            log.error("Error al intentar actualizar el rol: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar actualizar rol");
        }
    }

    @Override
    public void deleteRole(String roleCode) {
        log.info("Inicio de validaciones de negocio para eliminar rol");
        if (roleCode == null || roleCode.isBlank()) {
            throw new BusinessException("El código del rol es obligatorio");
        }

        Role existingRole = roleRepository.findById(roleCode)
                .orElseThrow(() -> new BusinessException("No existe el rol con código: " + roleCode));

        boolean roleAssignedToUsers = userRepository.existsByRole_RoleCode(roleCode);
        if (roleAssignedToUsers) {
            throw new BusinessException("No se puede eliminar el rol porque está asociado a uno o más usuarios");
        }

        try {
            log.info("Eliminando rol");
            roleRepository.delete(existingRole);
        } catch (Exception e) {
            log.error("Error al intentar eliminar el rol: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar eliminar el rol");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        try{
            log.info("obteniendo lista de roles");
            return roleRepository.findAll();
        } catch (Exception e) {
            log.error("Error al intentar eliminar el rol: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar consultar lista de roles");
        }
    }

    @Override
    public Role getRoleById(String roleCode) {
        try{
            log.info("Obteniendo rol con código: {}", roleCode);
            return roleRepository.findById(roleCode).orElseThrow(() -> new BusinessException("No se ha encontrado el permiso de código ".concat(roleCode)));
        } catch (BusinessException e) {
            throw new BusinessException(e.getMessage());
        }catch (Exception e) {
            log.error("Error al intentar consultar el rol con código: ".concat(roleCode));
            throw new SystemException("Error al intentar consultar el rol de id ".concat(roleCode));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByRoleCode(String roleCode) {
        log.info("Inicio de validaciones de negocio para consultar permisos del rol");
        if (roleCode == null || roleCode.isBlank()) {
            throw new BusinessException("El código del rol es obligatorio");
        }

        if (!roleRepository.existsById(roleCode)) {
            throw new BusinessException("No existe el rol con código: " + roleCode);
        }

        try {
            log.info("Obteniendo permisos asignados al rol con código: {}", roleCode);
            return permissionRepository.findPermissionsByRoleCode(roleCode);
        } catch (Exception e) {
            log.error("Error al intentar consultar permisos asignados al rol: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar consultar permisos asignados al rol");
        }
    }

    private void applyBusinessValidations(Role role) {
        if (role == null)
            throw new BusinessException("El rol no puede ser nulo");

        if (role.getRoleName() == null || role.getRoleName().isBlank())
            throw new BusinessException("El nombre del rol es obligatorio");

        if (role.getDescription() == null || role.getDescription().isBlank())
            throw new BusinessException("La descripción del rol es obligatorio");

    }
}
