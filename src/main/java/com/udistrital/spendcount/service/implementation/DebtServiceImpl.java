package com.udistrital.spendcount.service.implementation;

import com.udistrital.spendcount.exception.BusinessException;
import com.udistrital.spendcount.exception.SystemException;
import com.udistrital.spendcount.model.entity.Debt;
import com.udistrital.spendcount.model.entity.User;
import com.udistrital.spendcount.model.enums.DebtStatus;
import com.udistrital.spendcount.repository.DebtRepository;
import com.udistrital.spendcount.repository.UserRepository;
import com.udistrital.spendcount.service.DebtService;
import com.udistrital.spendcount.utils.Constant;
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
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final UserRepository userRepository;

    /**
     * Allows to create a new debt for a user.
     *
     * @param debt               debt info to save
     * @param userDocumentNumber document number of the user owner of the debt
     * @return Debt object saved
     */
    @Override
    public Debt createDebt(Debt debt, String userDocumentNumber) {

        //Business validation
        log.info("Inicio de validaciones de negocio para crear deuda");

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        this.applyBussinessValidation(debt);

        User user = userRepository.findById(userDocumentNumber)
                .orElseThrow(() -> new BusinessException("No existe el usuario con número de documento: " + userDocumentNumber));

        try {
            debt.setUser(user);
            debt.setCreatedAt(new Date());

            if (debt.getPendingAmount() == null)
                debt.setPendingAmount(debt.getInitialAmount());

            if (debt.getStatus() == null)
                debt.setStatus(DebtStatus.ACTIVE);

            log.info("Registrando deuda para el usuario: {}", userDocumentNumber);
            return debtRepository.saveAndFlush(debt);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar deuda", e);
            throw new SystemException("Error al intentar registrar deuda");
        }
    }

    /**
     * Allows to update an existing debt.
     *
     * @param debtId id of the debt to update
     * @param debt   debt info to update
     * @return Debt object updated
     */
    @Override
    public Debt updateDebt(Long debtId, Debt debt) {

        //Business validation
        log.info("Inicio de validaciones de negocio para actualizar deuda");

        if (debtId == null)
            throw new BusinessException("El identificador de la deuda es obligatorio");

        this.applyBussinessValidation(debt);

        Debt existingDebt = debtRepository.findById(debtId)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_DEBT_WITH_ID + debtId));

        try {
            existingDebt.setCreditor(debt.getCreditor());
            existingDebt.setDescription(debt.getDescription());
            existingDebt.setInitialAmount(debt.getInitialAmount());
            existingDebt.setPendingAmount(debt.getPendingAmount());
            existingDebt.setStartDate(debt.getStartDate());
            existingDebt.setDueDate(debt.getDueDate());
            existingDebt.setStatus(debt.getStatus());
            existingDebt.setUpdatedAt(new Date());

            log.info("Actualizando deuda con id: {}", debtId);
            return debtRepository.saveAndFlush(existingDebt);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar actualizar deuda", e);
            throw new SystemException("Error al intentar actualizar deuda");
        }
    }

    /**
     * Allows to delete a debt by id.
     *
     * @param debtId id of the debt to delete
     */
    @Override
    public void deleteDebt(Long debtId) {

        //Business validation
        log.info("Inicio de validaciones de negocio para eliminar deuda");

        if (debtId == null)
            throw new BusinessException("El identificador de la deuda es obligatorio");

        Debt existingDebt = debtRepository.findById(debtId)
                .orElseThrow(() -> new BusinessException("No existe la deuda con id: " + debtId));

        try {
            log.info("Eliminando deuda con id: {}", debtId);
            debtRepository.delete(existingDebt);

        } catch (Exception e) {
            log.error("Error al intentar eliminar deuda", e);
            throw new SystemException("Error al intentar eliminar deuda");
        }
    }

    /**
     * Allows to get a debt by id.
     *
     * @param debtId id of the debt to find
     * @return Debt object found
     */
    @Override
    @Transactional(readOnly = true)
    public Debt getDebtById(Long debtId) {
        if (debtId == null)
            throw new BusinessException("El identificador de la deuda es obligatorio");

        try {
            log.info("Consultando deuda con id: {}", debtId);
            return debtRepository.findById(debtId)
                    .orElseThrow(() -> new BusinessException("No existe la deuda con id: " + debtId));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar consultar deuda", e);
            throw new SystemException("Error al intentar consultar deuda");
        }
    }

    /**
     * Allows to get all debts for a user.
     *
     * @param userDocumentNumber document number of the user owner of the debts
     * @return List of Debt found
     */
    @Override
    @Transactional(readOnly = true)
    public List<Debt> getDebtsByUser(String userDocumentNumber) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        try {
            log.info("Consultando deudas del usuario: {}", userDocumentNumber);
            return debtRepository.findByUser_DocumentNumber(userDocumentNumber);

        } catch (Exception e) {
            log.error("Error al intentar consultar deudas del usuario", e);
            throw new SystemException("Error al intentar consultar deudas del usuario");
        }
    }

    /**
     * Allows to get debts by user and status.
     *
     * @param userDocumentNumber document number of the user owner of the debts
     * @param status             status of debts to find
     * @return List of Debt found
     */
    @Override
    @Transactional(readOnly = true)
    public List<Debt> getDebtsByUserAndStatus(String userDocumentNumber, DebtStatus status) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        if (status == null)
            throw new BusinessException("El estado de la deuda es obligatorio");

        try {
            log.info("Consultando deudas del usuario: {} por estado: {}", userDocumentNumber, status);
            return debtRepository.findByUser_DocumentNumberAndStatus(userDocumentNumber, status);

        } catch (Exception e) {
            log.error("Error al intentar consultar deudas por estado", e);
            throw new SystemException("Error al intentar consultar deudas por estado");
        }
    }

    /**
     * Applies business validations for the debt entity.
     *
     * @param debt debt to validate
     */
    private void applyBussinessValidation(Debt debt) {
        if (debt == null)
            throw new BusinessException("La información de la deuda es obligatoria");

        if (debt.getCreditor() == null || debt.getCreditor().isBlank())
            throw new BusinessException("El acreedor de la deuda es obligatorio");

        if (debt.getInitialAmount() == null)
            throw new BusinessException("El valor inicial de la deuda es obligatorio");

        if (debt.getInitialAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("El valor inicial de la deuda debe ser mayor a cero");

        if (debt.getPendingAmount() != null && debt.getPendingAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessException("El saldo pendiente de la deuda no puede ser negativo");

        if (debt.getPendingAmount() != null && debt.getPendingAmount().compareTo(debt.getInitialAmount()) > 0)
            throw new BusinessException("El saldo pendiente no puede ser mayor al valor inicial de la deuda");

        if (debt.getStartDate() == null)
            throw new BusinessException("La fecha de inicio de la deuda es obligatoria");

        if (debt.getStatus() == null)
            debt.setStatus(DebtStatus.ACTIVE);
    }
}