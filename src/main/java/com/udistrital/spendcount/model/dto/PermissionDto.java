package com.udistrital.spendcount.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de permiso")
public class PermissionDto {

    @Schema(
            description = "Código único del permiso",
            example = "USER_CREATE"
    )
    private String permissionCode;

    @Schema(
            description = "Nombre del permiso",
            example = "Crear usuario"
    )
    private String permissionName;

    @Schema(
            description = "Descripción del permiso",
            example = "Permite crear nuevos usuarios en el sistema"
    )
    private String description;
}