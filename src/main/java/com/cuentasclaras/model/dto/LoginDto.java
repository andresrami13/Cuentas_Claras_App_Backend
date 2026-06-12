package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para el inicio de sesión del usuario")
public class LoginDto {

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 12, message = "El número de documento no puede superar 12 caracteres")
    @Schema(
            description = "Número de documento del usuario",
            example = "1019109757"
    )
    private String documentNumber;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "Contraseña del usuario. Solo se recibe en la petición",
            example = "MiClave123*",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;
}
