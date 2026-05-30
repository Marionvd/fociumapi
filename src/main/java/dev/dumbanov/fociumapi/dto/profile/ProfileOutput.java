package dev.dumbanov.fociumapi.dto.profile;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileOutput(
        UUID uuid,
        String email,
        LocalDateTime createdAt,
        LocalDateTime lastLogin,
        String[] roles
) {
    public static ProfileOutput of(Profile profile) {
        return new ProfileOutput(
                profile.getUuid(),
                profile.getEmail(),
                profile.getCreatedAt(),
                profile.getLastLogin(),
                profile.getRoles().stream().map(Role::getName).toArray(String[]::new)
        );
    }
}
