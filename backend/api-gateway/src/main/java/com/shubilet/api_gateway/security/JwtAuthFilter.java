package com.shubilet.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/logout",
            "/api/members/register"
    );

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractTokenFromCookie(request);
        if (token == null) {
            sendUnauthorized(response, "Missing JWT cookie");
            return;
        }

        Claims claims;
        try {
            claims = jwtService.validateAndExtract(token);
        } catch (JwtException e) {
            sendUnauthorized(response, "Invalid or expired JWT");
            return;
        }

        String role = claims.get("role", String.class);
        Long userId = claims.get("userId", Long.class);

        Set<String> required = RouteRoleRegistry.requiredRolesFor(path);
        if (required != null && !required.contains("ROLE_" + role)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("{\"message\":\"Access denied\"}");
            return;
        }

        // Forward identity to downstream services via headers
        request = wrapWithHeaders(request, userId, role);

        chain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "jwt".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    private HttpServletRequest wrapWithHeaders(HttpServletRequest request, Long userId, String role) {
        return new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("X-User-Id".equalsIgnoreCase(name)) return String.valueOf(userId);
                if ("X-User-Role".equalsIgnoreCase(name)) return role;
                return super.getHeader(name);
            }
        };
    }
}
