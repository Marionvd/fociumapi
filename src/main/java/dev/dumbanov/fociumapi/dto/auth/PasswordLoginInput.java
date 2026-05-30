package dev.dumbanov.fociumapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordLoginInput(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
