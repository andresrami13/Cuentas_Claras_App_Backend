package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByRole_RoleCode(String roleCode);
    boolean existsByDocumentNumber(String documentNumber);
    boolean existsByEmail(String email);
    java.util.Optional<User> findByDocumentNumber(String documentNumber);
    java.util.Optional<User> findByEmail(String email);
}
