package com.cuentasclaras.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Estructura estándar de respuesta del servicio")
public class ApiResponse<T> {

    @Schema(
            description = "Código de respuesta HTTP o código interno de la operación",
            example = "200"
    )
    private int code;

    @Schema(
            description = "Mensaje descriptivo del resultado de la operación",
            example = "Operación realizada correctamente"
    )
    private String message;

    @Schema(
            description = "Información retornada por el servicio. Puede ser un objeto, una lista o null según la operación"
    )
    private T data;

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}
