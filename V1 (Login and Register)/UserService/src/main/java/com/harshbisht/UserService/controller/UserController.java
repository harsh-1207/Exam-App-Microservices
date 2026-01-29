package com.harshbisht.UserService.controller;

import com.harshbisht.UserService.entity.UserEntity;
import com.harshbisht.UserService.repository.UserRepository;
import com.harshbisht.UserService.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

//@RestController
//@RequestMapping("/users")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserRepository repo;
//
//    // Called by AUTH SERVICE
//    // Protected by InternalAuthFilter
//    @PostMapping
//    public UserEntity createUser(@RequestBody UserEntity user) {
//        return repo.save(user);
//    }
//
//    // Users can only read their own data (unless ADMIN)
//    @GetMapping("/{id}")
//    public UserEntity getUser(@PathVariable Long id, HttpServletRequest req) {
//
//        Long tokenUserId = (Long) req.getAttribute("userId");
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        boolean isAdmin = auth.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//
//        // if id matches token’s userId
//        if (!id.equals(tokenUserId) && !isAdmin) {
//            throw new RuntimeException("Access denied");
//        }
//
//        return repo.findById(id).orElseThrow();
//    }
//
//
//}
