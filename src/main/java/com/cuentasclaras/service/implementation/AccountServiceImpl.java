package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.Account;
import com.cuentasclaras.model.entity.User;
import com.cuentasclaras.model.enums.AccountType;
import com.cuentasclaras.repository.AccountRepository;
import com.cuentasclaras.repository.UserRepository;
import com.cuentasclaras.security.SecurityUtils;
import com.cuentasclaras.service.AccountService;
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
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Account createAccount(Account account, String userDocumentNumber) {

        log.info("Inicio de validaciones de negocio para crear cuenta");

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        this.applyBussinessValidation(account);

        User user = userRepository.findById(userDocumentNumber)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER + userDocumentNumber));

        try {
            account.setUser(user);
            account.setCreatedAt(new Date());

            if (account.getArchived() == null)
                account.setArchived(false);

            log.info("Registrando cuenta para el usuario: {}", userDocumentNumber);
            return accountRepository.saveAndFlush(account);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar registrar cuenta", e);
            throw new SystemException("Error al intentar registrar cuenta");
        }
    }

    @Override
    public Account updateAccount(Long accountId, Account account) {

        log.info("Inicio de validaciones de negocio para actualizar cuenta");

        if (accountId == null)
            throw new BusinessException("El identificador de la cuenta es obligatorio");

        this.applyBussinessValidation(account);

        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_ACCOUNT_WITH_ID + accountId));

        securityUtils.validateOwnership(existingAccount.getUser().getDocumentNumber(),
                Constant.DONT_EXIST_ACCOUNT_WITH_ID + accountId);

        try {
            existingAccount.setName(account.getName());
            existingAccount.setType(account.getType());
            existingAccount.setProvider(account.getProvider());
            existingAccount.setInitialBalance(account.getInitialBalance());
            existingAccount.setColor(account.getColor());
            existingAccount.setIcon(account.getIcon());
            if (account.getArchived() != null)
                existingAccount.setArchived(account.getArchived());
            existingAccount.setUpdatedAt(new Date());

            log.info("Actualizando cuenta con id: {}", accountId);
            return accountRepository.saveAndFlush(existingAccount);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar actualizar cuenta", e);
            throw new SystemException("Error al intentar actualizar cuenta");
        }
    }

    @Override
    public void deleteAccount(Long accountId) {

        log.info("Inicio de validaciones de negocio para eliminar cuenta");

        if (accountId == null)
            throw new BusinessException("El identificador de la cuenta es obligatorio");

        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_ACCOUNT_WITH_ID + accountId));

        securityUtils.validateOwnership(existingAccount.getUser().getDocumentNumber(),
                Constant.DONT_EXIST_ACCOUNT_WITH_ID + accountId);

        try {
            log.info("Eliminando cuenta con id: {}", accountId);
            accountRepository.delete(existingAccount);

        } catch (Exception e) {
            log.error("Error al intentar eliminar cuenta", e);
            throw new SystemException("Error al intentar eliminar cuenta");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        if (accountId == null)
            throw new BusinessException("El identificador de la cuenta es obligatorio");

        try {
            log.info("Consultando cuenta con id: {}", accountId);
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_ACCOUNT_WITH_ID + accountId));

            securityUtils.validateOwnership(account.getUser().getDocumentNumber(),
                    Constant.DONT_EXIST_ACCOUNT_WITH_ID + accountId);

            return account;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar consultar cuenta", e);
            throw new SystemException("Error al intentar consultar cuenta");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByUser(String userDocumentNumber) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException("El número de documento del usuario es obligatorio");

        securityUtils.validateSelf(userDocumentNumber);

        try {
            log.info("Consultando cuentas del usuario: {}", userDocumentNumber);
            return accountRepository.findByUser_DocumentNumber(userDocumentNumber);

        } catch (Exception e) {
            log.error("Error al intentar consultar cuentas del usuario", e);
            throw new SystemException("Error al intentar consultar cuentas del usuario");
        }
    }

    private void applyBussinessValidation(Account account) {
        if (account == null)
            throw new BusinessException("La información de la cuenta es obligatoria");

        if (account.getName() == null || account.getName().isBlank())
            throw new BusinessException("El nombre de la cuenta es obligatorio");

        if (account.getType() == null)
            account.setType(AccountType.OTHER);

        if (account.getInitialBalance() == null)
            account.setInitialBalance(BigDecimal.ZERO);

        if (account.getInitialBalance().compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessException("El saldo inicial de la cuenta no puede ser negativo");
    }
}
