package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.RolePermission;
import com.udistrital.spendcount.model.entity.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * <p>Repository interface for managing RolePermission entities. This interface extends JpaRepository,
 * providing basic CRUD operations for managing the RolePermision Table and verifying if a permission is linked to any role.</p>
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {

    boolean existsByIdPermissionCode(String permissionCode);

}