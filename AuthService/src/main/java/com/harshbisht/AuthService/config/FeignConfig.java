package com.harshbisht.AuthService.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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