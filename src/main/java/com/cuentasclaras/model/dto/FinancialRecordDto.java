package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.cuentasclaras.model.enums.FinancialRecordType;
import com.cuentasclaras.model.enums.Periodicity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

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

    @Schema(description = "Identificador de la categoría de presupuesto. Opcional: un egreso puede ir contra una cuenta sin categoría.", example = "3")
    private Long budgetCategoryId;

    @Schema(description = "Identificador de la cuenta de dinero (banco, billetera, efectivo) a la que pertenece el movimiento. Opcional.", example = "2")
    private Long accountId;

    @Schema(description = "Identificador del ciclo de presupuesto al que quedó ligado el movimiento (solo lectura). Se estampa con el ciclo activo al crear el ingreso/egreso.", example = "5")
    private Long budgetCycleId;

    @Size(max = 250, message = "La descripción no puede superar 250 caracteres")
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(
            description = "Fecha y hora de creación del movimiento (solo lectura). Permite ordenar por hora.",
            example = "2026-05-23T14:35:10",
            type = "string"
    )
    private Date createdAt;
}
