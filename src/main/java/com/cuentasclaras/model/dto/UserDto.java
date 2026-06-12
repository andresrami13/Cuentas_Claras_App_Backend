package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.cuentasclaras.model.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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

    @Size(max = 12, message = "El número de documento no puede superar 12 caracteres")
    @Schema(description = "Número de documento del usuario", example = "1019109757")
    private String documentNumber;

    @Schema(description = "Tipo de documento del usuario", example = "CC")
    private DocumentType documentType;

    @Size(max = 250, message = "El nombre no puede superar 250 caracteres")
    @Schema(description = "Nombres del usuario", example = "Diego Alexander")
    private String name;

    @Size(max = 250, message = "El apellido no puede superar 250 caracteres")
    @Schema(description = "Apellidos del usuario", example = "Muñoz Reyes")
    private String lastName;

    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 250, message = "El correo no puede superar 250 caracteres")
    @Schema(description = "Correo electrónico del usuario", example = "diego@email.com")
    private String email;

    @Size(max = 12, message = "El número de celular no puede superar 12 caracteres")
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

    @Size(max = 72, message = "La contraseña no puede superar 72 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "Contraseña del usuario. Solo se recibe en la petición y no se devuelve en la respuesta",
            example = "MiClaveSegura123*",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "Contraseña actual del usuario. Requerida únicamente cuando se quiere cambiar la contraseña",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String currentPassword;

    @Schema(description = "Indica si el usuario se encuentra bloqueado", example = "false")
    private Boolean locked;

    @Schema(description = "Rol asignado al usuario")
    private RoleDto role;
}
