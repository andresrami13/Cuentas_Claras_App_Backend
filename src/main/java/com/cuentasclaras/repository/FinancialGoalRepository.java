package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.FinancialGoal;
import com.cuentasclaras.model.enums.FinancialGoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {

    List<FinancialGoal> findByUser_DocumentNumber(String userDocumentNumber);

    List<FinancialGoal> findByUser_DocumentNumberAndStatus(
            String userDocumentNumber,
            FinancialGoalStatus status
    );
}
