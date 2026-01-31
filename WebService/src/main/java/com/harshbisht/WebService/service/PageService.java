package com.harshbisht.WebService.service;

import com.harshbisht.WebService.dto.AuthResponse;
import com.harshbisht.WebService.dto.LoginRequest;
import com.harshbisht.WebService.dto.RegisterRequest;
import com.harshbisht.WebService.external.AuthFeignClient;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageService {

    private final AuthFeignClient authFeign;

    public String login(LoginRequest req, HttpSession session){
        // call login from AuthService, and get the token
        AuthResponse response = authFeign.login(req);
        String token = response.getToken();

        // Store token in session
        session.setAttribute("token", token);

        // Decode ONLY non-sensitive info (no signature validation here)
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

        String role = payload.contains("STUDENT") ? "STUDENT"
                : payload.contains("TEACHER") ? "TEACHER"
                : "ADMIN";

        // TODO: need to call UserService, getUserByEmail, add name in session
        session.setAttribute("role", role);
        session.setAttribute("email", req.getEmail());

        return role;
    }

    public void register(RegisterRequest req) {
        authFeign.register(req);
    }
}
