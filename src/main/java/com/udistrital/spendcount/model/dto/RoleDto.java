package com.udistrital.spendcount.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de rol")
public class RoleDto {

    @Schema(
            description = "Código único del rol",
            example = "ADMIN"
    )
    String roleCode;

    @Schema(
            description = "Nombre del rol",
            example = "Administrador"
    )
    String roleName;

    @Schema(
            description = "Descripción del rol",
            example = "Rol con permisos de administración sobre el sistema"
    )
    String description;

    @Schema(
            description = "Lista de permisos asociados al rol"
    )
    List<RolePermissionDto> rolePermissionDtoList;
}