package com.udistrital.spendcount.controller;

import com.udistrital.spendcount.model.dto.ApiResponse;
import com.udistrital.spendcount.model.dto.DebtDto;
import com.udistrital.spendcount.model.enums.DebtStatus;
import com.udistrital.spendcount.service.DebtService;
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
 * REST controller that exposes CRUD endpoints for managing debts.
 *
 * <p>Base path: {@code /debts}</p>
 * <p>Cross-origin requests are allowed from {@code http://localhost:5173}.</p>
 *
 */
@RestController
@RequestMapping("/debts")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
@Tag(name = "Debts", description = "Servicios para registrar, consultar y administrar deudas de usuarios")
public class DebtController {

    /** Service layer that handles the business logic for debts. */
    private final DebtService debtService;

    /** Utility component for mapping between entities and DTOs. */
    private final Mapper mapper;

    /**
     * Creates a new debt for a user.
     *
     * @param debtDto the data transfer object containing the debt details
     * @return a ResponseEntity with HTTP 201 (Created) and the created DebtDto wrapped in an ApiResponse.
     */
    @PostMapping
    @Operation(
            summary = "Crear deuda",
            description = "Registra una deuda asociada a un usuario."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Deuda creada exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<DebtDto>> createDebt(@RequestBody DebtDto debtDto) {
        DebtDto response = mapper.toDebtDto(
                debtService.createDebt(
                        mapper.toDebt(debtDto),
                        debtDto.getUserDocumentNumber()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Deuda creada exitosamente",
                        response
                ));
    }

    /**
     * Updates an existing debt.
     *
     * @param debtId the unique identifier of the debt to update
     * @param debtDto the data transfer object containing the updated debt details
     * @return a ResponseEntity with HTTP 200 (OK) and the updated DebtDto wrapped in an ApiResponse.
     */
    @PutMapping("/{debtId}")
    @Operation(
            summary = "Actualizar deuda",
            description = "Actualiza una deuda existente por su identificador."
    )
    public ResponseEntity<ApiResponse<DebtDto>> updateDebt(
            @Parameter(description = "Identificador único de la deuda", example = "1")
            @PathVariable Long debtId,
            @RequestBody DebtDto debtDto) {

        DebtDto response = mapper.toDebtDto(
                debtService.updateDebt(
                        debtId,
                        mapper.toDebt(debtDto)
                )
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Deuda actualizada exitosamente",
                        response
                ));
    }

    /**
     * Deletes a debt by id.
     *
     * @param debtId the unique identifier of the debt to delete
     * @return a ResponseEntity with HTTP 200 (OK) and a success message wrapped in an ApiResponse.
     */
    @DeleteMapping("/{debtId}")
    @Operation(
            summary = "Eliminar deuda",
            description = "Elimina una deuda existente por su identificador."
    )
    public ResponseEntity<ApiResponse<Void>> deleteDebt(
            @Parameter(description = "Identificador único de la deuda", example = "1")
            @PathVariable Long debtId) {

        debtService.deleteDebt(debtId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Deuda eliminada exitosamente",
                        null
                ));
    }

    /**
     * Retrieves a debt by id.
     *
     * @param debtId the unique identifier of the debt to retrieve
     * @return a ResponseEntity with HTTP 200 (OK) and the DebtDto wrapped in an ApiResponse.
     */
    @GetMapping("/{debtId}")
    @Operation(
            summary = "Consultar deuda por id",
            description = "Consulta una deuda por su identificador."
    )
    public ResponseEntity<ApiResponse<DebtDto>> getDebtById(
            @Parameter(description = "Identificador único de la deuda", example = "1")
            @PathVariable Long debtId) {

        DebtDto response = mapper.toDebtDto(debtService.getDebtById(debtId));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Deuda obtenida exitosamente",
                        response
                ));
    }

    /**
     * Retrieves all debts by user document number.
     *
     * @param userDocumentNumber document number of the user owner of the debts
     * @return a ResponseEntity with HTTP 200 (OK) and the list of DebtDto wrapped in an ApiResponse.
     */
    @GetMapping("/users/{userDocumentNumber}")
    @Operation(
            summary = "Consultar deudas por usuario",
            description = "Consulta todas las deudas registradas para un usuario."
    )
    public ResponseEntity<ApiResponse<List<DebtDto>>> getDebtsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<DebtDto> response = mapper.toDebtsDto(debtService.getDebtsByUser(userDocumentNumber));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Deudas obtenidas exitosamente",
                        response
                ));
    }

    /**
     * Retrieves debts by user and status.
     *
     * @param userDocumentNumber document number of the user owner of the debts
     * @param status status of the debts
     * @return a ResponseEntity with HTTP 200 (OK) and the list of DebtDto wrapped in an ApiResponse.
     */
    @GetMapping("/users/{userDocumentNumber}/status/{status}")
    @Operation(
            summary = "Consultar deudas por usuario y estado",
            description = "Consulta las deudas de un usuario filtrando por estado: ACTIVE, PAID o CANCELLED."
    )
    public ResponseEntity<ApiResponse<List<DebtDto>>> getDebtsByUserAndStatus(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber,
            @Parameter(description = "Estado de la deuda", example = "ACTIVE")
            @PathVariable DebtStatus status) {

        List<DebtDto> response = mapper.toDebtsDto(
                debtService.getDebtsByUserAndStatus(userDocumentNumber, status)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Deudas filtradas obtenidas exitosamente",
                        response
                ));
    }
}