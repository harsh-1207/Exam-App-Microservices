package com.harshbisht.UserService.dto;

import lombok.Builder;
import lombok.Data;

// FIX: Separate DTO for outgoing responses — controls exactly what gets serialized
// to JSON, so you can safely add sensitive fields to UserEntity later (e.g. password hash)
// without accidentally exposing them via the API.
@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
}