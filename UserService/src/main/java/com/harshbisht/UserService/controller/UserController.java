package com.harshbisht.UserService.controller;

import com.harshbisht.UserService.dto.UserRequest;
import com.harshbisht.UserService.dto.UserResponse;
import com.harshbisht.UserService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public UserResponse createUser(@RequestBody UserRequest user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id, HttpServletRequest req) {
        // FIX: Extract HTTP concern here in the controller, not in the service.
        // HeaderAuthFilter sets this attribute after validating the internal secret + user headers.
        Long requestingUserId = (Long) req.getAttribute("userId");
        return userService.getUser(id, requestingUserId);
    }
}