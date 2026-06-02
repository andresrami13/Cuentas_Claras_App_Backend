package com.udistrital.spendcount.service;

/**
 * Service interface for integrating with OpenAI.
 */
public interface OpenAiClientService {

    String generateFinancialAdvice(String prompt);
}