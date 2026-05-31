package dev.dumbanov.fociumapi.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetInput(
        @NotBlank
        String newPassword
) {
}
