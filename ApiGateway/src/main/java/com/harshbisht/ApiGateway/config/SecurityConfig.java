package com.harshbisht.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/*
Defines how Spring Security should behave for incoming requests at the gateway.
*/
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(ex -> ex
                        .pathMatchers("/auth/**").permitAll()       // allowed without authentication
                        .anyExchange().authenticated()                          // Every other request must be authenticated
                )
                .build();
    }
}
