package dev.dumbanov.fociumapi.dto.task;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TaskCreateInput(
        @NotBlank
        String title,
        String description,
        LocalDateTime deadline
) {
}

