package com.cuentasclaras.model.enums;

/**
 * Identifica cómo se autentica la cuenta:
 * - LOCAL: registro tradicional con contraseña.
 * - GOOGLE: la cuenta entra validando un ID token de Google y no tiene contraseña.
 */
public enum AuthProvider {
    LOCAL,
    GOOGLE
}
