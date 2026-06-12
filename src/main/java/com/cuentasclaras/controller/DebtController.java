package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.model.dto.DebtDto;
import com.cuentasclaras.model.enums.DebtStatus;
import com.cuentasclaras.service.DebtService;
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
@RequestMapping("/debts")
@RequiredArgsConstructor
@Tag(name = "Debts", description = "Servicios para registrar, consultar y administrar deudas de usuarios")
public class DebtController {

    private final DebtService debtService;
    private final Mapper mapper;

    @PostMapping
    @Operation(summary = "Crear deuda", description = "Registra una deuda asociada a un usuario.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Deuda creada exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<DebtDto>> createDebt(@Valid @RequestBody DebtDto debtDto) {
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

    @PutMapping("/{debtId}")
    @Operation(summary = "Actualizar deuda", description = "Actualiza una deuda existente por su identificador.")
    public ResponseEntity<ApiResponse<DebtDto>> updateDebt(
            @Parameter(description = "Identificador único de la deuda", example = "1")
            @PathVariable Long debtId,
            @Valid @RequestBody DebtDto debtDto) {

        DebtDto response = mapper.toDebtDto(debtService.updateDebt(debtId, mapper.toDebt(debtDto)));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Deuda actualizada exitosamente", response));
    }

    @DeleteMapping("/{debtId}")
    @Operation(summary = "Eliminar deuda", description = "Elimina una deuda existente por su identificador.")
    public ResponseEntity<ApiResponse<Void>> deleteDebt(
            @Parameter(description = "Identificador único de la deuda", example = "1")
            @PathVariable Long debtId) {

        debtService.deleteDebt(debtId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Deuda eliminada exitosamente", null));
    }

    @GetMapping("/{debtId}")
    @Operation(summary = "Consultar deuda por id", description = "Consulta una deuda por su identificador.")
    public ResponseEntity<ApiResponse<DebtDto>> getDebtById(
            @Parameter(description = "Identificador único de la deuda", example = "1")
            @PathVariable Long debtId) {

        DebtDto response = mapper.toDebtDto(debtService.getDebtById(debtId));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Deuda obtenida exitosamente", response));
    }

    @GetMapping("/users/{userDocumentNumber}")
    @Operation(summary = "Consultar deudas por usuario", description = "Consulta todas las deudas registradas para un usuario.")
    public ResponseEntity<ApiResponse<List<DebtDto>>> getDebtsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<DebtDto> response = mapper.toDebtsDto(debtService.getDebtsByUser(userDocumentNumber));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Deudas obtenidas exitosamente", response));
    }

    @GetMapping("/users/{userDocumentNumber}/status/{status}")
    @Operation(summary = "Consultar deudas por usuario y estado", description = "Consulta las deudas de un usuario filtrando por estado: ACTIVE, PAID o CANCELLED.")
    public ResponseEntity<ApiResponse<List<DebtDto>>> getDebtsByUserAndStatus(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber,
            @Parameter(description = "Estado de la deuda", example = "ACTIVE")
            @PathVariable DebtStatus status) {

        List<DebtDto> response = mapper.toDebtsDto(debtService.getDebtsByUserAndStatus(userDocumentNumber, status));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Deudas filtradas obtenidas exitosamente", response));
    }
}
