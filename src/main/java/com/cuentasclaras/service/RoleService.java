package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.Permission;
import com.cuentasclaras.model.entity.Role;

import java.util.List;

public interface RoleService {

    Role createRole(Role role);

    Role updateRole(String roleCode, Role role);

    void deleteRole(String roleCode);

    List<Role> getAllRoles();

    Role getRoleById(String roleCode);

    List<Permission> getPermissionsByRoleCode(String roleCode);
}
