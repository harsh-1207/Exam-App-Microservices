package com.harshbisht.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/*
Defines how Spring Security should behave for incoming requests at the gateway.
Incoming request → passes through this chain → then reaches your Gateway filters

At the API Gateway, Spring Security is configured to permit all requests,
and authentication is handled via a custom GlobalFilter that validates JWTs
and injects user identity into headers for downstream services.
*/
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        /*
        CSRF is needed when you use sessions + cookies (browser-based auth)
        You are using JWT (stateless, header-based auth)
        So CSRF is unnecessary.
        */
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(ex -> ex
                        .anyExchange().permitAll()      // Allow ALL requests through Spring Security (No authentication, No role checks, No blocking)
                )
                .build();
    }
}

