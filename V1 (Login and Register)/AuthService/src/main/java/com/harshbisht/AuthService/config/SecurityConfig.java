package com.harshbisht.AuthService.config;

import com.harshbisht.AuthService.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/*
Flow:
User hits /auth/register or /auth/login → allowed without token.
User gets a JWT after login.
User calls /auth/profile (or any other protected endpoint).
JwtFilter intercepts request, validates token, sets authentication in the security context.
If valid → request proceeds to controller.
If invalid/missing → response is 401 Unauthorized
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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                // It runs before the default username/password filter.
                //This ensures JWTs are validated before any request reaches your controllers.
        return http.build();
    }
}