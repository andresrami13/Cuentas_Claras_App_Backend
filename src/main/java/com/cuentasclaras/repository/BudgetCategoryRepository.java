package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {
}
