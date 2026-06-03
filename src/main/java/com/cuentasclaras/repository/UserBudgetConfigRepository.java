package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.UserBudgetConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserBudgetConfigRepository extends JpaRepository<UserBudgetConfig, Long> {

    @Query("SELECT c FROM UserBudgetConfig c LEFT JOIN FETCH c.fixedCategories WHERE c.user.documentNumber = :documentNumber")
    Optional<UserBudgetConfig> findByUser_DocumentNumberWithCategories(@Param("documentNumber") String documentNumber);
}
