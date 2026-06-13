package com.cuentasclaras.security;

import com.cuentasclaras.model.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limitador de peticiones en memoria (ventana fija) por dirección IP.
 * Protege el login contra fuerza bruta y el coach IA contra abuso de costos.
 * Pensado para una sola instancia; si en el futuro se escala horizontalmente,
 * migrar a un almacén compartido (p. ej. Redis).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Value("${app.security.rate-limit.login.max-requests:5}")
    private int loginMaxRequests;

    @Value("${app.security.rate-limit.login.window-seconds:60}")
    private long loginWindowSeconds;

    @Value("${app.security.rate-limit.ai-coach.max-requests:15}")
    private int aiCoachMaxRequests;

    @Value("${app.security.rate-limit.ai-coach.window-seconds:3600}")
    private long aiCoachWindowSeconds;

    @Value("${app.security.rate-limit.google.max-requests:10}")
    private int googleMaxRequests;

    @Value("${app.security.rate-limit.google.window-seconds:60}")
    private long googleWindowSeconds;

    private final Map<String, long[]> loginCounters = new ConcurrentHashMap<>();
    private final Map<String, long[]> aiCoachCounters = new ConcurrentHashMap<>();
    private final Map<String, long[]> googleCounters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isPost = HttpMethod.POST.matches(request.getMethod());

        if (isPost && "/users/login".equals(path)) {
            if (!allow(loginCounters, clientIp(request), loginMaxRequests, loginWindowSeconds * 1000)) {
                tooManyRequests(response, "Demasiados intentos de inicio de sesión. Intente de nuevo en unos minutos.");
                return;
            }
        } else if (isPost && ("/users/login/google".equals(path) || "/users/google".equals(path))) {
            // No hay contraseña que adivinar, pero se limita para evitar abuso de la verificación
            // de tokens y la creación masiva de cuentas.
            if (!allow(googleCounters, clientIp(request), googleMaxRequests, googleWindowSeconds * 1000)) {
                tooManyRequests(response, "Demasiadas solicitudes de acceso con Google. Intente de nuevo en unos minutos.");
                return;
            }
        } else if (isPost && "/ai-coach/advice".equals(path)) {
            if (!allow(aiCoachCounters, clientIp(request), aiCoachMaxRequests, aiCoachWindowSeconds * 1000)) {
                tooManyRequests(response, "Ha alcanzado el límite de consultas al coach IA. Intente de nuevo más tarde.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Ventana fija atómica: si la ventana expiró se reinicia; si no, se incrementa
     * mientras no se supere el límite. v = {inicioVentanaMillis, conteo}.
     */
    private boolean allow(Map<String, long[]> store, String key, int limit, long windowMillis) {
        // Evita crecimiento ilimitado del mapa ante muchas IPs distintas
        if (store.size() > 50_000) {
            store.clear();
        }
        long now = System.currentTimeMillis();
        boolean[] allowed = {false};
        store.compute(key, (k, v) -> {
            if (v == null || now - v[0] >= windowMillis) {
                allowed[0] = true;
                return new long[]{now, 1};
            }
            if (v[1] < limit) {
                v[1]++;
                allowed[0] = true;
                return v;
            }
            allowed[0] = false;
            return v;
        });
        return allowed[0];
    }

    private String clientIp(HttpServletRequest request) {
        // Railway y otros proxies anteponen la IP real en X-Forwarded-For
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void tooManyRequests(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429); // 429 Too Many Requests
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.of(429, message, null)));
    }
}
