package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.model.dto.FinancialGoalDto;
import com.cuentasclaras.model.enums.FinancialGoalStatus;
import com.cuentasclaras.service.FinancialGoalService;
import com.cuentasclaras.utils.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financial-goals")
@RequiredArgsConstructor
@Tag(name = "Financial Goals", description = "Servicios para registrar, consultar y administrar metas financieras de usuarios")
public class FinancialGoalController {

    private final FinancialGoalService financialGoalService;
    private final Mapper mapper;

    @PostMapping
    @Operation(summary = "Crear meta financiera", description = "Registra una meta financiera asociada a un usuario.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Meta financiera creada exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<FinancialGoalDto>> createFinancialGoal(@Valid @RequestBody FinancialGoalDto financialGoalDto) {
        FinancialGoalDto response = mapper.toFinancialGoalDto(
                financialGoalService.createFinancialGoal(
                        mapper.toFinancialGoal(financialGoalDto),
                        financialGoalDto.getUserDocumentNumber()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Meta financiera creada exitosamente", response));
    }

    @PutMapping("/{financialGoalId}")
    @Operation(summary = "Actualizar meta financiera", description = "Actualiza una meta financiera existente por su identificador.")
    public ResponseEntity<ApiResponse<FinancialGoalDto>> updateFinancialGoal(
            @Parameter(description = "Identificador único de la meta financiera", example = "1")
            @PathVariable Long financialGoalId,
            @Valid @RequestBody FinancialGoalDto financialGoalDto) {

        FinancialGoalDto response = mapper.toFinancialGoalDto(
                financialGoalService.updateFinancialGoal(financialGoalId, mapper.toFinancialGoal(financialGoalDto))
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Meta financiera actualizada exitosamente", response));
    }

    @DeleteMapping("/{financialGoalId}")
    @Operation(summary = "Eliminar meta financiera", description = "Elimina una meta financiera existente por su identificador.")
    public ResponseEntity<ApiResponse<Void>> deleteFinancialGoal(
            @Parameter(description = "Identificador único de la meta financiera", example = "1")
            @PathVariable Long financialGoalId) {

        financialGoalService.deleteFinancialGoal(financialGoalId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Meta financiera eliminada exitosamente", null));
    }

    @GetMapping("/{financialGoalId}")
    @Operation(summary = "Consultar meta financiera por id", description = "Consulta una meta financiera por su identificador.")
    public ResponseEntity<ApiResponse<FinancialGoalDto>> getFinancialGoalById(
            @Parameter(description = "Identificador único de la meta financiera", example = "1")
            @PathVariable Long financialGoalId) {

        FinancialGoalDto response = mapper.toFinancialGoalDto(financialGoalService.getFinancialGoalById(financialGoalId));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Meta financiera obtenida exitosamente", response));
    }

    @GetMapping("/users/{userDocumentNumber}")
    @Operation(summary = "Consultar metas financieras por usuario", description = "Consulta todas las metas financieras registradas para un usuario.")
    public ResponseEntity<ApiResponse<List<FinancialGoalDto>>> getFinancialGoalsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<FinancialGoalDto> response = mapper.toFinancialGoalsDto(financialGoalService.getFinancialGoalsByUser(userDocumentNumber));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Metas financieras obtenidas exitosamente", response));
    }

    @GetMapping("/users/{userDocumentNumber}/status/{status}")
    @Operation(summary = "Consultar metas financieras por usuario y estado", description = "Consulta las metas financieras de un usuario filtrando por estado: ACTIVE, COMPLETED o CANCELLED.")
    public ResponseEntity<ApiResponse<List<FinancialGoalDto>>> getFinancialGoalsByUserAndStatus(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber,
            @Parameter(description = "Estado de la meta financiera", example = "ACTIVE")
            @PathVariable FinancialGoalStatus status) {

        List<FinancialGoalDto> response = mapper.toFinancialGoalsDto(
                financialGoalService.getFinancialGoalsByUserAndStatus(userDocumentNumber, status)
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Metas financieras filtradas obtenidas exitosamente", response));
    }
}
