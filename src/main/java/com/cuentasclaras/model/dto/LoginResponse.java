package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

    // Se fuerza el nombre JSON: con un Boolean "isX", Jackson tiende a publicarlo como "newUser".
    @JsonProperty("isNewUser")
    @Schema(
            description = "Solo en login con Google: true cuando el correo aún no está registrado y se " +
                    "requiere completar el perfil; false cuando la cuenta ya existe y se devuelve token",
            example = "false"
    )
    private Boolean isNewUser;

    @Schema(
            description = "Detalle del resultado del inicio de sesión",
            example = "Autenticación exitosa"
    )
    private String detail;

    @Schema(
            description = "Token JWT a enviar en el header Authorization: Bearer <token>. Solo presente cuando el login es exitoso"
    )
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Segundos de vigencia del token", example = "3600")
    private Long expiresIn;

    @Schema(description = "Número de documento del usuario autenticado", example = "1234567890")
    private String documentNumber;

    @Schema(description = "Nombre del usuario autenticado", example = "Diego")
    private String name;

    @Schema(description = "Código del rol del usuario autenticado", example = "USR")
    private String roleCode;

    @Schema(
            description = "Correo verificado por Google. Solo presente en login con Google cuando la cuenta " +
                    "aún no existe (registro incompleto)",
            example = "diego@gmail.com"
    )
    private String email;

    @Schema(
            description = "Apellido extraído del token de Google. Solo presente en login con Google cuando " +
                    "la cuenta aún no existe (registro incompleto)",
            example = "Ramírez"
    )
    private String lastName;
}
