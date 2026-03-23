package com.harshbisht.AuthService.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Automatically attach INTERNAL SECRET to every Feign request
// used for Service to Service calls
/*
We use a Feign RequestInterceptor to automatically attach an internal
shared secret header to all service-to-service calls. Downstream services
validate this secret and assign a SERVICE role, allowing secure internal communication.
*/
@Configuration
public class FeignConfig {

    @Value("${internal.secret}")
    private String internalSecret;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("X-Internal-Secret", internalSecret);
        };
    }
}