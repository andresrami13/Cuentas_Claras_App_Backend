package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de categoría de presupuesto")
public class BudgetCategoryDto {

    @Schema(description = "Identificador único de la categoría", example = "1")
    private Long id;

    @Schema(description = "Identificador del ciclo al que pertenece", example = "1")
    private Long cycleId;

    @Schema(description = "Nombre de la categoría", example = "Alimentación")
    private String categoryName;

    @Schema(description = "Monto asignado a la categoría", example = "500000.00")
    private BigDecimal assignedAmount;

    @Schema(description = "Monto gastado en la categoría", example = "120000.00")
    private BigDecimal spentAmount;

    @Schema(description = "Monto disponible (asignado - gastado)", example = "380000.00")
    private BigDecimal availableAmount;
}
