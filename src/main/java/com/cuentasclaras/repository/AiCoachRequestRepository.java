package com.cuentasclaras.repository;

import com.cuentasclaras.model.entity.AiCoachRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiCoachRequestRepository extends JpaRepository<AiCoachRequest, Long> {

    List<AiCoachRequest> findByUser_DocumentNumber(String userDocumentNumber);

    List<AiCoachRequest> findTop5ByUser_DocumentNumberAndFinancialGoal_FinancialGoalIdOrderByCreatedAtDesc(
            String userDocumentNumber,
            Long financialGoalId
    );

    void deleteByFinancialGoal_FinancialGoalId(Long financialGoalId);
}
