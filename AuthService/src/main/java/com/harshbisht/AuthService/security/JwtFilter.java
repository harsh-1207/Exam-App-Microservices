package com.harshbisht.AuthService.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


/*
This filter extracts the JWT from the request,
validates it, and converts it into a Spring Security
Authentication object, which is stored in the SecurityContext.

This enables Spring Security to treat the request as authenticated
and enforce access rules accordingly.
*/
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // If no token → continue (Spring Security will handle auth rules)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);

            Claims claims = jwtUtil.extractClaims(token);

            String role = claims.get("role", String.class);
            String formattedRole = role.toUpperCase();
            formattedRole = formattedRole.startsWith("ROLE_") ? formattedRole : "ROLE_" + formattedRole;

            Long userId = ((Number) claims.get("userId")).longValue();

            // Prevent overriding existing authentication
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId,                                                     // principal
                                null,                                                       // credentials
                                List.of(new SimpleGrantedAuthority(formattedRole))          // authorities (ROLE_USER, etc)
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
            // Debug-friendly logging
            System.out.println("JWT ERROR: " + e.getMessage());

            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}