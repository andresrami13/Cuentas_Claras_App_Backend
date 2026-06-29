package com.cuentasclaras.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.cuentasclaras.model.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para registrar, consultar y administrar las cuentas de dinero de un usuario (bancos, billeteras digitales, efectivo)")
public class AccountDto {

    @Schema(description = "Identificador único de la cuenta", example = "1")
    private Long accountId;

    @Schema(description = "Número de documento del usuario dueño de la cuenta", example = "1019109757")
    private String userDocumentNumber;

    @Size(max = 100, message = "El nombre de la cuenta no puede superar 100 caracteres")
    @Schema(description = "Nombre de la cuenta", example = "Mi Nequi")
    private String name;

    @Schema(description = "Tipo de cuenta: BANK, WALLET, CASH u OTHER", example = "WALLET")
    private AccountType type;

    @Size(max = 50, message = "El proveedor no puede superar 50 caracteres")
    @Schema(description = "Identificador del proveedor del catálogo, por ejemplo nequi, bancolombia, daviplata. Nulo para cuentas personalizadas.", example = "nequi")
    private String provider;

    @Schema(description = "Saldo inicial de la cuenta", example = "150000.00")
    private BigDecimal initialBalance;

    @Size(max = 20, message = "El color no puede superar 20 caracteres")
    @Schema(description = "Color de la tarjeta de la cuenta en formato hexadecimal", example = "#5B2C83")
    private String color;

    @Size(max = 50, message = "El icono no puede superar 50 caracteres")
    @Schema(description = "Icono de la cuenta", example = "wallet")
    private String icon;

    @Schema(description = "Indica si la cuenta está archivada (oculta)", example = "false")
    private Boolean archived;
}
