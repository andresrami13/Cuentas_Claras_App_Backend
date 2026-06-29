package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUser_DocumentNumber(String userDocumentNumber);

    List<Account> findByUser_DocumentNumberAndArchived(String userDocumentNumber, Boolean archived);
}
