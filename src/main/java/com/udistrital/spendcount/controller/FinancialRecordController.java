package com.udistrital.spendcount.controller;

import com.udistrital.spendcount.model.dto.ApiResponse;
import com.udistrital.spendcount.model.dto.FinancialRecordDto;
import com.udistrital.spendcount.model.enums.FinancialRecordType;
import com.udistrital.spendcount.service.FinancialRecordService;
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
 * REST controller that exposes CRUD endpoints for managing incomes and expenses.
 *
 * <p>Base path: {@code /financial-records}</p>
 * <p>Cross-origin requests are allowed from {@code http://localhost:5173}.</p>
 *
 */
@RestController
@RequestMapping("/financial-records")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
@Tag(name = "Financial Records", description = "Servicios para registrar ingresos, egresos y movimientos recurrentes como salarios")
public class FinancialRecordController {

    /** Service layer that handles the business logic for financial records. */
    private final FinancialRecordService financialRecordService;

    /** Utility component for mapping between entities and DTOs. */
    private final Mapper mapper;

    /**
     * Creates a new income record for a user.
     *
     * @param financialRecordDto the data transfer object containing the income details
     * @return a ResponseEntity with HTTP 201 (Created) and the created FinancialRecordDto wrapped in an ApiResponse.
     */
    @PostMapping("/incomes")
    @Operation(
            summary = "Crear ingreso",
            description = "Registra un ingreso asociado a un usuario. Un salario se registra por este servicio usando recurring=true y periodicity=MONTHLY o BIWEEKLY."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Ingreso creado exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<FinancialRecordDto>> createIncome(@RequestBody FinancialRecordDto financialRecordDto) {
        financialRecordDto.setRecordType(FinancialRecordType.INCOME);

        FinancialRecordDto response = mapper.toFinancialRecordDto(
                financialRecordService.createFinancialRecord(
                        mapper.toFinancialRecord(financialRecordDto),
                        financialRecordDto.getUserDocumentNumber()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Ingreso creado exitosamente",
                        response
                ));
    }

    /**
     * Creates a new expense record for a user.
     *
     * @param financialRecordDto the data transfer object containing the expense details
     * @return a ResponseEntity with HTTP 201 (Created) and the created FinancialRecordDto wrapped in an ApiResponse.
     */
    @PostMapping("/expenses")
    @Operation(
            summary = "Crear egreso",
            description = "Registra un egreso asociado a un usuario. Puede ser recurrente, por ejemplo arriendo, servicios o suscripciones."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Egreso creado exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<FinancialRecordDto>> createExpense(@RequestBody FinancialRecordDto financialRecordDto) {
        financialRecordDto.setRecordType(FinancialRecordType.EXPENSE);

        FinancialRecordDto response = mapper.toFinancialRecordDto(
                financialRecordService.createFinancialRecord(
                        mapper.toFinancialRecord(financialRecordDto),
                        financialRecordDto.getUserDocumentNumber()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Egreso creado exitosamente",
                        response
                ));
    }

    /**
     * Updates an existing financial record.
     *
     * @param financialRecordId  the unique identifier of the financial record to update
     * @param financialRecordDto the data transfer object containing the updated financial record details
     * @return a ResponseEntity with HTTP 200 (OK) and the updated FinancialRecordDto wrapped in an ApiResponse.
     */
    @PutMapping("/{financialRecordId}")
    @Operation(
            summary = "Actualizar movimiento financiero",
            description = "Actualiza un ingreso o egreso existente."
    )
    public ResponseEntity<ApiResponse<FinancialRecordDto>> updateFinancialRecord(
            @Parameter(description = "Identificador único del movimiento financiero", example = "1")
            @PathVariable Long financialRecordId,
            @RequestBody FinancialRecordDto financialRecordDto) {

        FinancialRecordDto response = mapper.toFinancialRecordDto(
                financialRecordService.updateFinancialRecord(
                        financialRecordId,
                        mapper.toFinancialRecord(financialRecordDto)
                )
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Movimiento financiero actualizado exitosamente",
                        response
                ));
    }

    /**
     * Deletes a financial record by id.
     *
     * @param financialRecordId the unique identifier of the financial record to delete
     * @return a ResponseEntity with HTTP 200 (OK) and a success message wrapped in an ApiResponse.
     */
    @DeleteMapping("/{financialRecordId}")
    @Operation(
            summary = "Eliminar movimiento financiero",
            description = "Elimina un ingreso o egreso existente por su identificador."
    )
    public ResponseEntity<ApiResponse<Void>> deleteFinancialRecord(
            @Parameter(description = "Identificador único del movimiento financiero", example = "1")
            @PathVariable Long financialRecordId) {

        financialRecordService.deleteFinancialRecord(financialRecordId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Movimiento financiero eliminado exitosamente",
                        null
                ));
    }

    /**
     * Retrieves a financial record by id.
     *
     * @param financialRecordId the unique identifier of the financial record to retrieve
     * @return a ResponseEntity with HTTP 200 (OK) and the FinancialRecordDto wrapped in an ApiResponse.
     */
    @GetMapping("/{financialRecordId}")
    @Operation(
            summary = "Consultar movimiento financiero por id",
            description = "Consulta un movimiento financiero por su identificador."
    )
    public ResponseEntity<ApiResponse<FinancialRecordDto>> getFinancialRecordById(
            @Parameter(description = "Identificador único del movimiento financiero", example = "1")
            @PathVariable Long financialRecordId) {

        FinancialRecordDto response = mapper.toFinancialRecordDto(
                financialRecordService.getFinancialRecordById(financialRecordId)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Movimiento financiero obtenido exitosamente",
                        response
                ));
    }

    /**
     * Retrieves all financial records by user document number.
     *
     * @param userDocumentNumber document number of the user owner of the records
     * @return a ResponseEntity with HTTP 200 (OK) and the list of FinancialRecordDto wrapped in an ApiResponse.
     */
    @GetMapping("/users/{userDocumentNumber}")
    @Operation(
            summary = "Consultar movimientos financieros por usuario",
            description = "Consulta todos los ingresos y egresos registrados para un usuario."
    )
    public ResponseEntity<ApiResponse<List<FinancialRecordDto>>> getFinancialRecordsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<FinancialRecordDto> response = mapper.toFinancialRecordsDto(
                financialRecordService.getFinancialRecordsByUser(userDocumentNumber)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Movimientos financieros obtenidos exitosamente",
                        response
                ));
    }

    /**
     * Retrieves financial records by user and type.
     *
     * @param userDocumentNumber document number of the user owner of the records
     * @param recordType type of financial record
     * @return a ResponseEntity with HTTP 200 (OK) and the list of FinancialRecordDto wrapped in an ApiResponse.
     */
    @GetMapping("/users/{userDocumentNumber}/type/{recordType}")
    @Operation(
            summary = "Consultar movimientos financieros por usuario y tipo",
            description = "Consulta los movimientos financieros de un usuario filtrando por tipo: INCOME o EXPENSE."
    )
    public ResponseEntity<ApiResponse<List<FinancialRecordDto>>> getFinancialRecordsByUserAndType(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber,
            @Parameter(description = "Tipo de movimiento financiero", example = "INCOME")
            @PathVariable FinancialRecordType recordType) {

        List<FinancialRecordDto> response = mapper.toFinancialRecordsDto(
                financialRecordService.getFinancialRecordsByUserAndType(userDocumentNumber, recordType)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Movimientos financieros filtrados obtenidos exitosamente",
                        response
                ));
    }

    /**
     * Retrieves recurrent financial records by user document number.
     *
     * @param userDocumentNumber document number of the user owner of the records
     * @return a ResponseEntity with HTTP 200 (OK) and the list of recurrent FinancialRecordDto wrapped in an ApiResponse.
     */
    @GetMapping("/users/{userDocumentNumber}/recurring")
    @Operation(
            summary = "Consultar movimientos financieros recurrentes por usuario",
            description = "Consulta los movimientos recurrentes de un usuario, por ejemplo salario, arriendo, servicios, suscripciones o pagos periódicos."
    )
    public ResponseEntity<ApiResponse<List<FinancialRecordDto>>> getRecurringFinancialRecordsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<FinancialRecordDto> response = mapper.toFinancialRecordsDto(
                financialRecordService.getRecurringFinancialRecordsByUser(userDocumentNumber)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Movimientos financieros recurrentes obtenidos exitosamente",
                        response
                ));
    }
}