package com.harshbisht.WebService.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshbisht.WebService.dto.AuthResponse;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/*
 * WebService calls other services directly via Feign, so it must mimic the
 * gateway headers that downstream services expect: internal secret plus the
 * current user's identity.
 */
@Configuration
public class FeignConfig {

    @Value("${internal.secret:gateway-secret-123456789101112131415}")
    private String internalSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("X-Internal-Secret", internalSecret);

            var requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                HttpSession session = servletRequestAttributes.getRequest().getSession(false);
                if (session != null) {
                    AuthResponse authResponse =
                            (AuthResponse) session.getAttribute("authResponse");
                    if (authResponse != null && authResponse.getToken() != null) {
                        String token = authResponse.getToken();
                        template.header("Authorization", "Bearer " + token);
                        populateUserHeaders(template, token);
                    }
                }
            }
        };
    }

    private void populateUserHeaders(feign.RequestTemplate template, String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode claims = objectMapper.readTree(payload);

            if (claims.has("userId")) {
                template.header("X-User-Id", claims.get("userId").asText());
            }

            if (claims.has("role")) {
                template.header("X-User-Role", claims.get("role").asText());
            }
        } catch (Exception ignored) {
            // Ignore malformed tokens; downstream will reject if the user is not authenticated.
        }
    }
}
