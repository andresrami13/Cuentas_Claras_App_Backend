package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.BudgetCycle;
import com.cuentasclaras.model.enums.BudgetCycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetCycleRepository extends JpaRepository<BudgetCycle, Long> {
    Optional<BudgetCycle> findByUser_DocumentNumberAndStatus(String documentNumber, BudgetCycleStatus status);
    boolean existsByUser_DocumentNumberAndStatus(String documentNumber, BudgetCycleStatus status);
}
