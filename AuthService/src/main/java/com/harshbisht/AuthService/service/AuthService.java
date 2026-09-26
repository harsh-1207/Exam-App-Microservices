package com.harshbisht.AuthService.service;

import com.harshbisht.AuthService.dto.LoginRequest;
import com.harshbisht.AuthService.dto.RegisterRequest;
import com.harshbisht.AuthService.dto.UserDto;
import com.harshbisht.AuthService.entity.AuthUser;
import com.harshbisht.AuthService.entity.Role;
import com.harshbisht.AuthService.exception.DuplicateEmailException;
import com.harshbisht.AuthService.exception.InvalidCredentialsException;
import com.harshbisht.AuthService.exception.RegistrationNotAllowedException;
import com.harshbisht.AuthService.exception.UserProfileCreationException;
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