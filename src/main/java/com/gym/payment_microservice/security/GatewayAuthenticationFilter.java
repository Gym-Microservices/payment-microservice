package com.gym.payment_microservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GatewayAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authSource = request.getHeader("X-Auth-Source");
        String userAgent = request.getHeader("User-Agent");

        // Detectar peticiones internas entre microservicios
        if (isInternalMicroserviceRequest(userAgent)) {
            // Crear token de sistema para comunicación interna
            GatewayAuthenticationToken authToken = new GatewayAuthenticationToken(
                    "system", "system", "system@gym.internal", 
                    Arrays.asList("ROLE_ADMIN", "ROLE_SYSTEM"));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } else if ("gateway".equals(authSource)) {
            String userId = request.getHeader("X-User-Id");
            String username = request.getHeader("X-User-Name");
            String email = request.getHeader("X-User-Email");
            String rolesHeader = request.getHeader("X-User-Roles");

            if (userId != null && !userId.isEmpty()) {
                List<String> roles = rolesHeader != null && !rolesHeader.isEmpty()
                        ? Arrays.asList(rolesHeader.split(","))
                        : List.of();

                GatewayAuthenticationToken authToken = new GatewayAuthenticationToken(
                        userId, username, email, roles);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isInternalMicroserviceRequest(String userAgent) {
        if (userAgent == null) return false;
        // Detectar peticiones de Java/Spring (RestTemplate)
        return userAgent.startsWith("Java/") || 
               userAgent.contains("Spring") ||
               userAgent.contains("RestTemplate");
    }
}
