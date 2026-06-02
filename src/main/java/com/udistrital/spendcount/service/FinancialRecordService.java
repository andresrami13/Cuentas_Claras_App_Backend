package com.udistrital.spendcount.service;

import com.udistrital.spendcount.model.entity.FinancialRecord;
import com.udistrital.spendcount.model.enums.FinancialRecordType;

import java.util.List;

/**
 * Service interface for managing financial records.
 */
public interface FinancialRecordService {

    FinancialRecord createFinancialRecord(FinancialRecord financialRecord, String userDocumentNumber);

    FinancialRecord updateFinancialRecord(Long financialRecordId, FinancialRecord financialRecord);

    void deleteFinancialRecord(Long financialRecordId);

    FinancialRecord getFinancialRecordById(Long financialRecordId);

    List<FinancialRecord> getFinancialRecordsByUser(String userDocumentNumber);

    List<FinancialRecord> getFinancialRecordsByUserAndType(String userDocumentNumber, FinancialRecordType recordType);

    List<FinancialRecord> getRecurringFinancialRecordsByUser(String userDocumentNumber);
}