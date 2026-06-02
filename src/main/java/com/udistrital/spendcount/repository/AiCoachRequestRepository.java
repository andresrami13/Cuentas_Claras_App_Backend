package com.udistrital.spendcount.repository;

import com.udistrital.spendcount.model.entity.AiCoachRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing AiCoachRequest entities.
 */
public interface AiCoachRequestRepository extends JpaRepository<AiCoachRequest, Long> {

    List<AiCoachRequest> findByUser_DocumentNumber(String userDocumentNumber);

    List<AiCoachRequest> findTop5ByUser_DocumentNumberAndFinancialGoal_FinancialGoalIdOrderByCreatedAtDesc(
            String userDocumentNumber,
            Long financialGoalId
    );
}