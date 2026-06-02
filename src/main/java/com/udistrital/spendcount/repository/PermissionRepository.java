package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
/**
 * <p>Repository interface for managing Permission entities. This interface extends JpaRepository,
 * providing basic CRUD operations and custom query methods for retrieving permissions based on role codes.</p>
 */
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