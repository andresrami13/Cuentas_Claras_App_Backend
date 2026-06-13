package com.cuentasclaras.config;

import com.cuentasclaras.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cuentasclaras.model.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;

    @Value("${app.security.admin-role-code}")
    private String adminRoleCode;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Públicos: login y registro (local y con Google)
                        .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/login/google").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/google").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        // Documentación del API (restringir o apagar en producción vía springdoc)
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Gestión administrativa: solo rol administrador
                        .requestMatchers("/roles/**", "/permissions/**").hasRole(adminRoleCode)
                        .requestMatchers(HttpMethod.GET, "/users").hasRole(adminRoleCode)
                        .requestMatchers(HttpMethod.PATCH, "/users/*/lock", "/users/*/role").hasRole(adminRoleCode)
                        .requestMatchers(HttpMethod.GET, "/ai-coach/health").hasRole(adminRoleCode)
                        // Todo lo demás requiere usuario autenticado
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        // Evita que el navegador "adivine" tipos de contenido (anti-MIME-sniffing)
                        .contentTypeOptions(c -> {})
                        // Impide que la app se incruste en iframes (anti-clickjacking)
                        .frameOptions(frame -> frame.deny())
                        // Exige HTTPS en visitas futuras (efectivo cuando se sirve por HTTPS)
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "No autenticado: se requiere un token válido"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                                        "No tiene permisos para acceder a este recurso"))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeJsonError(HttpServletResponse response, int status, String message) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.of(status, message, null)));
    }
}
