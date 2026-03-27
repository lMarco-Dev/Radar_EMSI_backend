package com.emsi.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().startsWith("/api/auth/login") && "POST".equalsIgnoreCase(request.getMethod())) {
            String ip = request.getRemoteAddr();
            Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.setHeader("Access-Control-Allow-Origin", "https://emsiradar.vercel.app");
                response.setHeader("Access-Control-Allow-Credentials", "true");

                response.getWriter().write("{\"success\": false, \"message\": \"Demasiados intentos de inicio de sesión. Por favor, intenta en 1 minuto.\"}");
                return;
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}