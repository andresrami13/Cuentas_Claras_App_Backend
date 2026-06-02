package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.FinancialRecord;
import com.udistrital.spendcount.model.enums.FinancialRecordType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing FinancialRecord entities.
 */
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
}