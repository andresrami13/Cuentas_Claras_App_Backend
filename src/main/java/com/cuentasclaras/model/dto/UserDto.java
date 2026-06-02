package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cuentasclaras.model.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de usuario para creación, consulta y actualización")
public class UserDto {

    @Schema(description = "Número de documento del usuario", example = "1019109757")
    private String documentNumber;

    @Schema(description = "Tipo de documento del usuario", example = "CC")
    private DocumentType documentType;

    @Schema(description = "Nombres del usuario", example = "Diego Alexander")
    private String name;

    @Schema(description = "Apellidos del usuario", example = "Muñoz Reyes")
    private String lastName;

    @Schema(description = "Correo electrónico del usuario", example = "diego@email.com")
    private String email;

    @Schema(description = "Número celular del usuario", example = "3103673285")
    private String celNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(
            description = "Fecha de nacimiento en formato yyyy-MM-dd",
            example = "1997-08-15",
            type = "string",
            format = "date"
    )
    private Date birthDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "Contraseña del usuario. Solo se recibe en la petición y no se devuelve en la respuesta",
            example = "MiClaveSegura123*",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;

    @Schema(description = "Indica si el usuario se encuentra bloqueado", example = "false")
    private Boolean locked;

    @Schema(description = "Rol asignado al usuario")
    private RoleDto role;

    @Schema(description = "Licencia profesional del doctor. Requerida cuando roleCode = DOC", example = "MED-123456")
    private String license;

    @Schema(description = "Codigo de especialidad. Requerido cuando roleCode = DOC", example = "CAR")
    private String specialityCode;

    @Schema(description = "Tipo de sangre del paciente. Usado cuando roleCode = PAC", example = "O+")
    private String bloodType;
}
