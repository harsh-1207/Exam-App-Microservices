package com.harshbisht.UserService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
/*
Flow:
A user calls GET /users/{id} with Authorization: Bearer <token>.
JwtFilter intercepts request, extracts token, and calls jwtUtil.extractClaims(token).
JwtUtil validates the signature using the secret key.
If valid → returns claims (role, userId, subject).
If invalid/expired → throws exception, leading to 401 Unauthorized.
 */
@Component
public class JwtUtil {

    private final String SECRET = "supersecretkeysupersecretkeysadasdkasdkashdkashdaskdhaskdhaskjd";

    // Validates and parses a JWT.
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}