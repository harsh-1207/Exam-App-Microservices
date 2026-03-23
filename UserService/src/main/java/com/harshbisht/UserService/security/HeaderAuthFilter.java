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
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        // Validate internal secret IF present
        if (!internalSecretExpected.equals(internalSecret)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // 🟢 PRIORITY 1: USER AUTH (from Gateway)
        if (userId != null && role != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            String formattedRole = role.toUpperCase();
            formattedRole = formattedRole.startsWith("ROLE_") ? formattedRole : "ROLE_" + formattedRole;

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            List.of(new SimpleGrantedAuthority(formattedRole))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 🟡 PRIORITY 2: INTERNAL SERVICE AUTH (only if no user)
        else if (internalSecret != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            "internal-service",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}