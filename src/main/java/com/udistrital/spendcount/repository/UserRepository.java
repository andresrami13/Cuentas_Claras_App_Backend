package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * <p>Repository interface for managing User entities. This interface extends JpaRepository,
 * providing basic CRUD operations for managing the User Table.</p>
 */
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByRole_RoleCode(String roleCode);
    boolean existsByDocumentNumber(String documentNumber);
    java.util.Optional<User> findByDocumentNumber(String documentNumber);
}