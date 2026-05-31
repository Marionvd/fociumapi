package dev.dumbanov.fociumapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestInput(
        @Email
        @NotBlank
        String email
) {
}
