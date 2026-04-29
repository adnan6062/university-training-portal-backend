package com.university.gateway.filter;

import com.university.gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * IMPORTANT:
     * All Swagger, OpenAPI, fallback, actuator paths MUST bypass JWT.
     * Otherwise Swagger UI will get 403 when fetching /v3/api-docs.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        // ✅ Always allow preflight (Swagger uses this)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        return
            // ===== Auth / Infra =====
            path.startsWith("/identity/auth/")
            || path.startsWith("/actuator")
            || path.startsWith("/fallback")

            // ===== Swagger UI (any /swagger* path springdoc may register) =====
            || path.startsWith("/swagger")
            || path.startsWith("/webjars")

            // ===== OpenAPI (local + gateway-proxied) =====
            || path.contains("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.isValid(token)) {
            sendUnauthorized(response, "Invalid or expired JWT token");
            return;
        }

        Claims claims = jwtUtil.extractAllClaims(token);

        // Propagate to downstream services
        request.setAttribute("userId", claims.get("userId"));
        request.setAttribute("username", claims.getSubject());
        request.setAttribute("roles", claims.get("roles"));

        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
