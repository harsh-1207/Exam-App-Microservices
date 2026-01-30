package com.harshbisht.AuthService.security;

import com.harshbisht.AuthService.entity.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
/*
Flow:
User logs in → AuthService calls generateToken(user) → returns JWT.
Client stores JWT and sends it in Authorization: Bearer <token>.
JwtFilter intercepts request → calls extractClaims(token) → validates and extracts role + userId.
If valid → request proceeds with authentication set.
If invalid/expired → request is blocked with 401 Unauthorized.
 */
@Component
public class JwtUtil {

    //@Value("${jwt.secret}")
    private String SECRET = "supersecretkeysupersecretkeysadasdkasdkashdkashdaskdhaskdhaskjd";

    // USER TOKEN (For when user is making a request)
    public String generateToken(AuthUser user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))            // Signs the token with your secret key using HMAC SHA
                .compact();
    }

    // SERVICE TOKEN (For when a service is making a request)
    public String generateServiceToken() {
        return Jwts.builder()
                .setSubject("AUTH-SERVICE")
                .claim("role", "SERVICE")
                .claim("type", "SERVICE")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 600000)) // 10 min
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();
    }

    // Validates and parses a JWT.
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()                    // Configures parser with your secret key
                .parseClaimsJws(token)
                .getBody();                 // Validates signature and returns the payload (Claims)
    }
}