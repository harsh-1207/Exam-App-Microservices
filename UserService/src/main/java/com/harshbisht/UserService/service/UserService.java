package com.harshbisht.UserService.service;

import com.harshbisht.UserService.entity.UserEntity;
import com.harshbisht.UserService.exception.AccessDeniedException;
import com.harshbisht.UserService.exception.UserProfileNotFoundException;
import com.harshbisht.UserService.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public UserEntity createUser(UserEntity user) {
        return repo.save(user);
    }

    public UserEntity getUser(Long id, HttpServletRequest req) {
        Long tokenUserId = (Long) req.getAttribute("userId");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!id.equals(tokenUserId) && !isAdmin) {
            throw new AccessDeniedException("Access denied");
        }

        return repo.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("User not found"));
    }
}

