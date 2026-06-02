package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.RolePermission;
import com.cuentasclaras.model.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    boolean existsByIdPermissionCode(String permissionCode);

}
