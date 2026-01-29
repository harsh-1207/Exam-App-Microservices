package com.harshbisht.UserService.config;

import com.harshbisht.UserService.security.InternalAuthFilter;
import com.harshbisht.UserService.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/*
Flow:
Registration flow
AuthService calls POST /users with X-INTERNAL-KEY.
InternalAuthFilter validates the key → marks request as authenticated.
User profile is created.

User-facing flow
A logged-in user calls GET /users/{id} with Authorization: Bearer <token>.
JwtFilter validates JWT → extracts role.
Security rules check role → only STUDENT, TEACHER, ADMIN can access.
Request proceeds if valid.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final InternalAuthFilter internalAuthFilter;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // this is where AuthService calls to create a profile, using the internal key (POST)
                .requestMatchers(HttpMethod.POST, "/users").authenticated()
                // only accessible to roles STUDENT, TEACHER, or ADMIN (GET)
                .requestMatchers(HttpMethod.GET, "/users/**")
                .hasAnyRole("STUDENT","TEACHER","ADMIN")
                // any other is authenticated
                .anyRequest().authenticated()
        );

        // FIRST: internal service auth
        // InternalAuthFilter runs → checks if the request is an internal POST to /users with the correct X-INTERNAL-KEY.
        http.addFilterBefore(internalAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // SECOND: JWT auth for users
        // JwtFilter runs → validates JWTs for user-facing requests
        http.addFilterAfter(jwtFilter, InternalAuthFilter.class);

        return http.build();
    }
}