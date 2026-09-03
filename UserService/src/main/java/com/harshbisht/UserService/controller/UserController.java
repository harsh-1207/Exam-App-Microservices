package com.harshbisht.UserService.controller;

import com.harshbisht.UserService.dto.UserRequest;
import com.harshbisht.UserService.dto.UserResponse;
import com.harshbisht.UserService.exception.UnauthorizedAccessException;
import com.harshbisht.UserService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(                 // Create a new user
            @Valid @RequestBody UserRequest user
    ) {
        return userService.createUser(user);
    }

    @GetMapping("/me")
    public UserResponse getMyDetails(
            HttpServletRequest request
    ) {
        Long currentUserId = (Long) request.getAttribute("userId");

        if (currentUserId == null) {
            throw new UnauthorizedAccessException("Authentication required");
        }

        return userService.getMyDetails(currentUserId);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(                    // Get a specific user by their ID
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        Long requestingUserId =
                (Long) request.getAttribute("userId");

        return userService.getUser(
                id,
                requestingUserId
        );
    }
}