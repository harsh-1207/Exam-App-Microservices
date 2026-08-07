package com.harshbisht.ExamService.security;

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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;

@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_ROLES = Set.of(
            "STUDENT",
            "TEACHER",
            "ADMIN"
    );

    @Value("${internal.secret}")
    private String internalSecretExpected;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String internalSecret = request.getHeader("X-Internal-Secret");
        String userId = request.getHeader("X-User-Id");
        String role = request.getHeader("X-User-Role");

        /*
         * Every request reaching ExamService must come from a trusted
         * internal caller.
         */
        if (!isValidSecret(internalSecret)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden"
            );
            return;
        }

        /*
         * User identity headers must appear together.
         *
         * Valid:
         *   X-User-Id + X-User-Role
         *
         * Valid internal service call:
         *   neither header
         *
         * Invalid:
         *   only one of them
         */
        if ((userId == null) != (role == null)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid authentication headers"
            );
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            /*
             * Request forwarded by ApiGateway.
             */
            if (userId != null) {

                Long parsedUserId;

                try {
                    parsedUserId = Long.parseLong(userId);
                } catch (NumberFormatException ex) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Invalid user identity"
                    );
                    return;
                }

                if (parsedUserId <= 0) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Invalid user identity"
                    );
                    return;
                }

                String normalizedRole = role.trim().toUpperCase();

                /*
                 * Never allow an arbitrary value to become a Spring role.
                 */
                if (!ALLOWED_ROLES.contains(normalizedRole)) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN,
                            "Invalid user role"
                    );
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                parsedUserId,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + normalizedRole
                                        )
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                request.setAttribute("userId", parsedUserId);

            } else {

                /*
                 * Internal service-to-service request.
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                "internal-service",
                                null,
                                List.of(
                                        new SimpleGrantedAuthority("ROLE_SERVICE")
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidSecret(String providedSecret) {

        if (providedSecret == null || internalSecretExpected == null) {
            return false;
        }

        return MessageDigest.isEqual(
                internalSecretExpected.getBytes(StandardCharsets.UTF_8),
                providedSecret.getBytes(StandardCharsets.UTF_8)
        );
    }
}