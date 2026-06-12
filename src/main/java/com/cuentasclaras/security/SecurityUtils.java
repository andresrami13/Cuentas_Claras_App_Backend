package com.cuentasclaras.security;

import com.cuentasclaras.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Punto único para validar identidad y propiedad de recursos.
 * El número de documento del usuario autenticado SIEMPRE proviene del token JWT,
 * nunca de parámetros enviados por el cliente.
 */
@Component
public class SecurityUtils {

    @Value("${app.security.admin-role-code}")
    private String adminRoleCode;

    public String getCurrentDocumentNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null)
            throw new BusinessException("Usuario no autenticado", HttpStatus.UNAUTHORIZED);
        return authentication.getName();
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + adminRoleCode));
    }

    /**
     * Exige que el número de documento recibido como parámetro sea el del usuario
     * autenticado. Sin excepción para administradores: los datos financieros son
     * privados incluso frente al admin.
     */
    public void validateSelf(String documentNumber) {
        if (!getCurrentDocumentNumber().equals(documentNumber))
            throw new BusinessException("No puede acceder a información de otros usuarios", HttpStatus.FORBIDDEN);
    }

    /**
     * Exige que el recurso pertenezca al usuario autenticado. Sin excepción para
     * administradores: los datos financieros son privados incluso frente al admin.
     * Responde 404 para no confirmar la existencia de recursos ajenos.
     */
    public void validateOwnership(String ownerDocumentNumber, String notFoundMessage) {
        if (!getCurrentDocumentNumber().equals(ownerDocumentNumber))
            throw new BusinessException(notFoundMessage, HttpStatus.NOT_FOUND);
    }

    /**
     * Igual que validateOwnership pero permite el acceso al administrador.
     * Solo para datos de cuenta (perfil), nunca para datos financieros.
     */
    public void validateOwnershipOrAdmin(String ownerDocumentNumber, String notFoundMessage) {
        if (isAdmin())
            return;
        validateOwnership(ownerDocumentNumber, notFoundMessage);
    }
}
