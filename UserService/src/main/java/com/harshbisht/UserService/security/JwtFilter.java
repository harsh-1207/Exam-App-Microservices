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
        // Looks for Authorization: Bearer <token>
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                // extract token and claims from header
                String token = header.substring(7);
                Claims claims = jwtUtil.extractClaims(token);

                String role = claims.get("role", String.class);
                Long userId = claims.get("userId", Long.class);

                // Creates a Spring Security authentication object:
                // claims.getSubject() → usually the email.
                // ROLE_<role> → attaches the user’s role as an authority.
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Attach userId to request
                request.setAttribute("userId", userId);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}