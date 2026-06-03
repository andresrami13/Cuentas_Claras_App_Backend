package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.BudgetCategory;
import com.cuentasclaras.model.entity.BudgetCycle;

import java.math.BigDecimal;

public interface BudgetCycleService {
    BudgetCycle createBudgetCycle(BudgetCycle budgetCycle, String userDocumentNumber);
    BudgetCycle getActiveCycle(String userDocumentNumber);
    BudgetCategory addCategory(Long cycleId, BudgetCategory category);
    BudgetCategory updateCategory(Long cycleId, Long categoryId, BudgetCategory category);
    void deductFromCategory(String userDocumentNumber, String categoryName, BigDecimal amount);
}
