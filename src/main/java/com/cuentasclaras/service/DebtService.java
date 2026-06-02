package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.Debt;
import com.cuentasclaras.model.enums.DebtStatus;

import java.util.List;

public interface DebtService {

    Debt createDebt(Debt debt, String userDocumentNumber);

    Debt updateDebt(Long debtId, Debt debt);

    void deleteDebt(Long debtId);

    Debt getDebtById(Long debtId);

    List<Debt> getDebtsByUser(String userDocumentNumber);

    List<Debt> getDebtsByUserAndStatus(String userDocumentNumber, DebtStatus status);
}
