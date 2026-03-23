package com.harshbisht.AuthService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FIX: The old version only had id + name. This DTO is sent via Feign to
 * UserService's POST /users endpoint, which now expects name + email + role
 * (matching UserService's UserRequest). If they're out of sync, the profile
 * is created with blank fields and the whole registration flow silently breaks.
 *
 * The response from UserService (id + name + email + role) is also received
 * via this same DTO — so it doubles as both the request and response shape.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
}