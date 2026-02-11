package com.harshbisht.ExamService.config;

import com.harshbisht.ExamService.security.JwtFilter;
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

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests(auth -> auth

                // Only TEACHER can create, edit and delete exams/subjects/questions
                .requestMatchers(HttpMethod.POST, "/subjects/**", "/exams/**", "/questions/**")
                .hasAnyRole("TEACHER")
                .requestMatchers(HttpMethod.PUT, "/subjects/**", "/exams/**", "/questions/**")
                .hasAnyRole("TEACHER")
                .requestMatchers(HttpMethod.DELETE, "/subjects/**", "/exams/**", "/questions/**")
                .hasAnyRole("TEACHER")

                // Students can view exams
                .requestMatchers(HttpMethod.GET, "/subjects/**", "/exams/**", "/questions/**")
                .hasAnyRole("STUDENT", "TEACHER", "ADMIN")

                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
