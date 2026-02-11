package com.harshbisht.ExamService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    //@Value("${jwt.secret}")
    private String SECRET = "supersecretkeysupersecretkeysadasdkasdkashdkashdaskdhaskdhaskjd";

    // Validates and parses a JWT.
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()                    // Configures parser with your secret key
                .parseClaimsJws(token)
                .getBody();                 // Validates signature and returns the payload (Claims)
    }
}