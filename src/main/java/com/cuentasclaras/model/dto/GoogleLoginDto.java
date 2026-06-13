package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para iniciar sesión con Google. Contiene el ID token (JWT) emitido por Google.")
public class GoogleLoginDto {

    @NotBlank(message = "El token de Google es obligatorio")
    @JsonAlias("idToken")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "ID token (JWT firmado por Google) obtenido en el frontend tras autenticarse con Google. " +
                    "También se acepta con el nombre 'idToken'.",
            example = "eyJhbGciOiJSUzI1NiIs...",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String googleToken;
}
