package com.cuentasclaras.security;

/**
 * Datos extraídos de un ID token de Google ya verificado.
 * Se toman siempre del token firmado, nunca de lo que envía el cliente.
 */
public record GoogleUserInfo(
        String email,
        boolean emailVerified,
        String givenName,
        String familyName,
        String sub
) {
}
