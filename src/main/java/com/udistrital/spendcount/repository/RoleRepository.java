package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * <p>Repository interface for managing Role entities. This interface extends JpaRepository,
 * providing basic CRUD operations for managing the Role Table.</p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
