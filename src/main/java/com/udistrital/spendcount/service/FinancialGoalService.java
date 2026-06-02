package com.udistrital.spendcount.service;

import com.udistrital.spendcount.model.entity.FinancialGoal;
import com.udistrital.spendcount.model.enums.FinancialGoalStatus;

import java.util.List;

/**
 * Service interface for managing financial goals.
 */
public interface FinancialGoalService {

    FinancialGoal createFinancialGoal(FinancialGoal financialGoal, String userDocumentNumber);

    FinancialGoal updateFinancialGoal(Long financialGoalId, FinancialGoal financialGoal);

    void deleteFinancialGoal(Long financialGoalId);

    FinancialGoal getFinancialGoalById(Long financialGoalId);

    List<FinancialGoal> getFinancialGoalsByUser(String userDocumentNumber);

    List<FinancialGoal> getFinancialGoalsByUserAndStatus(String userDocumentNumber, FinancialGoalStatus status);
}