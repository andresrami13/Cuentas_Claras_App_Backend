package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.FinancialGoal;
import com.cuentasclaras.model.enums.FinancialGoalStatus;

import java.util.List;

public interface FinancialGoalService {

    FinancialGoal createFinancialGoal(FinancialGoal financialGoal, String userDocumentNumber);

    FinancialGoal updateFinancialGoal(Long financialGoalId, FinancialGoal financialGoal);

    void deleteFinancialGoal(Long financialGoalId);

    FinancialGoal getFinancialGoalById(Long financialGoalId);

    List<FinancialGoal> getFinancialGoalsByUser(String userDocumentNumber);

    List<FinancialGoal> getFinancialGoalsByUserAndStatus(String userDocumentNumber, FinancialGoalStatus status);
}
