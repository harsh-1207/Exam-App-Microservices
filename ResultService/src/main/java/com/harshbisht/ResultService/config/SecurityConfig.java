package com.harshbisht.ResultService.config;

import com.harshbisht.ResultService.security.HeaderAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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

                // Only TEACHER can create, edit and delete exams/subjects/questions
                .requestMatchers(HttpMethod.POST, "/result/**")
                .hasAnyRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/result/**")
                .hasAnyRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/result/**")
                .hasAnyRole("TEACHER")

                // Students can view exams
                .requestMatchers(HttpMethod.GET, "/result/**")
                .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                .anyRequest().authenticated()
        );

        http.addFilterBefore(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
