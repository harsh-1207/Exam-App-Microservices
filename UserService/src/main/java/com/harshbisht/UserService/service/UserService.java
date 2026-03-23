package com.harshbisht.UserService.service;

import com.harshbisht.UserService.dto.UserRequest;
import com.harshbisht.UserService.dto.UserResponse;
import com.harshbisht.UserService.entity.UserEntity;
import com.harshbisht.UserService.exception.UnauthorizedAccessException;
import com.harshbisht.UserService.exception.UserProfileNotFoundException;
import com.harshbisht.UserService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;

    public UserResponse createUser(UserRequest request) {
        UserEntity user = new UserEntity();
        user.setName(request.getName());
        UserEntity saved = repo.save(user);
        return toResponse(saved);
    }

    /**
     * FIX: No longer accepts HttpServletRequest — the service layer should be
     * HTTP-agnostic. The controller extracts what's needed and passes it in.
     *
     * @param requestedId   the profile ID being requested
     * @param requestingUserId the userId from the JWT/header (null for ROLE_SERVICE calls)
     */
    public UserResponse getUser(Long requestedId, Long requestingUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // FIX: requestingUserId was always null before because HeaderAuthFilter
        // never set the "userId" attribute. Now it does (see HeaderAuthFilter fix).
        // Admins bypass the ownership check entirely.
        if (!isAdmin && !requestedId.equals(requestingUserId)) {
            throw new UnauthorizedAccessException("Access denied: you can only view your own profile");
        }

        return repo.findById(requestedId)
                .map(this::toResponse)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "User not found with id: " + requestedId));
    }

    private UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}