package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.Account;

import java.util.List;

public interface AccountService {

    Account createAccount(Account account, String userDocumentNumber);

    Account updateAccount(Long accountId, Account account);

    void deleteAccount(Long accountId);

    Account getAccountById(Long accountId);

    List<Account> getAccountsByUser(String userDocumentNumber);
}
