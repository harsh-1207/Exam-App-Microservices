package com.harshbisht.WebService.config;

import com.harshbisht.WebService.dto.AuthResponse;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/*
Sending the user's JWT with the feign call to other services
So that only the right role can access the protected APIs
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                HttpSession session = servletRequestAttributes.getRequest().getSession(false);
                if (session != null) {
                    AuthResponse authResponse =
                            (AuthResponse) session.getAttribute("authResponse");
                    if (authResponse != null && authResponse.getToken() != null) {
                        template.header(
                                "Authorization",
                                "Bearer " + authResponse.getToken()
                        );
                    }
                }
            }
        };
    }
}
