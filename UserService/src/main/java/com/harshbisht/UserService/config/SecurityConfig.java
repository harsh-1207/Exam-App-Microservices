package com.harshbisht.UserService.config;

import com.harshbisht.UserService.security.HeaderAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 Auth flow summary:
   All requests MUST carry X-Internal-Secret (enforced by HeaderAuthFilter).
   HeaderAuthFilter then sets authentication based on what other headers are present:
     - X-User-Id + X-User-Role present  → authenticated as the real user (ROLE_STUDENT etc.)
     - X-User-Id + X-User-Role absent   → authenticated as internal service (ROLE_SERVICE)

   POST /users  — internal service only (AuthService registering a new user)
   GET  /users/{id} — authenticated users with STUDENT / TEACHER / ADMIN role
*/
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HeaderAuthFilter headerAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // FIX: Was permitAll() — that let ANY request through Spring Security
                        // even if HeaderAuthFilter already rejected it at the wrong layer.
                        // Restricting to ROLE_SERVICE means only AuthService (with the shared
                        // secret but no user headers) can create profiles.
                        .requestMatchers(HttpMethod.POST, "/users")
                        .hasRole("SERVICE")

                        // User-facing read endpoint
                        .requestMatchers(HttpMethod.GET, "/users/**")
                        .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}