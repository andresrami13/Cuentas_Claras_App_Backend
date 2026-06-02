package com.udistrital.spendcount.service;

import com.udistrital.spendcount.model.entity.Debt;
import com.udistrital.spendcount.model.enums.DebtStatus;

import java.util.List;

/**
 * Service interface for managing debts.
 */
public interface DebtService {

    Debt createDebt(Debt debt, String userDocumentNumber);

    Debt updateDebt(Long debtId, Debt debt);

    void deleteDebt(Long debtId);

    Debt getDebtById(Long debtId);

    List<Debt> getDebtsByUser(String userDocumentNumber);

    List<Debt> getDebtsByUserAndStatus(String userDocumentNumber, DebtStatus status);
}