package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.UserBudgetConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBudgetConfigRepository extends JpaRepository<UserBudgetConfig, Long> {
    Optional<UserBudgetConfig> findByUser_DocumentNumber(String documentNumber);
}
