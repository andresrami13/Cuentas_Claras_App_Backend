package com.udistrital.spendcount.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Encryption {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encrypt(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        return passwordEncoder.encode(password);
    }

    public boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña almacenada no puede ser nula o vacía");
        }
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }
}