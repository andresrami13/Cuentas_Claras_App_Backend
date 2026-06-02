package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO que representa la relación entre un rol y un permiso")
public class RolePermissionDto {

    @Schema(
            description = "Código del rol asociado",
            example = "ADMIN"
    )
    private String roleCode;

    @Schema(
            description = "Código del permiso asociado",
            example = "USER_CREATE"
    )
    private String permissionCode;
}
