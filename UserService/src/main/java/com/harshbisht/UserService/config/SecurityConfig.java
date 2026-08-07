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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HeaderAuthFilter headerAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Only internal services can create user profiles.
                         *
                         * AuthService sends:
                         * X-Internal-Secret
                         *
                         * but does NOT send:
                         * X-User-Id
                         * X-User-Role
                         *
                         * Therefore HeaderAuthFilter assigns ROLE_SERVICE.
                         */
                        .requestMatchers(HttpMethod.POST, "/users")
                        .hasRole("SERVICE")

                        /*
                         * User profile reads require an authenticated user.
                         */
                        .requestMatchers(HttpMethod.GET, "/users/**")
                        .hasAnyRole(
                                "STUDENT",
                                "TEACHER",
                                "ADMIN"
                        )

                        /*
                         * Fail closed.
                         *
                         * Do NOT use authenticated() here because that would
                         * allow ROLE_SERVICE to access endpoints that haven't
                         * explicitly been protected above.
                         */
                        .anyRequest()
                        .denyAll()
                )

                .addFilterBefore(
                        headerAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}