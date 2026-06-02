package com.udistrital.spendcount.service;

import com.udistrital.spendcount.model.entity.Permission;
import com.udistrital.spendcount.model.entity.Role;

import java.util.List;

public interface RoleService {

    Role createRole(Role role);

    Role updateRole(String roleCode, Role role);

    void deleteRole(String roleCode);

    List<Role> getAllRoles();

    Role getRoleById(String roleCode);

    List<Permission> getPermissionsByRoleCode(String roleCode);
}