package com.mcesnik.backend.DTO;

import com.mcesnik.backend.model.User;

public record UserResponse(
    Long id,
    String username,
    String email,
    boolean enabled
) {
    public UserResponse(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.isEnabled()
        );
    }
}