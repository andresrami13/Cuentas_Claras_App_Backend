package com.cuentasclaras.security;

import com.cuentasclaras.exception.BusinessException;
import com.cuentasclaras.exception.SystemException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Verifica un ID token de Google contra las llaves públicas de Google.
 * Valida firma, vigencia, emisor (accounts.google.com) y que el "aud" sea
 * nuestro Client ID. Devuelve los datos del usuario tomados del token firmado.
 */
@Component
@Slf4j
public class GoogleTokenVerifier {

    @Value("${app.security.google.client-id:}")
    private String clientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    void init() {
        if (clientId == null || clientId.isBlank()) {
            log.warn("GOOGLE_CLIENT_ID no está configurado. El login y registro con Google " +
                    "rechazarán todos los tokens hasta que se configure.");
        }
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    /**
     * Verifica el idToken y devuelve los datos del usuario. Lanza BusinessException
     * si el token es inválido, expiró o su audiencia no corresponde a nuestro Client ID.
     */
    public GoogleUserInfo verify(String idTokenString) {
        if (clientId == null || clientId.isBlank())
            throw new SystemException("El inicio de sesión con Google no está configurado en el servidor");

        if (idTokenString == null || idTokenString.isBlank())
            throw new BusinessException("El idToken de Google es obligatorio");

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            // No se filtra el detalle del token al log para no exponer datos sensibles
            log.warn("No fue posible verificar el ID token de Google: {}", e.getMessage());
            throw new BusinessException("No fue posible validar el token de Google");
        }

        if (idToken == null)
            throw new BusinessException("El token de Google es inválido o no corresponde a esta aplicación");

        GoogleIdToken.Payload payload = idToken.getPayload();
        return new GoogleUserInfo(
                payload.getEmail(),
                Boolean.TRUE.equals(payload.getEmailVerified()),
                (String) payload.get("given_name"),
                (String) payload.get("family_name"),
                payload.getSubject()
        );
    }
}
