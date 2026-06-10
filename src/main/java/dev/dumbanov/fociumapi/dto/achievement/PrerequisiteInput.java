package dev.dumbanov.fociumapi.dto.achievement;

import jakarta.validation.constraints.NotNull;

public record PrerequisiteInput(
        @NotNull
        String type,
        @NotNull
        Integer requiredAmount
) {
}

