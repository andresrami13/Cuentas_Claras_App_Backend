package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.FixedBudgetCategory;
import com.cuentasclaras.model.entity.User;
import com.cuentasclaras.model.entity.UserBudgetConfig;
import com.cuentasclaras.repository.FixedBudgetCategoryRepository;
import com.cuentasclaras.repository.UserBudgetConfigRepository;
import com.cuentasclaras.repository.UserRepository;
import com.cuentasclaras.security.SecurityUtils;
import com.cuentasclaras.service.UserBudgetConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserBudgetConfigServiceImpl implements UserBudgetConfigService {

    private final UserBudgetConfigRepository userBudgetConfigRepository;
    private final FixedBudgetCategoryRepository fixedBudgetCategoryRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public UserBudgetConfig saveConfig(UserBudgetConfig config, String userDocumentNumber) {
        log.info("Guardando configuración de presupuesto para el usuario: {}", userDocumentNumber);

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        if (config.getPaymentDay() == null || config.getPaymentDay() < 1 || config.getPaymentDay() > 31)
            throw new BusinessException("El día de pago debe estar entre 1 y 31");

        if (config.getPeriodicity() == null)
            throw new BusinessException("La periodicidad es obligatoria");

        try {
            User user = userRepository.findById(userDocumentNumber)
                    .orElseThrow(() -> new BusinessException("No existe el usuario con número de documento: " + userDocumentNumber));

            Optional<UserBudgetConfig> existing = userBudgetConfigRepository
                    .findByUser_DocumentNumberWithCategories(userDocumentNumber);

            if (existing.isPresent()) {
                UserBudgetConfig existingConfig = existing.get();
                existingConfig.setPaymentDay(config.getPaymentDay());
                existingConfig.setPeriodicity(config.getPeriodicity());
                existingConfig.setNextPaymentDate(config.getNextPaymentDate());
                existingConfig.setUpdatedAt(new Date());

                existingConfig.getFixedCategories().clear();
                if (config.getFixedCategories() != null) {
                    for (FixedBudgetCategory fc : config.getFixedCategories()) {
                        fc.setUserBudgetConfig(existingConfig);
                        existingConfig.getFixedCategories().add(fc);
                    }
                }

                log.info("Actualizando configuración existente para el usuario: {}", userDocumentNumber);
                return userBudgetConfigRepository.saveAndFlush(existingConfig);
            }

            config.setUser(user);
            config.setCreatedAt(new Date());
            if (config.getFixedCategories() != null) {
                for (FixedBudgetCategory fc : config.getFixedCategories()) {
                    fc.setUserBudgetConfig(config);
                }
            }

            log.info("Creando nueva configuración para el usuario: {}", userDocumentNumber);
            return userBudgetConfigRepository.saveAndFlush(config);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al guardar configuración de presupuesto: {}", e.getMessage(), e);
            throw new SystemException("Error al guardar configuración de presupuesto");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserBudgetConfig getConfig(String userDocumentNumber) {
        log.info("Consultando configuración de presupuesto del usuario: {}", userDocumentNumber);

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        try {
            return userBudgetConfigRepository.findByUser_DocumentNumberWithCategories(userDocumentNumber)
                    .orElseThrow(() -> new BusinessException("No existe configuración de presupuesto para el usuario: " + userDocumentNumber));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al consultar configuración de presupuesto: {}", e.getMessage(), e);
            throw new SystemException("Error al consultar configuración de presupuesto");
        }
    }

    @Override
    public void deleteFixedCategory(String userDocumentNumber, Long categoryId) {
        log.info("Eliminando categoría fija {} del usuario: {}", categoryId, userDocumentNumber);

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        if (categoryId == null)
            throw new BusinessException("El identificador de la categoría es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        try {
            UserBudgetConfig config = userBudgetConfigRepository
                    .findByUser_DocumentNumberWithCategories(userDocumentNumber)
                    .orElseThrow(() -> new BusinessException("No existe configuración de presupuesto para el usuario: " + userDocumentNumber));

            FixedBudgetCategory category = fixedBudgetCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException("No existe la categoría fija con id: " + categoryId));

            if (!category.getUserBudgetConfig().getId().equals(config.getId()))
                throw new BusinessException("La categoría fija no pertenece a la configuración del usuario indicado");

            log.info("Eliminando categoría fija id: {} del usuario: {}", categoryId, userDocumentNumber);
            fixedBudgetCategoryRepository.delete(category);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al eliminar categoría fija: {}", e.getMessage(), e);
            throw new SystemException("Error al eliminar categoría fija");
        }
    }
}
