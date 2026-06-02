package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.FinancialGoal;
import com.udistrital.spendcount.model.enums.FinancialGoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing FinancialGoal entities.
 */
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {

    List<FinancialGoal> findByUser_DocumentNumber(String userDocumentNumber);

    List<FinancialGoal> findByUser_DocumentNumberAndStatus(
            String userDocumentNumber,
            FinancialGoalStatus status
    );
}