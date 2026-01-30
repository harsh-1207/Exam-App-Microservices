package com.harshbisht.AuthService.config;

import com.harshbisht.AuthService.security.JwtUtil;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String token = jwtUtil.generateServiceToken();
            template.header("Authorization", "Bearer " + token);
        };
    }
}

