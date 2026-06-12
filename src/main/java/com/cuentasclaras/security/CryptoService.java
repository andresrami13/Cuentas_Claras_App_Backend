package com.cuentasclaras.security;

import com.cuentasclaras.exception.SystemException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifrado simétrico AES-256-GCM para columnas sensibles (cifrado a nivel de aplicación).
 *
 * La clave vive fuera de la base de datos (variable de entorno DATA_ENCRYPTION_KEY),
 * de modo que quien tenga acceso a la base —incluido un administrador o un backup
 * robado— ve solo texto cifrado, no los datos financieros.
 *
 * GCM autentica el contenido (detecta manipulación) y usa un IV aleatorio por valor,
 * que se antepone al texto cifrado y se almacena junto a él en Base64.
 *
 * Los métodos son estáticos porque los AttributeConverter de JPA los instancia
 * Hibernate (no Spring) y no admiten inyección de dependencias.
 */
@Component
@Slf4j
public class CryptoService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int IV_LENGTH = 12;      // recomendado para GCM
    private static final int TAG_LENGTH_BITS = 128;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static SecretKey key;

    @Value("${app.security.data-encryption-key:}")
    private String configuredKey;

    @PostConstruct
    void init() {
        if (configuredKey == null || configuredKey.isBlank()) {
            log.warn("DATA_ENCRYPTION_KEY no está configurada: usando una clave de DESARROLLO insegura. "
                    + "No usar en producción; configure DATA_ENCRYPTION_KEY (32 bytes en Base64).");
            key = deriveKey("clave-desarrollo-cuentas-claras-no-usar-en-produccion");
            return;
        }
        byte[] decoded = Base64.getDecoder().decode(configuredKey);
        if (decoded.length != 32) {
            throw new IllegalStateException("DATA_ENCRYPTION_KEY debe ser de 32 bytes (AES-256) codificada en Base64");
        }
        key = new SecretKeySpec(decoded, "AES");
        log.info("Cifrado de datos sensibles inicializado correctamente");
    }

    private static SecretKey deriveKey(String passphrase) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(passphrase.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(hash, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo derivar la clave de desarrollo", e);
        }
    }

    public static String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_LENGTH];
            RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("Error al cifrar un dato sensible", e);
            throw new SystemException("Error al cifrar un dato sensible");
        }
    }

    public static String decrypt(String stored) {
        if (stored == null) {
            return null;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(stored);
            ByteBuffer buffer = ByteBuffer.wrap(combined);

            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error al descifrar un dato sensible", e);
            throw new SystemException("Error al descifrar un dato sensible");
        }
    }
}
