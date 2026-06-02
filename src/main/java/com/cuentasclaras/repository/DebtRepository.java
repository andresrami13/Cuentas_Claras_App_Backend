package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.Debt;
import com.cuentasclaras.model.enums.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {

    List<Debt> findByUser_DocumentNumber(String userDocumentNumber);

    List<Debt> findByUser_DocumentNumberAndStatus(
            String userDocumentNumber,
            DebtStatus status
    );
}
