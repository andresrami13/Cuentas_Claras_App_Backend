package com.udistrital.spendcount.service.implementation;

import com.udistrital.spendcount.exception.BusinessException;
import com.udistrital.spendcount.exception.SystemException;
import com.udistrital.spendcount.model.entity.FinancialRecord;
import com.udistrital.spendcount.model.entity.User;
import com.udistrital.spendcount.model.enums.FinancialRecordType;
import com.udistrital.spendcount.repository.FinancialRecordRepository;
import com.udistrital.spendcount.repository.UserRepository;
import com.udistrital.spendcount.service.FinancialRecordService;
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
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    /**
     * Allows to create a new financial record for a user.
     *
     * @param financialRecord   financial record info to save
     * @param userDocumentNumber document number of the user owner of the record
     * @return FinancialRecord object saved
     */
    @Override
    public FinancialRecord createFinancialRecord(FinancialRecord financialRecord, String userDocumentNumber) {

        //Business validation
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
            return financialRecordRepository.saveAndFlush(financialRecord);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar movimiento financiero", e);
            throw new SystemException("Error al intentar registrar movimiento financiero");
        }
    }

    /**
     * Allows to update an existing financial record.
     *
     * @param financialRecordId id of the financial record to update
     * @param financialRecord   financial record info to update
     * @return FinancialRecord object updated
     */
    @Override
    public FinancialRecord updateFinancialRecord(Long financialRecordId, FinancialRecord financialRecord) {

        //Business validation
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

    /**
     * Allows to delete a financial record by id.
     *
     * @param financialRecordId id of the financial record to delete
     */
    @Override
    public void deleteFinancialRecord(Long financialRecordId) {

        //Business validation
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

    /**
     * Allows to get a financial record by id.
     *
     * @param financialRecordId id of the financial record to find
     * @return FinancialRecord object found
     */
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

    /**
     * Allows to get all financial records for a user.
     *
     * @param userDocumentNumber document number of the user owner of the records
     * @return List of FinancialRecord found
     */
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

    /**
     * Allows to get financial records by user and type.
     *
     * @param userDocumentNumber document number of the user owner of the records
     * @param recordType         type of financial record
     * @return List of FinancialRecord found
     */
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

    /**
     * Allows to get recurrent financial records by user.
     *
     * @param userDocumentNumber document number of the user owner of the recurrent records
     * @return List of FinancialRecord found
     */
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

    /**
     * Applies business validations for the financial record entity.
     *
     * @param financialRecord financial record to validate
     */
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