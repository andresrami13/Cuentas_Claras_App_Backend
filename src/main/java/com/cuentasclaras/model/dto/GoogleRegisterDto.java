package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cuentasclaras.model.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

/**
 * Registro de una cuenta nueva con Google. El nombre, apellido y correo NO se
 * reciben aquí: se extraen del idToken verificado para no confiar en el cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para completar el registro de un usuario nuevo que entra con Google")
public class GoogleRegisterDto {

    @NotBlank(message = "El token de Google es obligatorio")
    @JsonAlias("idToken")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "ID token (JWT firmado por Google) del usuario que se está registrando. " +
                    "También se acepta con el nombre 'idToken'.",
            example = "eyJhbGciOiJSUzI1NiIs...",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String googleToken;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 12, message = "El número de documento no puede superar 12 caracteres")
    @Schema(description = "Número de documento del usuario", example = "1234567890")
    private String documentNumber;

    @NotNull(message = "El tipo de documento es obligatorio")
    @Schema(description = "Tipo de documento del usuario", example = "CC")
    private DocumentType documentType;

    @NotBlank(message = "El número de celular es obligatorio")
    @Size(max = 12, message = "El número de celular no puede superar 12 caracteres")
    @Schema(description = "Número celular del usuario", example = "3103673285")
    private String celNumber;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(
            description = "Fecha de nacimiento en formato yyyy-MM-dd",
            example = "1995-04-20",
            type = "string",
            format = "date"
    )
    private Date birthDate;
}
