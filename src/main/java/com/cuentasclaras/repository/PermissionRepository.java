package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    @Query("""
           SELECT p
           FROM Permission p
           WHERE p.permissionCode IN (
               SELECT rp.id.permissionCode
               FROM RolePermission rp
               WHERE rp.id.roleCode = :roleCode
           )
           """)
    List<Permission> findPermissionsByRoleCode(@Param("roleCode") String roleCode);

}
