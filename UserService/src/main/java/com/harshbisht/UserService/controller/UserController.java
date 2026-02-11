package com.harshbisht.UserService.controller;

import com.harshbisht.UserService.entity.UserEntity;
import com.harshbisht.UserService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserEntity createUser(@RequestBody UserEntity user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public UserEntity getUser(@PathVariable Long id, HttpServletRequest req) {
        return userService.getUser(id, req);
    }
}
