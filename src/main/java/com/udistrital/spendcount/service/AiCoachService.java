package com.udistrital.spendcount.service;

import com.udistrital.spendcount.model.entity.AiCoachRequest;

import java.util.List;

/**
 * Service interface for managing AI financial coach requests.
 */
public interface AiCoachService {

    AiCoachRequest requestFinancialAdvice(String userDocumentNumber, Long financialGoalId, String question);

    AiCoachRequest getAiCoachRequestById(Long aiCoachRequestId);

    List<AiCoachRequest> getAiCoachRequestsByUser(String userDocumentNumber);
}