package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.model.enums.Periodicity;
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
@Schema(description = "DTO para registrar ingresos y egresos de un usuario. Un salario se registra como un ingreso recurrente.")
public class FinancialRecordDto {

    @Schema(description = "Identificador único del movimiento financiero", example = "1")
    private Long financialRecordId;

    @Schema(description = "Número de documento del usuario dueño del movimiento financiero", example = "1019109757")
    private String userDocumentNumber;

    @Schema(
            description = "Tipo de movimiento financiero. Para salario se usa INCOME con recurring en true.",
            example = "INCOME"
    )
    private FinancialRecordType recordType;

    @Schema(description = "Identificador de la categoría de presupuesto. Obligatorio para egresos, opcional para ingresos.", example = "3")
    private Long budgetCategoryId;

    @Schema(description = "Descripción del movimiento financiero", example = "Salario mensual empresa ABC")
    private String description;

    @Schema(description = "Valor del movimiento financiero", example = "3500000.00")
    private BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(
            description = "Fecha del movimiento financiero en formato yyyy-MM-dd",
            example = "2026-05-23",
            type = "string",
            format = "date"
    )
    private LocalDate recordDate;

    @Schema(description = "Indica si el movimiento financiero es recurrente", example = "true")
    private Boolean recurring;

    @Schema(
            description = "Periodicidad del movimiento financiero cuando recurring es true",
            example = "MONTHLY"
    )
    private Periodicity periodicity;
}
