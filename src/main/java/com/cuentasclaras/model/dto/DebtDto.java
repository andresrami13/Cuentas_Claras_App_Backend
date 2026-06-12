package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.cuentasclaras.model.enums.DebtStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para registrar, consultar y actualizar deudas de un usuario")
public class DebtDto {

    @Schema(description = "Identificador único de la deuda", example = "1")
    private Long debtId;

    @Schema(description = "Número de documento del usuario dueño de la deuda", example = "1019109757")
    private String userDocumentNumber;

    @Size(max = 150, message = "El acreedor no puede superar 150 caracteres")
    @Schema(description = "Entidad o persona acreedora de la deuda", example = "Banco ABC")
    private String creditor;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    @Schema(description = "Descripción de la deuda", example = "Crédito de libre inversión")
    private String description;

    @Schema(description = "Valor inicial de la deuda", example = "5000000.00")
    private BigDecimal initialAmount;

    @Schema(description = "Saldo pendiente de la deuda", example = "3500000.00")
    private BigDecimal pendingAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(
            description = "Fecha de inicio de la deuda en formato yyyy-MM-dd",
            example = "2026-01-10",
            type = "string",
            format = "date"
    )
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(
            description = "Fecha límite de pago de la deuda en formato yyyy-MM-dd",
            example = "2026-12-10",
            type = "string",
            format = "date"
    )
    private LocalDate dueDate;

    @Schema(description = "Estado actual de la deuda", example = "ACTIVE")
    private DebtStatus status;
}
