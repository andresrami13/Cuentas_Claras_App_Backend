package com.cuentasclaras.model.dto;

import com.cuentasclaras.model.enums.BudgetCycleStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de ciclo de presupuesto")
public class BudgetCycleDto {

    @Schema(description = "Identificador único del ciclo", example = "1")
    private Long id;

    @Schema(description = "Número de documento del usuario dueño del ciclo", example = "1019109757")
    private String userDocumentNumber;

    @Schema(description = "Fecha de inicio del ciclo", example = "2024-06-01")
    private LocalDate startDate;

    @Schema(description = "Fecha de fin del ciclo", example = "2024-06-30")
    private LocalDate endDate;

    @Schema(description = "Día de pago del usuario (1-31)", example = "15")
    private Integer paymentDay;

    @Schema(description = "Estado del ciclo", example = "ACTIVE")
    private BudgetCycleStatus status;

    @Schema(description = "Categorías del ciclo con montos asignados, gastados y disponibles")
    private List<BudgetCategoryDto> categories;
}
