package com.cuentasclaras.service.implementation;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.cuentasclaras.model.entity.AiCoachRequest;
import com.cuentasclaras.model.entity.Debt;
import com.cuentasclaras.model.entity.FinancialGoal;
import com.cuentasclaras.model.entity.FinancialRecord;
import com.cuentasclaras.model.entity.User;
import com.cuentasclaras.model.enums.DebtStatus;
import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.repository.AiCoachRequestRepository;
import com.cuentasclaras.repository.DebtRepository;
import com.cuentasclaras.repository.FinancialGoalRepository;
import com.cuentasclaras.repository.FinancialRecordRepository;
import com.cuentasclaras.repository.UserRepository;
import com.cuentasclaras.service.AiCoachService;
import com.cuentasclaras.service.OpenAiClientService;
import com.cuentasclaras.utils.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AiCoachServiceImpl implements AiCoachService {

    private final AiCoachRequestRepository aiCoachRequestRepository;
    private final UserRepository userRepository;
    private final FinancialGoalRepository financialGoalRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final DebtRepository debtRepository;
    private final OpenAiClientService openAiClientService;

    @Value("${openai.model}")
    private String openAiModel;

    @Override
    public AiCoachRequest requestFinancialAdvice(String userDocumentNumber, Long financialGoalId, String question) {

        log.info("Inicio de validaciones de negocio para solicitar consejo financiero IA");

        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException(Constant.USER_DOCUMENT_NUMBER_REQUIRED);

        if (financialGoalId == null)
            throw new BusinessException("El identificador de la meta financiera es obligatorio");

        if (question == null || question.isBlank())
            throw new BusinessException("La pregunta para el coach financiero IA es obligatoria");

        User user = userRepository.findById(userDocumentNumber)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_USER_WITH_DOCUMENT_NUMBER.concat(userDocumentNumber)));

        FinancialGoal financialGoal = financialGoalRepository.findById(financialGoalId)
                .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_FINANCIAL_GOAL_WITH_ID.concat(String.valueOf(financialGoalId))));

        if (!financialGoal.getUser().getDocumentNumber().equals(userDocumentNumber))
            throw new BusinessException("La meta financiera no pertenece al usuario indicado");

        try {
            log.info("Construyendo contexto financiero para el usuario: {}", userDocumentNumber);

            List<AiCoachRequest> previousAiCoachRequests = aiCoachRequestRepository
                    .findTop5ByUser_DocumentNumberAndFinancialGoal_FinancialGoalIdOrderByCreatedAtDesc(
                            userDocumentNumber,
                            financialGoalId
                    );

            Collections.reverse(previousAiCoachRequests);

            String financialContext = this.buildFinancialContext(
                    userDocumentNumber,
                    financialGoal,
                    previousAiCoachRequests,
                    question
            );

            String prompt = this.buildPrompt(financialContext, question);

            log.info("Solicitando recomendación financiera al coach IA para el usuario: {}", userDocumentNumber);
            String aiResponse = openAiClientService.generateFinancialAdvice(prompt);

            AiCoachRequest aiCoachRequest = AiCoachRequest.builder()
                    .user(user)
                    .financialGoal(financialGoal)
                    .question(question)
                    .financialContext(financialContext)
                    .aiResponse(aiResponse)
                    .model(openAiModel)
                    .createdAt(new Date())
                    .build();

            log.info("Guardando respuesta del coach IA para el usuario: {}", userDocumentNumber);
            return aiCoachRequestRepository.saveAndFlush(aiCoachRequest);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar generar consejo financiero IA para el usuario: {}", userDocumentNumber, e);
            throw new SystemException("Error al intentar generar consejo financiero IA");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AiCoachRequest getAiCoachRequestById(Long aiCoachRequestId) {
        if (aiCoachRequestId == null)
            throw new BusinessException("El identificador de la solicitud al coach IA es obligatorio");

        try {
            log.info("Consultando solicitud al coach IA con id: {}", aiCoachRequestId);
            return aiCoachRequestRepository.findById(aiCoachRequestId)
                    .orElseThrow(() -> new BusinessException(Constant.DONT_EXIST_AI_COACH_REQUEST_WITH_ID.concat(String.valueOf(aiCoachRequestId))));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar consultar solicitud al coach IA con id: {}", aiCoachRequestId, e);
            throw new SystemException("Error al intentar consultar solicitud al coach IA");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiCoachRequest> getAiCoachRequestsByUser(String userDocumentNumber) {
        if (userDocumentNumber == null || userDocumentNumber.isBlank())
            throw new BusinessException(Constant.USER_DOCUMENT_NUMBER_REQUIRED);

        try {
            log.info("Consultando solicitudes al coach IA del usuario: {}", userDocumentNumber);
            return aiCoachRequestRepository.findByUser_DocumentNumber(userDocumentNumber);

        } catch (Exception e) {
            log.error("Error al intentar consultar solicitudes al coach IA del usuario: {}", userDocumentNumber, e);
            throw new SystemException("Error al intentar consultar solicitudes al coach IA del usuario");
        }
    }

    private String buildFinancialContext(
            String userDocumentNumber,
            FinancialGoal financialGoal,
            List<AiCoachRequest> previousAiCoachRequests,
            String question
    ) {
        List<FinancialRecord> incomes = financialRecordRepository.findByUser_DocumentNumberAndRecordType(
                userDocumentNumber,
                FinancialRecordType.INCOME
        );

        List<FinancialRecord> expenses = financialRecordRepository.findByUser_DocumentNumberAndRecordType(
                userDocumentNumber,
                FinancialRecordType.EXPENSE
        );

        List<FinancialRecord> recurringRecords = financialRecordRepository.findByUser_DocumentNumberAndRecurring(
                userDocumentNumber,
                true
        );

        List<Debt> activeDebts = debtRepository.findByUser_DocumentNumberAndStatus(
                userDocumentNumber,
                DebtStatus.ACTIVE
        );

        BigDecimal totalIncome = incomes.stream()
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = expenses.stream()
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActiveDebt = activeDebts.stream()
                .map(Debt::getPendingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal estimatedSavingCapacity = totalIncome.subtract(totalExpense);

        BigDecimal remainingGoalAmount = financialGoal.getTargetAmount().subtract(financialGoal.getCurrentAmount());

        long monthsToGoal = ChronoUnit.MONTHS.between(
                financialGoal.getStartDate().withDayOfMonth(1),
                financialGoal.getTargetDate().withDayOfMonth(1)
        );

        if (monthsToGoal <= 0)
            monthsToGoal = 1;

        BigDecimal requiredMonthlySaving = remainingGoalAmount
                .divide(BigDecimal.valueOf(monthsToGoal), 2, RoundingMode.HALF_UP);

        StringBuilder context = new StringBuilder();

        context.append("Resumen financiero actual del usuario:\n");
        context.append("- Total de ingresos registrados: ").append(totalIncome).append("\n");
        context.append("- Total de egresos registrados: ").append(totalExpense).append("\n");
        context.append("- Capacidad de ahorro estimada: ").append(estimatedSavingCapacity).append("\n");
        context.append("- Total de deudas activas pendientes: ").append(totalActiveDebt).append("\n");
        context.append("- Cantidad de ingresos registrados: ").append(incomes.size()).append("\n");
        context.append("- Cantidad de egresos registrados: ").append(expenses.size()).append("\n");
        context.append("- Cantidad de movimientos recurrentes: ").append(recurringRecords.size()).append("\n");
        context.append("- Cantidad de deudas activas: ").append(activeDebts.size()).append("\n\n");

        context.append("Meta financiera asociada a esta conversación:\n");
        context.append("- Id de la meta: ").append(financialGoal.getFinancialGoalId()).append("\n");
        context.append("- Nombre: ").append(financialGoal.getName()).append("\n");
        context.append("- Descripción: ").append(financialGoal.getDescription()).append("\n");
        context.append("- Valor objetivo: ").append(financialGoal.getTargetAmount()).append("\n");
        context.append("- Valor actual: ").append(financialGoal.getCurrentAmount()).append("\n");
        context.append("- Valor restante: ").append(remainingGoalAmount).append("\n");
        context.append("- Fecha inicial: ").append(financialGoal.getStartDate()).append("\n");
        context.append("- Fecha objetivo: ").append(financialGoal.getTargetDate()).append("\n");
        context.append("- Meses aproximados para cumplir la meta: ").append(monthsToGoal).append("\n");
        context.append("- Ahorro mensual requerido aproximado: ").append(requiredMonthlySaving).append("\n\n");

        context.append("Movimientos recurrentes registrados:\n");
        if (recurringRecords.isEmpty()) {
            context.append("- No hay movimientos recurrentes registrados.\n");
        } else {
            recurringRecords.forEach(recordUnit -> context.append("- ")
                    .append(recordUnit.getRecordType())
                    .append(" | ")
                    .append(recordUnit.getBudgetCategory() != null ? recordUnit.getBudgetCategory().getCategoryName() : "Sin categoría")
                    .append(" | Valor: ")
                    .append(recordUnit.getAmount())
                    .append(" | Periodicidad: ")
                    .append(recordUnit.getPeriodicity())
                    .append("\n"));
        }

        context.append("\nDeudas activas registradas:\n");
        if (activeDebts.isEmpty()) {
            context.append("- No hay deudas activas registradas.\n");
        } else {
            activeDebts.forEach(debt -> context.append("- ")
                    .append(debt.getCreditor())
                    .append(" | ")
                    .append(debt.getDescription())
                    .append(" | Saldo pendiente: ")
                    .append(debt.getPendingAmount())
                    .append(" | Fecha límite: ")
                    .append(debt.getDueDate())
                    .append("\n"));
        }

        context.append("\nHistorial de conversaciones anteriores con el coach IA para esta misma meta financiera:\n");
        if (previousAiCoachRequests == null || previousAiCoachRequests.isEmpty()) {
            context.append("- No existen solicitudes anteriores relacionadas con esta meta financiera.\n");
        } else {
            int counter = 1;
            for (AiCoachRequest previousRequest : previousAiCoachRequests) {
                context.append("\nInteracción anterior ").append(counter).append(":\n");
                context.append("- Fecha: ").append(previousRequest.getCreatedAt()).append("\n");
                context.append("- Pregunta anterior del usuario: ").append(previousRequest.getQuestion()).append("\n");
                context.append("- Respuesta anterior del coach IA: ").append(this.limitText(previousRequest.getAiResponse(), 1500)).append("\n");
                counter++;
            }
        }

        context.append("\nPregunta actual del usuario:\n");
        context.append(question);

        return context.toString();
    }

    private String buildPrompt(String financialContext, String question) {
        return """
                Eres un coach financiero para una aplicación web de gestión financiera personal.

                Tu tarea es analizar el comportamiento financiero del usuario usando ingresos, egresos, deudas activas,
                movimientos recurrentes, salario registrado como ingreso recurrente, una meta financiera registrada
                y el historial de conversaciones anteriores relacionadas con esa misma meta.

                Reglas importantes:
                - Responde siempre en español.
                - Da recomendaciones prácticas, claras y responsables.
                - No prometas resultados financieros.
                - No recomiendes inversiones específicas.
                - No des asesoría financiera riesgosa.
                - No uses lenguaje alarmista.
                - Explica con claridad si la meta parece viable, difícil o requiere ajustes.
                - Usa el contexto financiero entregado por el sistema.
                - Usa el historial anterior para no repetir exactamente las mismas recomendaciones.
                - Si ya diste una recomendación antes, puedes darle continuidad o mejorarla.
                - No inventes ingresos, egresos, deudas o metas no proporcionadas.
                - No menciones que eres un modelo de lenguaje.
                - No solicites datos que ya están en el contexto financiero.

                Instrucciones sobre continuidad:
                - Revisa las conversaciones anteriores relacionadas con la misma meta financiera.
                - Identifica si el usuario ya recibió un plan previo.
                - Si existe un plan previo, evalúa si el comportamiento financiero actual permite mantenerlo o ajustarlo.
                - Propón mejoras frente a recomendaciones anteriores.
                - Evita repetir literalmente la misma respuesta anterior.
                - Mantén coherencia con las recomendaciones pasadas, salvo que los datos actuales indiquen que se deben cambiar.

                Estructura de la respuesta:
                1. Diagnóstico breve actualizado.
                2. Análisis de la meta financiera.
                3. Comparación con recomendaciones anteriores, si existen.
                4. Plan mensual recomendado.
                5. Recomendaciones concretas.
                6. Alertas o riesgos.
                7. Conclusión motivadora.

                Contexto financiero:
                %s

                Pregunta actual del usuario:
                %s
                """.formatted(financialContext, question);
    }

    private String limitText(String text, int maxLength) {
        if (text == null)
            return "";

        if (text.length() <= maxLength)
            return text;

        return text.substring(0, maxLength).concat("...");
    }
}
