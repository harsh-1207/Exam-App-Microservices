package com.harshbisht.AuthService.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
/*
User logs in → gets JWT.
User calls /auth/profile with Authorization: Bearer <token>.
JwtFilter intercepts request, validates token, extracts role + subject.
Sets authentication in SecurityContext.
Controller can now access Authentication object to know the user’s identity and role.
If token is invalid → request is blocked with 401 Unauthorized.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // Runs for every request before it reaches your controllers.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            // If header valid, Validate token if present
            String token = header.substring(7);

            try {
                Claims claims = jwtUtil.extractClaims(token);
                String role = claims.get("role", String.class);

                /*
                Creates a Spring Security authentication object:
                claims.getSubject() → usually the username/email.
                ROLE_<role> → attaches the user’s role as an authority.
                 */
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // Token invalid or expired
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}