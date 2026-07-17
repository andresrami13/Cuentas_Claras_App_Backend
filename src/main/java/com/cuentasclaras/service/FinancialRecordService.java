package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.FinancialRecord;
import com.cuentasclaras.model.enums.FinancialRecordType;

import java.util.List;

public interface FinancialRecordService {

    FinancialRecord createFinancialRecord(FinancialRecord financialRecord, String userDocumentNumber);

    FinancialRecord updateFinancialRecord(Long financialRecordId, FinancialRecord financialRecord);

    void deleteFinancialRecord(Long financialRecordId);

    FinancialRecord getFinancialRecordById(Long financialRecordId);

    List<FinancialRecord> getFinancialRecordsByUser(String userDocumentNumber);

    List<FinancialRecord> getFinancialRecordsByUserAndType(String userDocumentNumber, FinancialRecordType recordType);

    List<FinancialRecord> getRecurringFinancialRecordsByUser(String userDocumentNumber);

    List<FinancialRecord> getFinancialRecordsByCycle(Long cycleId);
}
