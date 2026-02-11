//package com.harshbisht.WebService.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    //@Value("${jwt.secret}")
//    private String SECRET = "supersecretkeysupersecretkeysadasdkasdkashdkashdaskdhaskdhaskjd";
//
//    // SERVICE TOKEN (For when a service is making a request)
//    public String generateServiceToken() {
//        return Jwts.builder()
//                .setSubject("AUTH-SERVICE")
//                .claim("role", "SERVICE")
//                .claim("type", "SERVICE")
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 600000)) // 10 min
//                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
//                .compact();
//    }
//
//    // Validates and parses a JWT.
//    public Claims extractClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(SECRET.getBytes())
//                .build()                    // Configures parser with your secret key
//                .parseClaimsJws(token)
//                .getBody();                 // Validates signature and returns the payload (Claims)
//    }
//}