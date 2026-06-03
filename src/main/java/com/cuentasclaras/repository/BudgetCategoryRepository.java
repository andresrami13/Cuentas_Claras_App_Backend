package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {
    Optional<BudgetCategory> findByCycle_IdAndCategoryNameIgnoreCase(Long cycleId, String categoryName);
}
