package com.harshbisht.UserService.config;

import com.harshbisht.UserService.security.JwtFilter;
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

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // 🔥 ONLY AUTH SERVICE (SERVICE TOKEN) CAN CREATE USER
                        .requestMatchers(HttpMethod.POST, "/users")
                        .hasRole("SERVICE")

                        // Normal users can view
                        .requestMatchers(HttpMethod.GET, "/users/**")
                        .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}