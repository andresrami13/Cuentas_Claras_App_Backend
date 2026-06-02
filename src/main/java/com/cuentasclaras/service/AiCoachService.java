package com.cuentasclaras.service;

import com.cuentasclaras.model.entity.AiCoachRequest;

import java.util.List;

public interface AiCoachService {

    AiCoachRequest requestFinancialAdvice(String userDocumentNumber, Long financialGoalId, String question);

    AiCoachRequest getAiCoachRequestById(Long aiCoachRequestId);

    List<AiCoachRequest> getAiCoachRequestsByUser(String userDocumentNumber);
}
