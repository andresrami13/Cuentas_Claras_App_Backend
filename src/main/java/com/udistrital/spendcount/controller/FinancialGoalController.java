package com.udistrital.spendcount.controller;

import com.udistrital.spendcount.model.dto.ApiResponse;
import com.udistrital.spendcount.model.dto.FinancialGoalDto;
import com.udistrital.spendcount.model.enums.FinancialGoalStatus;
import com.udistrital.spendcount.service.FinancialGoalService;
import com.udistrital.spendcount.utils.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes CRUD endpoints for managing financial goals.
 *
 * <p>Base path: {@code /financial-goals}</p>
 * <p>Cross-origin requests are allowed from {@code http://localhost:5173}.</p>
 *
 */
@RestController
@RequestMapping("/financial-goals")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
@Tag(name = "Financial Goals", description = "Servicios para registrar, consultar y administrar metas financieras de usuarios")
public class FinancialGoalController {

    /** Service layer that handles the business logic for financial goals. */
    private final FinancialGoalService financialGoalService;

    /** Utility component for mapping between entities and DTOs. */
    private final Mapper mapper;

    /**
     * Creates a new financial goal for a user.
     *
     * @param financialGoalDto the data transfer object containing the financial goal details
     * @return a ResponseEntity with HTTP 201 (Created) and the created FinancialGoalDto wrapped in an ApiResponse.
     */
    @PostMapping
    @Operation(
            summary = "Crear meta financiera",
            description = "Registra una meta financiera asociada a un usuario."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Meta financiera creada exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<FinancialGoalDto>> createFinancialGoal(@RequestBody FinancialGoalDto financialGoalDto) {
        FinancialGoalDto response = mapper.toFinancialGoalDto(
                financialGoalService.createFinancialGoal(
                        mapper.toFinancialGoal(financialGoalDto),
                        financialGoalDto.getUserDocumentNumber()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Meta financiera creada exitosamente",
                        response
                ));
    }

    /**
     * Updates an existing financial goal.
     *
     * @param financialGoalId the unique identifier of the financial goal to update
     * @param financialGoalDto the data transfer object containing the updated financial goal details
     * @return a ResponseEntity with HTTP 200 (OK) and the updated FinancialGoalDto wrapped in an ApiResponse.
     */
    @PutMapping("/{financialGoalId}")
    @Operation(
            summary = "Actualizar meta financiera",
            description = "Actualiza una meta financiera existente por su identificador."
    )
    public ResponseEntity<ApiResponse<FinancialGoalDto>> updateFinancialGoal(
            @Parameter(description = "Identificador único de la meta financiera", example = "1")
            @PathVariable Long financialGoalId,
            @RequestBody FinancialGoalDto financialGoalDto) {

        FinancialGoalDto response = mapper.toFinancialGoalDto(
                financialGoalService.updateFinancialGoal(
                        financialGoalId,
                        mapper.toFinancialGoal(financialGoalDto)
                )
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Meta financiera actualizada exitosamente",
                        response
                ));
    }

    /**
     * Deletes a financial goal by id.
     *
     * @param financialGoalId the unique identifier of the financial goal to delete
     * @return a ResponseEntity with HTTP 200 (OK) and a success message wrapped in an ApiResponse.
     */
    @DeleteMapping("/{financialGoalId}")
    @Operation(
            summary = "Eliminar meta financiera",
            description = "Elimina una meta financiera existente por su identificador."
    )
    public ResponseEntity<ApiResponse<Void>> deleteFinancialGoal(
            @Parameter(description = "Identificador único de la meta financiera", example = "1")
            @PathVariable Long financialGoalId) {

        financialGoalService.deleteFinancialGoal(financialGoalId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Meta financiera eliminada exitosamente",
                        null
                ));
    }

    /**
     * Retrieves a financial goal by id.
     *
     * @param financialGoalId the unique identifier of the financial goal to retrieve
     * @return a ResponseEntity with HTTP 200 (OK) and the FinancialGoalDto wrapped in an ApiResponse.
     */
    @GetMapping("/{financialGoalId}")
    @Operation(
            summary = "Consultar meta financiera por id",
            description = "Consulta una meta financiera por su identificador."
    )
    public ResponseEntity<ApiResponse<FinancialGoalDto>> getFinancialGoalById(
            @Parameter(description = "Identificador único de la meta financiera", example = "1")
            @PathVariable Long financialGoalId) {

        FinancialGoalDto response = mapper.toFinancialGoalDto(
                financialGoalService.getFinancialGoalById(financialGoalId)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Meta financiera obtenida exitosamente",
                        response
                ));
    }

    /**
     * Retrieves all financial goals by user document number.
     *
     * @param userDocumentNumber document number of the user owner of the financial goals
     * @return a ResponseEntity with HTTP 200 (OK) and the list of FinancialGoalDto wrapped in an ApiResponse.
     */
    @GetMapping("/users/{userDocumentNumber}")
    @Operation(
            summary = "Consultar metas financieras por usuario",
            description = "Consulta todas las metas financieras registradas para un usuario."
    )
    public ResponseEntity<ApiResponse<List<FinancialGoalDto>>> getFinancialGoalsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<FinancialGoalDto> response = mapper.toFinancialGoalsDto(
                financialGoalService.getFinancialGoalsByUser(userDocumentNumber)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Metas financieras obtenidas exitosamente",
                        response
                ));
    }

    /**
     * Retrieves financial goals by user and status.
     *
     * @param userDocumentNumber document number of the user owner of the financial goals
     * @param status financial goal status
     * @return a ResponseEntity with HTTP 200 (OK) and the list of FinancialGoalDto wrapped in an ApiResponse.
     */
    @GetMapping("/users/{userDocumentNumber}/status/{status}")
    @Operation(
            summary = "Consultar metas financieras por usuario y estado",
            description = "Consulta las metas financieras de un usuario filtrando por estado: ACTIVE, COMPLETED o CANCELLED."
    )
    public ResponseEntity<ApiResponse<List<FinancialGoalDto>>> getFinancialGoalsByUserAndStatus(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber,
            @Parameter(description = "Estado de la meta financiera", example = "ACTIVE")
            @PathVariable FinancialGoalStatus status) {

        List<FinancialGoalDto> response = mapper.toFinancialGoalsDto(
                financialGoalService.getFinancialGoalsByUserAndStatus(userDocumentNumber, status)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Metas financieras filtradas obtenidas exitosamente",
                        response
                ));
    }
}