package dev.dumbanov.fociumapi.dto.achievement;

import java.util.List;

public record AchievementUpdateInput(
        String name,
        String description,
        Integer rewardPoints,
        List<PrerequisiteInput> prerequisites
) {
}

