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
        String userId         = request.getHeader("X-User-Id");
        String role           = request.getHeader("X-User-Role");

        // FIX 1: The old code called Long.valueOf(userId) BEFORE the secret check
        // and BEFORE the null check on userId. If X-User-Id is absent (e.g. an
        // internal service call), Long.valueOf(null) throws a NullPointerException
        // immediately — the request dies before reaching the 403 branch.
        // Solution: validate the secret first, then parse userId only when it's present.

        if (!internalSecretExpected.equals(internalSecret)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            // PRIORITY 1: Gateway-forwarded user request (has userId + role)
            if (userId != null && role != null) {

                // FIX 2: Parse userId here, after null-safety is confirmed.
                // Wrap in try-catch so a malformed header returns 400 rather than a 500 NPE.
                Long userIdLong;
                try {
                    userIdLong = Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                String formattedRole = role.toUpperCase();
                formattedRole = formattedRole.startsWith("ROLE_") ? formattedRole : "ROLE_" + formattedRole;

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userIdLong,
                                null,
                                List.of(new SimpleGrantedAuthority(formattedRole))
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

                // FIX 3: Store parsed userId as a request attribute.
                // Services can read req.getAttribute("userId") instead of calling
                // Long.parseLong(principal.toString()), which is fragile if the
                // principal type ever changes.
                request.setAttribute("userId", userIdLong);

            } else {
                // PRIORITY 2: Internal service call (secret present, no user headers)
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