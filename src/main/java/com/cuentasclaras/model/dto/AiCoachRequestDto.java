package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para solicitar y consultar consejos financieros generados por el coach IA")
public class AiCoachRequestDto {

    @Schema(description = "Identificador único de la solicitud al coach IA", example = "1")
    private Long aiCoachRequestId;

    @Schema(description = "Número de documento del usuario que solicita el consejo financiero", example = "1019109757")
    private String userDocumentNumber;

    @Schema(description = "Identificador de la meta financiera asociada a la solicitud", example = "1")
    private Long financialGoalId;

    @Schema(
            description = "Pregunta realizada por el usuario al coach financiero IA",
            example = "¿Qué debo hacer para cumplir mi meta de ahorro?"
    )
    private String question;

    @Schema(description = "Respuesta generada por el coach financiero IA")
    private String aiResponse;

    @Schema(description = "Modelo de OpenAI utilizado para generar la respuesta", example = "gpt-5.2")
    private String model;
}
