package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission createPermission(Permission permission);

    Permission updatePermission(String permissionCode, Permission permission);

    void deletePermission(String permissionCode);

    List<Permission> getAllPermissions();

    Permission getPermissionById(String permissionCode);
}
