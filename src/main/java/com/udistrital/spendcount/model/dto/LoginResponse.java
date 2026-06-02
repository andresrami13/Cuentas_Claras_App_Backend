package com.udistrital.spendcount.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta del proceso de autenticación")
public class LoginResponse {

    @Schema(
            description = "Indica si las credenciales suministradas coinciden correctamente",
            example = "true"
    )
    private Boolean match;

    @Schema(
            description = "Detalle del resultado del inicio de sesión",
            example = "Autenticación exitosa"
    )
    private String detail;
}