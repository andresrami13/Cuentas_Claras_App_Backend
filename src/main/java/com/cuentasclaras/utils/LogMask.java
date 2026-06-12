package com.cuentasclaras.utils;

/**
 * Utilidades para enmascarar datos sensibles en los logs (p. ej. cédulas),
 * de modo que no queden escritos en claro en los registros del servidor.
 */
public final class LogMask {

    private LogMask() {
    }

    public static String doc(String documentNumber) {
        if (documentNumber == null || documentNumber.length() <= 4) {
            return "***";
        }
        return documentNumber.substring(0, 3)
                + "***"
                + documentNumber.substring(documentNumber.length() - 2);
    }
}
