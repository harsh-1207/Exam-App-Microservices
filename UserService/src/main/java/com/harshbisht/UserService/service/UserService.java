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

        user.setName(request.getName().trim());

        UserEntity saved = repo.save(user);

        return toResponse(saved);
    }

    public UserResponse getUser(
            Long requestedId,
            Long requestingUserId
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new UnauthorizedAccessException(
                    "Authentication required"
            );
        }

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN")
                        );

        /*
         * ADMIN can view any profile.
         *
         * STUDENT/TEACHER can only view their own profile.
         *
         * If requestingUserId is null, access is denied rather than
         * accidentally allowing access.
         */
        if (!isAdmin) {

            if (requestingUserId == null ||
                    !requestedId.equals(requestingUserId)) {

                throw new UnauthorizedAccessException(
                        "Access denied: you can only view your own profile"
                );
            }
        }

        return repo.findById(requestedId)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new UserProfileNotFoundException(
                                "User not found with id: " + requestedId
                        )
                );
    }

    private UserResponse toResponse(UserEntity entity) {

        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}