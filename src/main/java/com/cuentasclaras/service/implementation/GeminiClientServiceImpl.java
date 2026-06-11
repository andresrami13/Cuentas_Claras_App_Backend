package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.dto.ConversationTurn;
import com.cuentasclaras.service.GeminiClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiClientServiceImpl implements GeminiClientService {

    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.model}")
    private String geminiModel;

    private final RestClient restClient = RestClient.create();

    @Override
    public String generateFinancialAdvice(String systemContext, String currentQuestion, List<ConversationTurn> history) {
        try {
            log.info("Iniciando petición al modelo de Gemini");

            String url = GEMINI_API_URL.formatted(geminiModel, geminiApiKey);

            List<Map<String, Object>> contents = new java.util.ArrayList<>();
            if (history != null) {
                for (ConversationTurn turn : history) {
                    contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", turn.userMessage()))));
                    contents.add(Map.of("role", "model", "parts", List.of(Map.of("text", turn.modelResponse()))));
                }
            }
            contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", currentQuestion))));

            Map<String, Object> requestBody = Map.of(
                    "system_instruction", Map.of("parts", List.of(Map.of("text", systemContext))),
                    "contents", contents
            );

            Map<?, ?> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            String text = extractText(response);
            log.info("Respuesta generada correctamente por Gemini");
            return text;

        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar generar consejo financiero con Gemini", e);
            throw new SystemException("Error al intentar generar consejo financiero con Gemini");
        }
    }

    @Override
    public boolean validateConnection() {
        try {
            if ("sin-key".equals(geminiApiKey) || geminiApiKey == null || geminiApiKey.isBlank()) {
                log.warn("GEMINI_API_KEY no está configurada");
                return false;
            }
            String testResponse = generateFinancialAdvice("Responde únicamente con la palabra: OK", "OK", List.of());
            return testResponse != null && !testResponse.isBlank();
        } catch (Exception e) {
            log.error("Fallo en la validación de conexión con Gemini", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> response) {
        try {
            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) candidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> part = (Map<?, ?>) parts.get(0);
            return (String) part.get("text");
        } catch (Exception e) {
            throw new SystemException("Gemini no retornó contenido en la respuesta");
        }
    }
}
