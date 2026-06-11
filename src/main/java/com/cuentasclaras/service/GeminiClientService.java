package com.cuentasclaras.service;

import com.cuentasclaras.model.dto.ConversationTurn;

import java.util.List;

public interface GeminiClientService {

    String generateFinancialAdvice(String systemContext, String currentQuestion, List<ConversationTurn> history);

    boolean validateConnection();
}
