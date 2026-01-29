package com.harshbisht.AuthService.repository;

import com.harshbisht.AuthService.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findFirstByEmail(String email);
    Boolean existsByEmail(String email);
}
