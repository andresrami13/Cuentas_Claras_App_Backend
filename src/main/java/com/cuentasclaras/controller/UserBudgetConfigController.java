package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.model.dto.UserBudgetConfigDto;
import com.cuentasclaras.service.UserBudgetConfigService;
import com.cuentasclaras.utils.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-budget-config")
@RequiredArgsConstructor
@Tag(name = "User Budget Config", description = "Servicios para gestionar la configuración de presupuesto del usuario")
public class UserBudgetConfigController {

    private final UserBudgetConfigService userBudgetConfigService;
    private final Mapper mapper;

    @PostMapping
    @Operation(summary = "Crear o actualizar configuración de presupuesto", description = "Crea la configuración de presupuesto del usuario si no existe, o la actualiza si ya existe.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Configuración guardada exitosamente")
    public ResponseEntity<ApiResponse<UserBudgetConfigDto>> saveConfig(@RequestBody UserBudgetConfigDto userBudgetConfigDto) {
        UserBudgetConfigDto response = mapper.toUserBudgetConfigDto(
                userBudgetConfigService.saveConfig(
                        mapper.toUserBudgetConfig(userBudgetConfigDto),
                        userBudgetConfigDto.getUserDocumentNumber()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Configuración de presupuesto guardada exitosamente", response));
    }

    @GetMapping("/{documentNumber}")
    @Operation(summary = "Obtener configuración de presupuesto del usuario", description = "Retorna la configuración de presupuesto registrada para el usuario.")
    public ResponseEntity<ApiResponse<UserBudgetConfigDto>> getConfig(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String documentNumber) {

        UserBudgetConfigDto response = mapper.toUserBudgetConfigDto(userBudgetConfigService.getConfig(documentNumber));
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Configuración de presupuesto obtenida exitosamente", response));
    }

    @DeleteMapping("/{documentNumber}/fixed-categories/{categoryId}")
    @Operation(summary = "Eliminar categoría fija", description = "Elimina una categoría fija de la configuración de presupuesto del usuario.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría fija eliminada exitosamente")
    public ResponseEntity<ApiResponse<Void>> deleteFixedCategory(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String documentNumber,
            @Parameter(description = "Identificador de la categoría fija", example = "1")
            @PathVariable Long categoryId) {

        userBudgetConfigService.deleteFixedCategory(documentNumber, categoryId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Categoría fija eliminada exitosamente", null));
    }
}
