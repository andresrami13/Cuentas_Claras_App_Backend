package com.cuentasclaras.controller;

import com.cuentasclaras.model.dto.AiCoachRequestDto;
import com.cuentasclaras.model.dto.ApiResponse;
import com.cuentasclaras.service.AiCoachService;
import com.cuentasclaras.utils.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-coach")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173","https://yellow-hill-0c3a76b0f.7.azurestaticapps.net"})
@Tag(name = "AI Coach", description = "Servicios para generar y consultar recomendaciones financieras con IA")
public class AiCoachController {

    private final AiCoachService aiCoachService;
    private final Mapper mapper;

    @PostMapping("/advice")
    @Operation(
            summary = "Solicitar consejo financiero IA",
            description = "Genera una recomendación financiera usando IA a partir del histórico de ingresos, egresos, deudas y una meta financiera del usuario."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Consejo financiero generado exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Error de validación de negocio")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponse<AiCoachRequestDto>> requestFinancialAdvice(
            @RequestBody AiCoachRequestDto aiCoachRequestDto) {

        AiCoachRequestDto response = mapper.toAiCoachRequestDto(
                aiCoachService.requestFinancialAdvice(
                        aiCoachRequestDto.getUserDocumentNumber(),
                        aiCoachRequestDto.getFinancialGoalId(),
                        aiCoachRequestDto.getQuestion()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "Consejo financiero generado exitosamente",
                        response
                ));
    }

    @GetMapping("/{aiCoachRequestId}")
    @Operation(
            summary = "Consultar solicitud al coach IA por id",
            description = "Consulta una solicitud realizada al coach financiero IA por su identificador."
    )
    public ResponseEntity<ApiResponse<AiCoachRequestDto>> getAiCoachRequestById(
            @Parameter(description = "Identificador único de la solicitud al coach IA", example = "1")
            @PathVariable Long aiCoachRequestId) {

        AiCoachRequestDto response = mapper.toAiCoachRequestDto(
                aiCoachService.getAiCoachRequestById(aiCoachRequestId)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Solicitud al coach IA obtenida exitosamente",
                        response
                ));
    }

    @GetMapping("/users/{userDocumentNumber}/requests")
    @Operation(
            summary = "Consultar solicitudes al coach IA por usuario",
            description = "Consulta el histórico de solicitudes realizadas al coach financiero IA por un usuario."
    )
    public ResponseEntity<ApiResponse<List<AiCoachRequestDto>>> getAiCoachRequestsByUser(
            @Parameter(description = "Número de documento del usuario", example = "1019109757")
            @PathVariable String userDocumentNumber) {

        List<AiCoachRequestDto> response = mapper.toAiCoachRequestsDto(
                aiCoachService.getAiCoachRequestsByUser(userDocumentNumber)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(
                        HttpStatus.OK.value(),
                        "Solicitudes al coach IA obtenidas exitosamente",
                        response
                ));
    }
}
