package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.Account;
import com.cuentasclaras.model.entity.BudgetCategory;
import com.cuentasclaras.model.entity.BudgetCycle;
import com.cuentasclaras.model.entity.FinancialRecord;
import com.cuentasclaras.model.entity.User;
import com.cuentasclaras.model.enums.BudgetCycleStatus;
import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.repository.AccountRepository;
import com.cuentasclaras.repository.BudgetCategoryRepository;
import com.cuentasclaras.repository.BudgetCycleRepository;
import com.cuentasclaras.repository.FinancialRecordRepository;
import com.cuentasclaras.repository.UserRepository;
import com.cuentasclaras.security.SecurityUtils;
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
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final AccountRepository accountRepository;
    private final BudgetCycleRepository budgetCycleRepository;
    private final SecurityUtils securityUtils;

    @Override
    public FinancialRecord createFinancialRecord(FinancialRecord financialRecord, String userDocumentNumber) {

        log.info("Inicio de validaciones de negocio para crear movimiento financiero");

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        this.applyBussinessValidation(financialRecord);

        User user = userRepository.findById(userDocumentNumber)
                .orElseThrow(() -> new BusinessException("No existe el usuario con número de documento: " + userDocumentNumber));

        try {
            if (financialRecord.getBudgetCategory() != null) {
                BudgetCategory category = budgetCategoryRepository
                        .findById(financialRecord.getBudgetCategory().getId())
                        .orElseThrow(() -> new BusinessException("No existe la categoría de presupuesto con id: " + financialRecord.getBudgetCategory().getId()));
                financialRecord.setBudgetCategory(category);
            }

            financialRecord.setAccount(this.resolveAccount(financialRecord.getAccount(), userDocumentNumber));

            // Se liga el movimiento al ciclo ACTIVO del usuario (si existe). Así los ingresos
            // —que no llevan categoría— también quedan ligados al ciclo y las vistas por ciclo
            // arrancan en 0 al iniciar uno nuevo. Sin ciclo activo, queda sin ciclo (histórico).
            financialRecord.setBudgetCycle(
                    budgetCycleRepository
                            .findByUser_DocumentNumberAndStatus(userDocumentNumber, BudgetCycleStatus.ACTIVE)
                            .orElse(null));

            financialRecord.setUser(user);
            financialRecord.setCreatedAt(new Date());

            if (financialRecord.getRecurring() == null)
                financialRecord.setRecurring(false);

            if (Boolean.FALSE.equals(financialRecord.getRecurring()))
                financialRecord.setPeriodicity(null);

            log.info("Registrando movimiento financiero para el usuario: {}", userDocumentNumber);
            return financialRecordRepository.saveAndFlush(financialRecord);

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

        securityUtils.validateOwnership(existingFinancialRecord.getUser().getDocumentNumber(),
                Constant.DONT_EXIST_FINANCIAL_MOVEMENT_WITH_ID + financialRecordId);

        try {
            existingFinancialRecord.setRecordType(financialRecord.getRecordType());
            existingFinancialRecord.setBudgetCategory(financialRecord.getBudgetCategory());
            existingFinancialRecord.setAccount(this.resolveAccount(
                    financialRecord.getAccount(),
                    existingFinancialRecord.getUser().getDocumentNumber()));
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

        securityUtils.validateOwnership(existingFinancialRecord.getUser().getDocumentNumber(),
                Constant.DONT_EXIST_FINANCIAL_MOVEMENT_WITH_ID + financialRecordId);

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
            FinancialRecord financialRecord = financialRecordRepository.findById(financialRecordId)
                    .orElseThrow(() -> new BusinessException("No existe el movimiento financiero con id: " + financialRecordId));

            securityUtils.validateOwnership(financialRecord.getUser().getDocumentNumber(),
                    Constant.DONT_EXIST_FINANCIAL_MOVEMENT_WITH_ID + financialRecordId);

            return financialRecord;

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

        securityUtils.validateSelf(userDocumentNumber);

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

        securityUtils.validateSelf(userDocumentNumber);

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

        securityUtils.validateSelf(userDocumentNumber);

        try {
            log.info("Consultando movimientos financieros recurrentes del usuario: {}", userDocumentNumber);
            return financialRecordRepository.findByUser_DocumentNumberAndRecurring(userDocumentNumber, true);

        } catch (Exception e) {
            log.error("Error al intentar consultar movimientos financieros recurrentes del usuario", e);
            throw new SystemException("Error al intentar consultar movimientos financieros recurrentes del usuario");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecord> getFinancialRecordsByCycle(Long cycleId) {
        if (cycleId == null)
            throw new BusinessException("El identificador del ciclo es obligatorio");

        BudgetCycle cycle = budgetCycleRepository.findById(cycleId)
                .orElseThrow(() -> new BusinessException("No existe el ciclo con id: " + cycleId));

        securityUtils.validateOwnership(cycle.getUser().getDocumentNumber(),
                "No existe el ciclo con id: " + cycleId);

        try {
            log.info("Consultando movimientos financieros del ciclo: {}", cycleId);
            return financialRecordRepository.findByBudgetCycle_Id(cycleId);

        } catch (Exception e) {
            log.error("Error al intentar consultar movimientos financieros del ciclo", e);
            throw new SystemException("Error al intentar consultar movimientos financieros del ciclo");
        }
    }

    private void applyBussinessValidation(FinancialRecord financialRecord) {
        if (financialRecord == null)
            throw new BusinessException("La información del movimiento financiero es obligatoria");

        if (financialRecord.getRecordType() == null)
            throw new BusinessException("El tipo de movimiento financiero es obligatorio");

        // La categoría ya no es obligatoria en egresos: un movimiento puede ir contra
        // una cuenta sin categoría. Sí exigimos que tenga al menos una de las dos,
        // para que ningún movimiento quede totalmente sin clasificar.
        if (FinancialRecordType.EXPENSE.equals(financialRecord.getRecordType())
                && financialRecord.getBudgetCategory() == null
                && financialRecord.getAccount() == null)
            throw new BusinessException("Un egreso debe tener al menos una categoría de presupuesto o una cuenta");

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

    // Resuelve la cuenta enviada (solo trae el id) a la entidad gestionada,
    // validando que exista y pertenezca al mismo usuario del movimiento.
    private Account resolveAccount(Account account, String userDocumentNumber) {
        if (account == null || account.getAccountId() == null)
            return null;

        Account managed = accountRepository.findById(account.getAccountId())
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_ACCOUNT_WITH_ID + account.getAccountId()));

        if (!managed.getUser().getDocumentNumber().equals(userDocumentNumber))
            throw new BusinessException("La cuenta no pertenece al usuario del movimiento");

        return managed;
    }
}
