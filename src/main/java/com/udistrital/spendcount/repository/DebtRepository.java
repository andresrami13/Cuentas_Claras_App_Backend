package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.Debt;
import com.udistrital.spendcount.model.enums.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Debt entities.
 */
public interface DebtRepository extends JpaRepository<Debt, Long> {

    List<Debt> findByUser_DocumentNumber(String userDocumentNumber);

    List<Debt> findByUser_DocumentNumberAndStatus(
            String userDocumentNumber,
            DebtStatus status
    );
}