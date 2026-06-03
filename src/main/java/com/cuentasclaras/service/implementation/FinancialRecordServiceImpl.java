package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.FinancialRecord;
import com.cuentasclaras.model.entity.User;
import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.repository.FinancialRecordRepository;
import com.cuentasclaras.repository.UserRepository;
import com.cuentasclaras.service.BudgetCycleService;
import com.cuentasclaras.service.FinancialRecordService;
import com.cuentasclaras.utils.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;
    private final BudgetCycleService budgetCycleService;

    @Override
    public FinancialRecord createFinancialRecord(FinancialRecord financialRecord, String userDocumentNumber) {

        log.info("Inicio de validaciones de negocio para crear movimiento financiero");

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        this.applyBussinessValidation(financialRecord);

        User user = userRepository.findById(userDocumentNumber)
                .orElseThrow(() -> new BusinessException("No existe el usuario con número de documento: " + userDocumentNumber));

        try {
            financialRecord.setUser(user);
            financialRecord.setCreatedAt(new Date());

            if (financialRecord.getRecurring() == null)
                financialRecord.setRecurring(false);

            if (Boolean.FALSE.equals(financialRecord.getRecurring()))
                financialRecord.setPeriodicity(null);

            log.info("Registrando movimiento financiero para el usuario: {}", userDocumentNumber);
            FinancialRecord saved = financialRecordRepository.saveAndFlush(financialRecord);

            if (FinancialRecordType.EXPENSE.equals(saved.getRecordType()) && saved.getCategory() != null)
                budgetCycleService.deductFromCategory(userDocumentNumber, saved.getCategory(), saved.getAmount());

            return saved;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar movimiento financiero", e);
            throw new SystemException("Error al intentar registrar movimiento financiero");
        }
    }

    @Override
    public FinancialRecord updateFinancialRecord(Long financialRecordId, FinancialRecord financialRecord) {

        log.info("Inicio de validaciones de negocio para actualizar movimiento financiero");

        if (financialRecordId == null)
            throw new BusinessException("El identificador del movimiento financiero es obligatorio");

        this.applyBussinessValidation(financialRecord);

        FinancialRecord existingFinancialRecord = financialRecordRepository.findById(financialRecordId)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_FINANCIAL_MOVEMENT_WITH_ID + financialRecordId));

        try {
            existingFinancialRecord.setRecordType(financialRecord.getRecordType());
            existingFinancialRecord.setCategory(financialRecord.getCategory());
            existingFinancialRecord.setDescription(financialRecord.getDescription());
            existingFinancialRecord.setAmount(financialRecord.getAmount());
            existingFinancialRecord.setRecordDate(financialRecord.getRecordDate());
            existingFinancialRecord.setRecurring(financialRecord.getRecurring());
            existingFinancialRecord.setPeriodicity(Boolean.TRUE.equals(financialRecord.getRecurring())
                    ? financialRecord.getPeriodicity()
                    : null);
            existingFinancialRecord.setUpdatedAt(new Date());

            log.info("Actualizando movimiento financiero con id: {}", financialRecordId);
            return financialRecordRepository.saveAndFlush(existingFinancialRecord);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar actualizar movimiento financiero", e);
            throw new SystemException("Error al intentar actualizar movimiento financiero");
        }
    }

    @Override
    public void deleteFinancialRecord(Long financialRecordId) {

        log.info("Inicio de validaciones de negocio para eliminar movimiento financiero");

        if (financialRecordId == null)
            throw new BusinessException("El identificador del movimiento financiero es obligatorio");

        FinancialRecord existingFinancialRecord = financialRecordRepository.findById(financialRecordId)
                .orElseThrow(() -> new BusinessException("No existe el movimiento financiero con id: " + financialRecordId));

        try {
            log.info("Eliminando movimiento financiero con id: {}", financialRecordId);
            financialRecordRepository.delete(existingFinancialRecord);

        } catch (Exception e) {
            log.error("Error al intentar eliminar movimiento financiero", e);
            throw new SystemException("Error al intentar eliminar movimiento financiero");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecord getFinancialRecordById(Long financialRecordId) {
        if (financialRecordId == null)
            throw new BusinessException("El identificador del movimiento financiero es obligatorio");

        try {
            log.info("Consultando movimiento financiero con id: {}", financialRecordId);
            return financialRecordRepository.findById(financialRecordId)
                    .orElseThrow(() -> new BusinessException("No existe el movimiento financiero con id: " + financialRecordId));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar consultar movimiento financiero", e);
            throw new SystemException("Error al intentar consultar movimiento financiero");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecord> getFinancialRecordsByUser(String userDocumentNumber) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        try {
            log.info("Consultando movimientos financieros del usuario: {}", userDocumentNumber);
            return financialRecordRepository.findByUser_DocumentNumber(userDocumentNumber);

        } catch (Exception e) {
            log.error("Error al intentar consultar movimientos financieros del usuario", e);
            throw new SystemException("Error al intentar consultar movimientos financieros del usuario");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecord> getFinancialRecordsByUserAndType(String userDocumentNumber, FinancialRecordType recordType) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        if (recordType == null)
            throw new BusinessException("El tipo de movimiento financiero es obligatorio");

        try {
            log.info("Consultando movimientos financieros del usuario: {} por tipo: {}", userDocumentNumber, recordType);
            return financialRecordRepository.findByUser_DocumentNumberAndRecordType(userDocumentNumber, recordType);

        } catch (Exception e) {
            log.error("Error al intentar consultar movimientos financieros por tipo", e);
            throw new SystemException("Error al intentar consultar movimientos financieros por tipo");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecord> getRecurringFinancialRecordsByUser(String userDocumentNumber) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        try {
            log.info("Consultando movimientos financieros recurrentes del usuario: {}", userDocumentNumber);
            return financialRecordRepository.findByUser_DocumentNumberAndRecurring(userDocumentNumber, true);

        } catch (Exception e) {
            log.error("Error al intentar consultar movimientos financieros recurrentes del usuario", e);
            throw new SystemException("Error al intentar consultar movimientos financieros recurrentes del usuario");
        }
    }

    private void applyBussinessValidation(FinancialRecord financialRecord) {
        if (financialRecord == null)
            throw new BusinessException("La información del movimiento financiero es obligatoria");

        if (financialRecord.getRecordType() == null)
            throw new BusinessException("El tipo de movimiento financiero es obligatorio");

        if (financialRecord.getCategory() == null || financialRecord.getCategory().isBlank())
            throw new BusinessException("La categoría del movimiento financiero es obligatoria");

        if (financialRecord.getAmount() == null)
            throw new BusinessException("El valor del movimiento financiero es obligatorio");

        if (financialRecord.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("El valor del movimiento financiero debe ser mayor a cero");

        if (financialRecord.getRecordDate() == null)
            throw new BusinessException("La fecha del movimiento financiero es obligatoria");

        if (financialRecord.getRecurring() == null)
            financialRecord.setRecurring(false);

        if (Boolean.TRUE.equals(financialRecord.getRecurring()) && financialRecord.getPeriodicity() == null)
            throw new BusinessException("La periodicidad es obligatoria cuando el movimiento financiero es recurrente");

        if (Boolean.FALSE.equals(financialRecord.getRecurring()))
            financialRecord.setPeriodicity(null);
    }
}
