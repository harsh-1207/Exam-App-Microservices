package com.harshbisht.AuthService.dto;

import com.harshbisht.AuthService.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}

