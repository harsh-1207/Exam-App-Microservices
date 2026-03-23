package com.harshbisht.ApiGateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/*
A global filter that intercepts every request passing through the gateway.
GlobalFilter: Runs for every request through the gateway.
Ordered: Lets you control execution order. Returning -1 means it runs early in the filter chain.

JWT is validated ONLY here
Everything else (UserService, ExamService) just trusts this layer

Client → Gateway (this filter)
       → JWT validated
       → headers injected
       → forwarded to services
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {
    // GlobalFilter -> runs for every request through gateway
    // Ordered → controls when it runs

//    private String secret = "supersecretkeysupersecretkeysadasdkasdkashdkashdaskdhaskdhaskjd";
    @Value("${jwt.secret}")
    private String secret;

    @Value("${internal.secret}")
    private String internalSecret;

    // The main method that runs for EACH request.
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Skip /auth/** paths
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Check Authorization header
        // If missing or not starting with Bearer, return 401 Unauthorized
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Parse JWT:
        try {
            String token = authHeader.substring(7);

            // Validates the token and extracts claims (like userId and role)
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Mutate request with user info
            // sent further to services, with these headers
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-Internal-Secret", "gateway-secret-123456789101112131415")
                    .header("X-User-Id", claims.get("userId").toString())
                    .header("X-User-Role", claims.get("role").toString())
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            System.out.println("GATEWAY JWT ERROR: " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
