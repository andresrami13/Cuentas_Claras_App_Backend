package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.BudgetCategory;
import com.cuentasclaras.model.entity.BudgetCycle;

public interface BudgetCycleService {
    BudgetCycle createBudgetCycle(BudgetCycle budgetCycle, String userDocumentNumber);
    BudgetCycle getActiveCycle(String userDocumentNumber);
    BudgetCategory addCategory(Long cycleId, BudgetCategory category);
    BudgetCategory updateCategory(Long cycleId, Long categoryId, BudgetCategory category);
    void deleteCategory(Long cycleId, Long categoryId);
}
