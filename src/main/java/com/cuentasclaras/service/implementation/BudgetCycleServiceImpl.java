package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.BudgetCategory;
import com.cuentasclaras.model.entity.BudgetCycle;
import com.cuentasclaras.model.entity.FinancialRecord;
import com.cuentasclaras.model.entity.User;
import com.cuentasclaras.model.enums.BudgetCycleStatus;
import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.model.enums.Periodicity;
import com.cuentasclaras.repository.BudgetCategoryRepository;
import com.cuentasclaras.repository.BudgetCycleRepository;
import com.cuentasclaras.repository.FinancialRecordRepository;
import com.cuentasclaras.repository.UserRepository;
import com.cuentasclaras.security.SecurityUtils;
import com.cuentasclaras.service.BudgetCycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BudgetCycleServiceImpl implements BudgetCycleService {

    private final BudgetCycleRepository budgetCycleRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final UserRepository userRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final SecurityUtils securityUtils;

    @Override
    public BudgetCycle createBudgetCycle(BudgetCycle budgetCycle, String userDocumentNumber) {
        log.info("Inicio de validaciones para crear ciclo de presupuesto");

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        if (budgetCycle.getPaymentDay() == null || budgetCycle.getPaymentDay() < 1 || budgetCycle.getPaymentDay() > 31)
            throw new BusinessException("El día de pago debe estar entre 1 y 31");

        if (budgetCycle.getPeriodicity() == null)
            throw new BusinessException("La periodicidad del ciclo es obligatoria");

        if (!isSupportedPeriodicity(budgetCycle.getPeriodicity()))
            throw new BusinessException("La periodicidad debe ser WEEKLY, BIWEEKLY o MONTHLY");

        try {
            User user = userRepository.findById(userDocumentNumber)
                    .orElseThrow(() -> new BusinessException("No existe el usuario con número de documento: " + userDocumentNumber));

            if (budgetCycleRepository.existsByUser_DocumentNumberAndStatus(userDocumentNumber, BudgetCycleStatus.ACTIVE))
                throw new BusinessException("El usuario ya tiene un ciclo de presupuesto activo");

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = calculateEndDate(startDate, budgetCycle.getPeriodicity());

            budgetCycle.setStartDate(startDate);
            budgetCycle.setEndDate(endDate);
            budgetCycle.setUser(user);
            budgetCycle.setStatus(BudgetCycleStatus.ACTIVE);
            budgetCycle.setCreatedAt(new Date());

            log.info("Creando ciclo de presupuesto para el usuario: {}", userDocumentNumber);
            return budgetCycleRepository.saveAndFlush(budgetCycle);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar crear ciclo de presupuesto: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar crear ciclo de presupuesto");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetCycle getActiveCycle(String userDocumentNumber) {
        log.info("Consultando ciclo activo del usuario: {}", userDocumentNumber);

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        try {
            BudgetCycle cycle = budgetCycleRepository.findByUser_DocumentNumberAndStatus(userDocumentNumber, BudgetCycleStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException("No existe ciclo de presupuesto activo para el usuario: " + userDocumentNumber));

            if (cycle.getCategories() != null)
                cycle.getCategories().forEach(c -> c.setSpentAmount(calculateSpentAmount(c.getId())));

            return cycle;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar consultar ciclo activo: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar consultar ciclo activo");
        }
    }

    @Override
    public BudgetCategory addCategory(Long cycleId, BudgetCategory category) {
        log.info("Agregando categoría al ciclo: {}", cycleId);

        if (cycleId == null)
            throw new BusinessException("El identificador del ciclo es obligatorio");

        if (category.getCategoryName() == null || category.getCategoryName().isBlank())
            throw new BusinessException("El nombre de la categoría es obligatorio");

        if (category.getAssignedAmount() == null || category.getAssignedAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("El monto asignado debe ser mayor a cero");

        try {
            BudgetCycle cycle = budgetCycleRepository.findById(cycleId)
                    .orElseThrow(() -> new BusinessException("No existe el ciclo con id: " + cycleId));

            securityUtils.validateOwnership(cycle.getUser().getDocumentNumber(),
                    "No existe el ciclo con id: " + cycleId);

            if (!BudgetCycleStatus.ACTIVE.equals(cycle.getStatus()))
                throw new BusinessException("Solo se pueden agregar categorías a un ciclo activo");

            category.setCycle(cycle);
            category.setCreatedAt(new Date());

            log.info("Registrando categoría en el ciclo: {}", cycleId);
            BudgetCategory saved = budgetCategoryRepository.saveAndFlush(category);
            saved.setSpentAmount(calculateSpentAmount(saved.getId()));
            return saved;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar agregar categoría al ciclo: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar agregar categoría al ciclo");
        }
    }

    @Override
    public BudgetCategory updateCategory(Long cycleId, Long categoryId, BudgetCategory category) {
        log.info("Actualizando categoría {} del ciclo: {}", categoryId, cycleId);

        if (cycleId == null)
            throw new BusinessException("El identificador del ciclo es obligatorio");

        if (categoryId == null)
            throw new BusinessException("El identificador de la categoría es obligatorio");

        if (category.getAssignedAmount() != null && category.getAssignedAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("El monto asignado debe ser mayor a cero");

        try {
            BudgetCycle cycle = budgetCycleRepository.findById(cycleId)
                    .orElseThrow(() -> new BusinessException("No existe el ciclo con id: " + cycleId));

            securityUtils.validateOwnership(cycle.getUser().getDocumentNumber(),
                    "No existe el ciclo con id: " + cycleId);

            if (!BudgetCycleStatus.ACTIVE.equals(cycle.getStatus()))
                throw new BusinessException("Solo se pueden modificar categorías de un ciclo activo");

            BudgetCategory existing = budgetCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException("No existe la categoría con id: " + categoryId));

            if (!existing.getCycle().getId().equals(cycleId))
                throw new BusinessException("La categoría no pertenece al ciclo indicado");

            if (category.getCategoryName() != null && !category.getCategoryName().isBlank())
                existing.setCategoryName(category.getCategoryName());

            if (category.getAssignedAmount() != null)
                existing.setAssignedAmount(category.getAssignedAmount());

            existing.setUpdatedAt(new Date());

            log.info("Actualizando categoría id: {} del ciclo: {}", categoryId, cycleId);
            BudgetCategory saved = budgetCategoryRepository.saveAndFlush(existing);
            saved.setSpentAmount(calculateSpentAmount(saved.getId()));
            return saved;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar actualizar categoría: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar actualizar categoría");
        }
    }

    /**
     * Suma en memoria los gastos (EXPENSE) asociados a la categoría. Reemplaza la
     * antigua suma por SQL (@Formula), imposible ahora que los montos están cifrados.
     */
    private BigDecimal calculateSpentAmount(Long categoryId) {
        return financialRecordRepository
                .findByBudgetCategory_IdAndRecordType(categoryId, FinancialRecordType.EXPENSE)
                .stream()
                .map(FinancialRecord::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void deleteCategory(Long cycleId, Long categoryId) {
        log.info("Eliminando categoría {} del ciclo: {}", categoryId, cycleId);

        if (cycleId == null)
            throw new BusinessException("El identificador del ciclo es obligatorio");

        if (categoryId == null)
            throw new BusinessException("El identificador de la categoría es obligatorio");

        try {
            BudgetCycle cycle = budgetCycleRepository.findById(cycleId)
                    .orElseThrow(() -> new BusinessException("No existe el ciclo con id: " + cycleId));

            securityUtils.validateOwnership(cycle.getUser().getDocumentNumber(),
                    "No existe el ciclo con id: " + cycleId);

            if (!BudgetCycleStatus.ACTIVE.equals(cycle.getStatus()))
                throw new BusinessException("Solo se pueden eliminar categorías de un ciclo activo");

            BudgetCategory existing = budgetCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException("No existe la categoría con id: " + categoryId));

            if (!existing.getCycle().getId().equals(cycleId))
                throw new BusinessException("La categoría no pertenece al ciclo indicado");

            log.info("Eliminando categoría id: {} del ciclo: {}", categoryId, cycleId);
            budgetCategoryRepository.delete(existing);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar eliminar categoría: {}", e.getMessage(), e);
            throw new SystemException("Error al intentar eliminar categoría");
        }
    }

    private boolean isSupportedPeriodicity(Periodicity periodicity) {
        return periodicity == Periodicity.WEEKLY
                || periodicity == Periodicity.BIWEEKLY
                || periodicity == Periodicity.MONTHLY;
    }

    private LocalDate calculateEndDate(LocalDate startDate, Periodicity periodicity) {
        return switch (periodicity) {
            case WEEKLY -> startDate.plusDays(6);
            case BIWEEKLY -> startDate.plusDays(13);
            case MONTHLY -> startDate.plusMonths(1).minusDays(1);
            default -> throw new BusinessException("Periodicidad no soportada: " + periodicity);
        };
    }
}
