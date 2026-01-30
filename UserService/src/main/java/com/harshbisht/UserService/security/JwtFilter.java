package com.harshbisht.UserService.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

/*
Flow :
A user calls GET /users/{id} with Authorization: Bearer <token>.
JwtFilter intercepts request, validates token, extracts role + userId.
Sets authentication in SecurityContext and attaches userId to request.
Controller checks if id matches userId or if user has ROLE_ADMIN.
If valid → returns profile.
If invalid/expired → request blocked with 401 Unauthorized.
 */

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // If no token → continue, security rules will block later
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            Claims claims = jwtUtil.extractClaims(token);

            String role = claims.get("role", String.class);
            String email = claims.getSubject();

            // 🔥 Prevent overriding existing authentication
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            // Attach userId only if present (SERVICE token doesn't have it)
            if (claims.get("userId") != null) {
                Long userId = ((Number) claims.get("userId")).longValue();
                request.setAttribute("userId", userId);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
