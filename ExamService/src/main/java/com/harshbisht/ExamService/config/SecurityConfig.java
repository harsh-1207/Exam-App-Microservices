package com.harshbisht.ExamService.config;

import com.harshbisht.ExamService.security.HeaderAuthFilter;
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

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth

                // Only TEACHER can create, edit, delete
                .requestMatchers(HttpMethod.POST,   "/subjects/**", "/exams/**", "/questions/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.PUT,    "/subjects/**", "/exams/**", "/questions/**").hasRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/subjects/**", "/exams/**", "/questions/**").hasRole("TEACHER")

                // FIX: GET /exams/{id}/attempt was covered by the broad GET /exams/** rule
                // which already permits STUDENT. However, making it explicit here documents
                // intent clearly and ensures it stays correct if rules are reordered.
                // Students should only reach published exams — enforced in ExamService, not here.
                .requestMatchers(HttpMethod.GET, "/exams/*/attempt").hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                // All authenticated roles can read
                .requestMatchers(HttpMethod.GET, "/subjects/**", "/exams/**", "/questions/**")
                .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                .anyRequest().authenticated()
        );

        http.addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}