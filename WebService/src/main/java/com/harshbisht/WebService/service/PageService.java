package com.harshbisht.WebService.service;

import com.harshbisht.WebService.dto.AuthResponse;
import com.harshbisht.WebService.dto.LoginRequest;
import com.harshbisht.WebService.dto.RegisterRequest;
import com.harshbisht.WebService.dto.UserResponse;
import com.harshbisht.WebService.external.AuthFeignClient;
import com.harshbisht.WebService.external.UserFeignClient;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PageService {

    private final AuthFeignClient authFeign;
    private final UserFeignClient userFeign;

    public String login(LoginRequest req, HttpSession session){
        AuthResponse response = authFeign.login(req);
        String token = response.getToken();

        session.setAttribute("token", token);
        session.setAttribute("authResponse", response);

        String role = resolveRole(token);
        String displayName = "User";

        try {
            UserResponse user = userFeign.getMyDetails();
            if (user != null && user.getName() != null && !user.getName().isBlank()) {
                displayName = user.getName();
            }
        } catch (Exception ignored) {
            // Keep the fallback name if the user profile is unavailable.
        }

        String email = req.getEmail() == null ? "" : req.getEmail().trim();
        session.setAttribute("role", role);
        session.setAttribute("email", email);
        session.setAttribute("name", displayName);

        return role;
    }

    public void register(RegisterRequest req) {
        authFeign.register(req);
    }

    private String resolveRole(String token) {
        String payload = decodePayload(token);
        if (payload == null) {
            return "USER";
        }

        if (payload.contains("STUDENT")) return "STUDENT";
        if (payload.contains("TEACHER")) return "TEACHER";
        return "ADMIN";
    }

    private Long extractUserId(String token) {
        String payload = decodePayload(token);
        if (payload == null) {
            return null;
        }

        try {
            int userIdIndex = payload.indexOf("\"userId\"");
            if (userIdIndex < 0) {
                return null;
            }

            int colonIndex = payload.indexOf(':', userIdIndex);
            if (colonIndex < 0) {
                return null;
            }

            String afterColon = payload.substring(colonIndex + 1).trim();
            int endIndex = afterColon.indexOf(',');
            if (endIndex < 0) {
                endIndex = afterColon.indexOf('}');
            }
            if (endIndex < 0) {
                return null;
            }

            String userIdText = afterColon.substring(0, endIndex).trim();
            if (userIdText.startsWith("\"") && userIdText.endsWith("\"")) {
                userIdText = userIdText.substring(1, userIdText.length() - 1);
            }
            return Long.parseLong(userIdText);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String decodePayload(String token) {
        if (token == null || token.split("\\.").length < 2) {
            return null;
        }

        try {
            String[] parts = token.split("\\.");
            return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return null;
        }
    }
}
