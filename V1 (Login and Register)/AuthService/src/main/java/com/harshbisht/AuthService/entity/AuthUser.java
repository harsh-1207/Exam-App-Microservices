package com.harshbisht.AuthService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Long userId;
}