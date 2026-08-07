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

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * TEACHER exam-management APIs.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/exams/**",
                                "/questions/**"
                        ).hasRole("TEACHER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/exams/**",
                                "/questions/**"
                        ).hasRole("TEACHER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/exams/**",
                                "/questions/**"
                        ).hasRole("TEACHER")

                        /*
                         * Subject management.
                         *
                         * Existing controller documentation says
                         * TEACHER / ADMIN.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/subjects/**"
                        ).hasAnyRole("TEACHER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/subjects/**"
                        ).hasAnyRole("TEACHER", "ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/subjects/**"
                        ).hasAnyRole("TEACHER", "ADMIN")

                        /*
                         * Teacher's own exams.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/exams/my"
                        ).hasRole("TEACHER")

                        /*
                         * Students use this endpoint to start an exam.
                         * Teachers/Admins should not use the student attempt API.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/exams/*/attempt"
                        ).hasRole("STUDENT")

                        /*
                         * Questions contain the correct-answer flag.
                         * Therefore students MUST NOT access these endpoints.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/exams/*/questions",
                                "/exams/*/questions/*"
                        ).hasAnyRole("TEACHER", "ADMIN")

                        /*
                         * Normal exam/subject reads.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/subjects/**"
                        ).hasAnyRole(
                                "STUDENT",
                                "TEACHER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/exams/**"
                        ).hasAnyRole(
                                "STUDENT",
                                "TEACHER",
                                "ADMIN"
                        )

                        /*
                         * Everything else is denied.
                         *
                         * This is important because ROLE_SERVICE should not
                         * automatically receive access to future endpoints.
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