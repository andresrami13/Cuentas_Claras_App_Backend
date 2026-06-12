package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.FinancialRecord;
import com.cuentasclaras.model.enums.FinancialRecordType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findByUser_DocumentNumber(String userDocumentNumber);

    List<FinancialRecord> findByUser_DocumentNumberAndRecordType(
            String userDocumentNumber,
            FinancialRecordType recordType
    );

    List<FinancialRecord> findByUser_DocumentNumberAndRecurring(
            String userDocumentNumber,
            Boolean recurring
    );

    List<FinancialRecord> findByBudgetCategory_IdAndRecordType(
            Long budgetCategoryId,
            FinancialRecordType recordType
    );
}
