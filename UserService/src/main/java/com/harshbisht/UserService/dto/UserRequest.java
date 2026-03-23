package com.harshbisht.UserService.dto;

import lombok.Data;

// FIX: Separate DTO for incoming requests — never expose JPA entities directly
// in your controller layer (mass-assignment risk + leaks DB structure).
@Data
public class UserRequest {
    private String name;
    private String email;
    private String role;
}