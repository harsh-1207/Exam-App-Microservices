package com.harshbisht.AuthService.config;

import feign.RequestInterceptor;
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

    private String internalSecret = "gateway-secret-123456789101112131415";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("X-Internal-Secret", internalSecret);
        };
    }
}