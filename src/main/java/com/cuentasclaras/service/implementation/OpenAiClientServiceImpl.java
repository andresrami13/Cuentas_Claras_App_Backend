package com.cuentasclaras.service.implementation;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.service.OpenAiClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OpenAiClientServiceImpl implements OpenAiClientService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.model}")
    private String openAiModel;

    @Override
    public String generateFinancialAdvice(String prompt) {
        try {
            log.info("Iniciando petición al modelo de OpenAI");

            OpenAIClient client = OpenAIOkHttpClient.builder()
                    .apiKey(openAiApiKey)
                    .build();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .model(openAiModel)
                    .addSystemMessage("""
                            Eres un coach financiero para una aplicación web de gestión financiera personal.
                            Responde siempre en español.
                            Da recomendaciones prácticas, responsables y claras.
                            No prometas resultados financieros.
                            No recomiendes inversiones específicas.
                            No inventes información que no esté en el contexto.
                            """)
                    .addUserMessage(prompt)
                    .build();

            ChatCompletion chatCompletion = client.chat().completions().create(params);

            String response = chatCompletion.choices()
                    .get(0)
                    .message()
                    .content()
                    .orElseThrow(() -> new SystemException("OpenAI no retornó contenido en la respuesta"));

            log.info("Respuesta generada correctamente por OpenAI");
            return response;

        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar generar consejo financiero con OpenAI", e);
            throw new SystemException("Error al intentar generar consejo financiero con OpenAI");
        }
    }
}
