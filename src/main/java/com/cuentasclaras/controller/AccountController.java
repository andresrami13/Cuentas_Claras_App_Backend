package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.AccountDto;
import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.service.AccountService;
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
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Servicios para administrar las cuentas de dinero de un usuario (bancos, billeteras digitales, efectivo)")
public class AccountController {

    private final AccountService accountService;
    private final Mapper mapper;

    @PostMapping
    @Operation(summary = "Crear cuenta", description = "Registra una cuenta de dinero asociada a un usuario.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<AccountDto>> createAccount(@Valid @RequestBody AccountDto accountDto) {
        AccountDto response = mapper.toAccountDto(
                accountService.createAccount(
                        mapper.toAccount(accountDto),
                        accountDto.getUserDocumentNumber()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Cuenta creada exitosamente", response));
    }

    @PutMapping("/{accountId}")
    @Operation(summary = "Actualizar cuenta", description = "Actualiza una cuenta existente por su identificador.")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccount(
            @Parameter(description = "Identificador único de la cuenta", example = "1")
            @PathVariable Long accountId,
            @Valid @RequestBody AccountDto accountDto) {

        AccountDto response = mapper.toAccountDto(accountService.updateAccount(accountId, mapper.toAccount(accountDto)));

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Cuenta actualizada exitosamente", response));
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Eliminar cuenta", description = "Elimina una cuenta existente por su identificador.")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @Parameter(description = "Identificador único de la cuenta", example = "1")
            @PathVariable Long accountId) {

        accountService.deleteAccount(accountId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Cuenta eliminada exitosamente", null));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Consultar cuenta por id", description = "Consulta una cuenta por su identificador.")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountById(
            @Parameter(description = "Identificador único de la cuenta", example = "1")
            @PathVariable Long accountId) {

        AccountDto response = mapper.toAccountDto(accountService.getAccountById(accountId));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Cuenta obtenida exitosamente", response));
    }

    @GetMapping("/users/{userDocumentNumber}")
    @Operation(summary = "Consultar cuentas por usuario", description = "Consulta todas las cuentas registradas para un usuario.")
    public ResponseEntity<ApiResponse<List<AccountDto>>> getAccountsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<AccountDto> response = mapper.toAccountsDto(accountService.getAccountsByUser(userDocumentNumber));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "Cuentas obtenidas exitosamente", response));
    }
}
