package com.cuentasclaras.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtService {

    public static final String CLAIM_ROLE = "role";

    @Value("${app.security.jwt.secret}")
    private String configuredSecret;

    @Value("${app.security.jwt.expiration-minutes}")
    private long expirationMinutes;

    private SecretKey signingKey;

    @PostConstruct
    void init() {
        if (configuredSecret == null || configuredSecret.isBlank()) {
            log.warn("JWT_SECRET no está configurado. Se generó una clave aleatoria: "
                    + "las sesiones se invalidarán en cada reinicio de la aplicación.");
            this.signingKey = Jwts.SIG.HS256.key().build();
            return;
        }
        // Keys.hmacShaKeyFor falla si el secreto tiene menos de 256 bits (32 caracteres)
        this.signingKey = Keys.hmacShaKeyFor(configuredSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String documentNumber, String roleCode) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMinutes * 60_000);

        return Jwts.builder()
                .subject(documentNumber)
                .claim(CLAIM_ROLE, roleCode)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public long getExpirationSeconds() {
        return expirationMinutes * 60;
    }

    /**
     * Valida firma y vigencia del token. Retorna los claims si es válido o null si no lo es.
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token JWT inválido o expirado: {}", e.getMessage());
            return null;
        }
    }
}
