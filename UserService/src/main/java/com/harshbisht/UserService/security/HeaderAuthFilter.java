package com.harshbisht.UserService.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Single filter handling two auth paths:
 *
 *   PRIORITY 1 — Gateway forwards user requests:
 *     Headers: X-Internal-Secret (validated) + X-User-Id + X-User-Role
 *     → authenticates as the actual user with their role
 *
 *   PRIORITY 2 — Internal service-to-service calls (e.g. AuthService creating a user):
 *     Headers: X-Internal-Secret only (no X-User-Id / X-User-Role)
 *     → authenticates as "internal-service" with ROLE_SERVICE
 *
 *   All other requests (no valid secret) → 403 Forbidden.
 */
@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    @Value("${internal.secret}")
    private String internalSecretExpected;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String internalSecret = request.getHeader("X-Internal-Secret");
        String userId         = request.getHeader("X-User-Id");
        String role           = request.getHeader("X-User-Role");

        // FIX: Only enforce the secret when it IS present (gateway/service calls).
        // Requests that arrive without the header at all are rejected here.
        // Requests that supply a wrong value are also rejected.
        boolean secretValid = internalSecretExpected.equals(internalSecret);

        if (!secretValid) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // At this point the shared secret is valid.
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // PRIORITY 1: Gateway forwarded a real user → use their identity.
            if (userId != null && role != null) {
                String formattedRole = role.toUpperCase();
                formattedRole = formattedRole.startsWith("ROLE_")
                        ? formattedRole
                        : "ROLE_" + formattedRole;

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority(formattedRole))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

                // FIX: Store parsed userId as a request attribute so downstream
                // services (UserService.getUser) can read it without touching HTTP headers.
                try {
                    request.setAttribute("userId", Long.parseLong(userId));
                } catch (NumberFormatException ignored) {
                    // non-numeric userId — leave attribute null, service will handle it
                }

            } else {
                // PRIORITY 2: Internal service call (no user headers) → ROLE_SERVICE.
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                "internal-service",
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}