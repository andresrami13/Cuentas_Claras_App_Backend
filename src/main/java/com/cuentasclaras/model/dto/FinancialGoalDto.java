package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.cuentasclaras.model.enums.FinancialGoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para registrar, consultar y actualizar metas financieras de un usuario")
public class FinancialGoalDto {

    @Schema(description = "Identificador único de la meta financiera", example = "1")
    private Long financialGoalId;

    @Schema(description = "Número de documento del usuario dueño de la meta financiera", example = "1019109757")
    private String userDocumentNumber;

    @Schema(description = "Nombre corto de la meta financiera", example = "Comprar computador")
    private String name;

    @Schema(description = "Descripción detallada de la meta financiera", example = "Ahorrar para comprar un computador portátil")
    private String description;

    @Schema(description = "Valor objetivo de la meta financiera", example = "5000000")
    private BigDecimal targetAmount;

    @Schema(description = "Valor actualmente ahorrado para la meta financiera", example = "500000")
    private BigDecimal currentAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha inicial de la meta financiera", example = "2026-05-23", type = "string", format = "date")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha objetivo para cumplir la meta financiera", example = "2026-12-23", type = "string", format = "date")
    private LocalDate targetDate;

    @Schema(description = "Estado actual de la meta financiera", example = "ACTIVE")
    private FinancialGoalStatus status;
}
