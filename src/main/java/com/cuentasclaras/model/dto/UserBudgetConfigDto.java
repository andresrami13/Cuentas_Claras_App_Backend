package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de configuración de presupuesto del usuario")
public class UserBudgetConfigDto {

    @Schema(description = "Identificador único de la configuración", example = "1")
    private Long id;

    @Schema(description = "Número de documento del usuario", example = "1019109757")
    private String userDocumentNumber;

    @Schema(description = "Día del mes en que el usuario recibe su pago (1-31)", example = "15")
    private Integer paymentDay;

    @Schema(description = "Fecha del próximo pago", example = "2024-07-15")
    private LocalDate nextPaymentDate;
}
