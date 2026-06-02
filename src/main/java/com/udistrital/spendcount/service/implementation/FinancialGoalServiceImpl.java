package com.udistrital.spendcount.service.implementation;

import com.udistrital.spendcount.exception.BusinessException;
import com.udistrital.spendcount.exception.SystemException;
import com.udistrital.spendcount.model.entity.FinancialGoal;
import com.udistrital.spendcount.model.entity.User;
import com.udistrital.spendcount.model.enums.FinancialGoalStatus;
import com.udistrital.spendcount.repository.FinancialGoalRepository;
import com.udistrital.spendcount.repository.UserRepository;
import com.udistrital.spendcount.service.FinancialGoalService;
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
public class FinancialGoalServiceImpl implements FinancialGoalService {

    private final FinancialGoalRepository financialGoalRepository;
    private final UserRepository userRepository;

    /**
     * Allows to create a new financial goal for a user.
     *
     * @param financialGoal financial goal info to save
     * @param userDocumentNumber document number of the user owner of the financial goal
     * @return FinancialGoal object saved
     */
    @Override
    public FinancialGoal createFinancialGoal(FinancialGoal financialGoal, String userDocumentNumber) {

        //Business validation
        log.info("Inicio de validaciones de negocio para crear meta financiera");

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException(Constant.USER_DOCUMENT_NUMBER_REQUIRED);

        this.applyBussinessValidation(financialGoal);

        User user = userRepository.findById(userDocumentNumber)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER.concat(userDocumentNumber)));

        try {
            financialGoal.setUser(user);
            financialGoal.setCreatedAt(new Date());

            if (financialGoal.getCurrentAmount() == null)
                financialGoal.setCurrentAmount(BigDecimal.ZERO);

            if (financialGoal.getStatus() == null)
                financialGoal.setStatus(FinancialGoalStatus.ACTIVE);

            log.info("Registrando meta financiera para el usuario: {}", userDocumentNumber);
            return financialGoalRepository.saveAndFlush(financialGoal);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar meta financiera para el usuario: {}", userDocumentNumber, e);
            throw new SystemException("Error al intentar registrar meta financiera");
        }
    }

    /**
     * Allows to update an existing financial goal.
     *
     * @param financialGoalId id of the financial goal to update
     * @param financialGoal financial goal info to update
     * @return FinancialGoal object updated
     */
    @Override
    public FinancialGoal updateFinancialGoal(Long financialGoalId, FinancialGoal financialGoal) {

        //Business validation
        log.info("Inicio de validaciones de negocio para actualizar meta financiera");

        if (financialGoalId == null)
            throw new BusinessException("El identificador de la meta financiera es obligatorio");

        this.applyBussinessValidation(financialGoal);

        FinancialGoal existingFinancialGoal = financialGoalRepository.findById(financialGoalId)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_FINANCIAL_GOAL_WITH_ID.concat(String.valueOf(financialGoalId))));

        try {
            existingFinancialGoal.setName(financialGoal.getName());
            existingFinancialGoal.setDescription(financialGoal.getDescription());
            existingFinancialGoal.setTargetAmount(financialGoal.getTargetAmount());
            existingFinancialGoal.setCurrentAmount(financialGoal.getCurrentAmount());
            existingFinancialGoal.setStartDate(financialGoal.getStartDate());
            existingFinancialGoal.setTargetDate(financialGoal.getTargetDate());
            existingFinancialGoal.setStatus(financialGoal.getStatus());
            existingFinancialGoal.setUpdatedAt(new Date());

            log.info("Actualizando meta financiera con id: {}", financialGoalId);
            return financialGoalRepository.saveAndFlush(existingFinancialGoal);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar actualizar meta financiera con id: {}", financialGoalId, e);
            throw new SystemException("Error al intentar actualizar meta financiera");
        }
    }

    /**
     * Allows to delete a financial goal by id.
     *
     * @param financialGoalId id of the financial goal to delete
     */
    @Override
    public void deleteFinancialGoal(Long financialGoalId) {

        //Business validation
        log.info("Inicio de validaciones de negocio para eliminar meta financiera");

        if (financialGoalId == null)
            throw new BusinessException("El identificador de la meta financiera es obligatorio");

        FinancialGoal existingFinancialGoal = financialGoalRepository.findById(financialGoalId)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_FINANCIAL_GOAL_WITH_ID.concat(String.valueOf(financialGoalId))));

        try {
            log.info("Eliminando meta financiera con id: {}", financialGoalId);
            financialGoalRepository.delete(existingFinancialGoal);

        } catch (Exception e) {
            log.error("Error al intentar eliminar meta financiera con id: {}", financialGoalId, e);
            throw new SystemException("Error al intentar eliminar meta financiera");
        }
    }

    /**
     * Allows to get a financial goal by id.
     *
     * @param financialGoalId id of the financial goal to find
     * @return FinancialGoal object found
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialGoal getFinancialGoalById(Long financialGoalId) {
        if (financialGoalId == null)
            throw new BusinessException("El identificador de la meta financiera es obligatorio");

        try {
            log.info("Consultando meta financiera con id: {}", financialGoalId);
            return financialGoalRepository.findById(financialGoalId)
                    .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_FINANCIAL_GOAL_WITH_ID.concat(String.valueOf(financialGoalId))));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar consultar meta financiera con id: {}", financialGoalId, e);
            throw new SystemException("Error al intentar consultar meta financiera");
        }
    }

    /**
     * Allows to get all financial goals by user.
     *
     * @param userDocumentNumber document number of the user owner of the financial goals
     * @return List of FinancialGoal found
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialGoal> getFinancialGoalsByUser(String userDocumentNumber) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException(Constant.USER_DOCUMENT_NUMBER_REQUIRED);

        try {
            log.info("Consultando metas financieras del usuario: {}", userDocumentNumber);
            return financialGoalRepository.findByUser_DocumentNumber(userDocumentNumber);

        } catch (Exception e) {
            log.error("Error al intentar consultar metas financieras del usuario: {}", userDocumentNumber, e);
            throw new SystemException("Error al intentar consultar metas financieras del usuario");
        }
    }

    /**
     * Allows to get financial goals by user and status.
     *
     * @param userDocumentNumber document number of the user owner of the financial goals
     * @param status status to filter financial goals
     * @return List of FinancialGoal found
     */
    @Override
    @Transactional(readOnly = true)
    public List<FinancialGoal> getFinancialGoalsByUserAndStatus(String userDocumentNumber, FinancialGoalStatus status) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException(Constant.USER_DOCUMENT_NUMBER_REQUIRED);

        if (status == null)
            throw new BusinessException("El estado de la meta financiera es obligatorio");

        try {
            log.info("Consultando metas financieras del usuario: {} por estado: {}", userDocumentNumber, status);
            return financialGoalRepository.findByUser_DocumentNumberAndStatus(userDocumentNumber, status);

        } catch (Exception e) {
            log.error("Error al intentar consultar metas financieras por estado", e);
            throw new SystemException("Error al intentar consultar metas financieras por estado");
        }
    }

    /**
     * Applies business validations for the financial goal entity.
     *
     * @param financialGoal financial goal to validate
     */
    private void applyBussinessValidation(FinancialGoal financialGoal) {
        if (financialGoal == null)
            throw new BusinessException("La información de la meta financiera es obligatoria");

        if (financialGoal.getName() == null || financialGoal.getName().isBlank())
            throw new BusinessException("El nombre de la meta financiera es obligatorio");

        if (financialGoal.getTargetAmount() == null)
            throw new BusinessException("El valor objetivo de la meta financiera es obligatorio");

        if (financialGoal.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("El valor objetivo de la meta financiera debe ser mayor a cero");

        if (financialGoal.getCurrentAmount() != null && financialGoal.getCurrentAmount().compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessException("El valor actual de la meta financiera no puede ser negativo");

        if (financialGoal.getCurrentAmount() != null && financialGoal.getCurrentAmount().compareTo(financialGoal.getTargetAmount()) > 0)
            throw new BusinessException("El valor actual no puede ser mayor al valor objetivo de la meta financiera");

        if (financialGoal.getStartDate() == null)
            throw new BusinessException("La fecha inicial de la meta financiera es obligatoria");

        if (financialGoal.getTargetDate() == null)
            throw new BusinessException("La fecha objetivo de la meta financiera es obligatoria");

        if (!financialGoal.getTargetDate().isAfter(financialGoal.getStartDate()))
            throw new BusinessException("La fecha objetivo debe ser posterior a la fecha inicial de la meta financiera");

        if (financialGoal.getStatus() == null)
            financialGoal.setStatus(FinancialGoalStatus.ACTIVE);

        if (financialGoal.getCurrentAmount() == null)
            financialGoal.setCurrentAmount(BigDecimal.ZERO);
    }
}