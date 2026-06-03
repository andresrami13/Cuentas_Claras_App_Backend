package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.FixedBudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedBudgetCategoryRepository extends JpaRepository<FixedBudgetCategory, Long> {
}
