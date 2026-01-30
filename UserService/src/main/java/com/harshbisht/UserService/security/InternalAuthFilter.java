//package com.harshbisht.UserService.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
///*
//Flow:
//AuthService calls POST /users with header X-INTERNAL-KEY: supersecretkey....
//InternalAuthFilter intercepts request.
//Validates the key → marks request as authenticated.
//Controller (UserController.createUser) executes and saves the profile.
//If someone tries to call POST /users without the key → blocked with 403 Forbidden
// */
//@Component
//public class InternalAuthFilter extends OncePerRequestFilter {
//
//    // @Value("${jwt.internalSecret}")
//    private final String INTERNAL_KEY = "supersecretkeysupersecretkeysadasdkasdkashdkashdaskdhaskdhaskjd";
//
//    // Runs for every request before it reaches your controllers
//    @Override
//    protected void doFilterInternal(HttpServletRequest req,
//                                    HttpServletResponse res,
//                                    FilterChain chain)
//            throws ServletException, IOException {
//        // Check if request is POST /users, Only applies to user creation requests
//        if (req.getMethod().equals("POST") && req.getRequestURI().startsWith("/users")) {
//
//            String key = req.getHeader("X-INTERNAL-KEY");
//
//            // Validate X-INTERNAL-KEY header
//            if (key == null || !key.equals(INTERNAL_KEY)) {
//                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                return;
//            }
//            // Mark request as authenticated internally
//            UsernamePasswordAuthenticationToken auth =
//                    new UsernamePasswordAuthenticationToken("internal-service", null, Collections.emptyList());
//
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
//
//        chain.doFilter(req, res);
//    }
//}