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

    @Value("${jwt.secret}")
    private String secret;

    @Value("${internal.secret}")
    private String internalSecret;

    // The main method that runs for EACH request.
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // SECURITY FIX: strip any client-supplied trust headers on every request,
        // including /auth/** (which skips JWT validation below). These three
        // headers are how downstream services decide who the caller is — a
        // client must never be able to set them directly. Doing this
        // unconditionally, before the /auth short-circuit, means AuthService
        // is protected too, even though it doesn't currently rely on these
        // headers — if that ever changes, this still holds.
        ServerHttpRequest strippedRequest = exchange.getRequest().mutate()
                .headers(httpHeaders -> {
                    httpHeaders.remove("X-Internal-Secret");
                    httpHeaders.remove("X-User-Id");
                    httpHeaders.remove("X-User-Role");
                })
                .build();
        exchange = exchange.mutate().request(strippedRequest).build();

        // Skip /auth/** paths — no JWT to validate yet (login/register happen here)
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

            // SECURITY FIX (bug 1): use the injected internalSecret field, not a
            // hardcoded literal. The old code declared @Value("${internal.secret}")
            // but then ignored it and sent a fixed string instead — changing
            // internal.secret anywhere (e.g. via env var in a real deployment)
            // would silently break every downstream call, since the gateway kept
            // sending the stale hardcoded value forever.
            //
            // SECURITY FIX (bug 2): use .headers(h -> h.set(...)) instead of
            // .header(name, value). Builder#header() ADDS a value alongside any
            // existing header of the same name rather than replacing it. Since
            // we already stripped client-supplied values above, this is now
            // belt-and-suspenders — but .set() is also just the correct method
            // to express "this header has exactly this one value."
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .headers(httpHeaders -> {
                        httpHeaders.set("X-Internal-Secret", internalSecret);
                        httpHeaders.set("X-User-Id", claims.get("userId").toString());
                        httpHeaders.set("X-User-Role", claims.get("role").toString());
                    })
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