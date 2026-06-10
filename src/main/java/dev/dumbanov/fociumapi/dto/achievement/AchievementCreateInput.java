package dev.dumbanov.fociumapi.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AchievementCreateInput(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        Integer rewardPoints,
        List<PrerequisiteInput> prerequisites
) {
}

