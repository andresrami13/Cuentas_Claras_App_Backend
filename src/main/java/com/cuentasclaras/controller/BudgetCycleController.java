package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.model.dto.BudgetCategoryDto;
import com.cuentasclaras.model.dto.BudgetCycleDto;
import com.cuentasclaras.service.BudgetCycleService;
import com.cuentasclaras.utils.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budget-cycles")
@RequiredArgsConstructor
@Tag(name = "Budget Cycles", description = "Servicios para gestionar ciclos de presupuesto y sus categorías")
public class BudgetCycleController {

    private final BudgetCycleService budgetCycleService;
    private final Mapper mapper;

    @PostMapping
    @Operation(summary = "Crear ciclo de presupuesto", description = "Crea un nuevo ciclo de presupuesto para el usuario. Solo puede existir un ciclo activo a la vez.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ciclo creado exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    public ResponseEntity<ApiResponse<BudgetCycleDto>> createBudgetCycle(@RequestBody BudgetCycleDto budgetCycleDto) {
        BudgetCycleDto response = mapper.toBudgetCycleDto(
                budgetCycleService.createBudgetCycle(
                        mapper.toBudgetCycle(budgetCycleDto),
                        budgetCycleDto.getUserDocumentNumber()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Ciclo de presupuesto creado exitosamente", response));
    }

    @PutMapping("/{cycleId}")
    @Operation(summary = "Actualizar ciclo de presupuesto", description = "Actualiza el día de pago y/o la periodicidad de un ciclo de presupuesto activo. Al cambiar la periodicidad se recalcula la fecha de fin.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ciclo actualizado exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    public ResponseEntity<ApiResponse<BudgetCycleDto>> updateBudgetCycle(
            @Parameter(description = "Identificador del ciclo", example = "1")
            @PathVariable Long cycleId,
            @RequestBody BudgetCycleDto budgetCycleDto) {

        BudgetCycleDto response = mapper.toBudgetCycleDto(
                budgetCycleService.updateBudgetCycle(cycleId, mapper.toBudgetCycle(budgetCycleDto))
        );
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Ciclo de presupuesto actualizado exitosamente", response));
    }

    @GetMapping("/users/{documentNumber}/active")
    @Operation(summary = "Obtener ciclo activo del usuario", description = "Retorna el ciclo de presupuesto activo con sus categorías, montos asignados, gastados y disponibles.")
    public ResponseEntity<ApiResponse<BudgetCycleDto>> getActiveCycle(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String documentNumber) {

        BudgetCycleDto response = mapper.toBudgetCycleDto(budgetCycleService.getActiveCycle(documentNumber));
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Ciclo activo obtenido exitosamente", response));
    }

    @PostMapping("/{cycleId}/categories")
    @Operation(summary = "Agregar categoría al ciclo", description = "Agrega una nueva categoría de gasto al ciclo de presupuesto activo.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoría agregada exitosamente")
    public ResponseEntity<ApiResponse<BudgetCategoryDto>> addCategory(
            @Parameter(description = "Identificador del ciclo", example = "1")
            @PathVariable Long cycleId,
            @RequestBody BudgetCategoryDto budgetCategoryDto) {

        BudgetCategoryDto response = mapper.toBudgetCategoryDto(
                budgetCycleService.addCategory(cycleId, mapper.toBudgetCategory(budgetCategoryDto))
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Categoría agregada exitosamente", response));
    }

    @DeleteMapping("/{cycleId}/categories/{categoryId}")
    @Operation(summary = "Eliminar categoría del ciclo", description = "Elimina una categoría existente del ciclo de presupuesto activo.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @Parameter(description = "Identificador del ciclo", example = "1")
            @PathVariable Long cycleId,
            @Parameter(description = "Identificador de la categoría", example = "1")
            @PathVariable Long categoryId) {

        budgetCycleService.deleteCategory(cycleId, categoryId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Categoría eliminada exitosamente", null));
    }

    @PutMapping("/{cycleId}/categories/{categoryId}")
    @Operation(summary = "Actualizar categoría del ciclo", description = "Actualiza el nombre o los montos de una categoría existente en el ciclo.")
    public ResponseEntity<ApiResponse<BudgetCategoryDto>> updateCategory(
            @Parameter(description = "Identificador del ciclo", example = "1")
            @PathVariable Long cycleId,
            @Parameter(description = "Identificador de la categoría", example = "1")
            @PathVariable Long categoryId,
            @RequestBody BudgetCategoryDto budgetCategoryDto) {

        BudgetCategoryDto response = mapper.toBudgetCategoryDto(
                budgetCycleService.updateCategory(cycleId, categoryId, mapper.toBudgetCategory(budgetCategoryDto))
        );
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Categoría actualizada exitosamente", response));
    }
}
