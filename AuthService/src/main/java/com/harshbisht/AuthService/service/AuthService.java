package com.harshbisht.AuthService.service;

import com.harshbisht.AuthService.dto.LoginRequest;
import com.harshbisht.AuthService.dto.RegisterRequest;
import com.harshbisht.AuthService.dto.UserDto;
import com.harshbisht.AuthService.dto.UserEntity;
import com.harshbisht.AuthService.entity.AuthUser;
import com.harshbisht.AuthService.entity.Role;
import com.harshbisht.AuthService.exception.*;
import com.harshbisht.AuthService.external.UserFeignClient;
import com.harshbisht.AuthService.repository.AuthUserRepository;
import com.harshbisht.AuthService.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository repo;
    private final PasswordEncoder encoder;
    private final UserFeignClient userFeign;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    @Transactional
    public String register(RegisterRequest req) {

        // Prevent duplicate email
        if (repo.existsByEmail(req.getEmail())) {
            // throw new RuntimeException("Email already registered");
            throw new DuplicateEmailException("Email already registered");
        }

        // Block ADMIN self-registration
        if (req.getRole() == Role.ADMIN) {
            // throw new RuntimeException("Cannot register as ADMIN");
            throw new RegistrationNotAllowedException("Cannot register as ADMIN");
        }

        // Create auth record first
        AuthUser user = new AuthUser();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());

        repo.save(user); // saves ID

        try {
            // Create profile in User Service
            UserDto profile = UserDto.builder()
                    .name(req.getName())
                    .build();

            UserDto savedUser = userFeign.createUser(profile);

            // Link userId from user service
            user.setUserId(savedUser.getId());
            repo.save(user);

        } catch (Exception e) {
            // rollback auth user if user-service fails
            repo.delete(user);
            // throw new RuntimeException("User profile creation failed");
            throw new UserProfileCreationException("User profile creation failed");
        }

        return "User Registered Successfully";
    }

    /**
     * SECURITY FIX: previously threw UserNotFoundException (404) when the email
     * didn't exist, and InvalidCredentialsException (401) when the password was
     * wrong. Two different exceptions with two different status codes let an
     * attacker enumerate valid registered emails just by watching which error
     * came back — a classic username-enumeration vulnerability (OWASP ASVS 2.1.7 /
     * CWE-203). Both failure paths now throw the exact same exception with the
     * exact same message, so a bad email and a bad password are indistinguishable
     * from the response.
     *
     * Also wired in LoginAttemptService: 5 failed attempts locks that email out
     * for 15 minutes, closing the brute-force / credential-stuffing gap that
     * existed before (unlimited attempts, no penalty).
     */
    public String login(LoginRequest req) {

        loginAttemptService.assertNotLocked(req.getEmail());

        AuthUser user = repo.findFirstByEmail(req.getEmail()).orElse(null);

        if (user == null || !encoder.matches(req.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailure(req.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        loginAttemptService.recordSuccess(req.getEmail());
        return jwtUtil.generateToken(user);
    }
}