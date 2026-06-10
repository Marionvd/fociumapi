package dev.dumbanov.fociumapi.dto.achievement;

import dev.dumbanov.fociumapi.entity.Achievement;
import dev.dumbanov.fociumapi.entity.AchievementPrerequisite;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record AchievementOutput(
        UUID uuid,
        String name,
        String description,
        Integer rewardPoints,
        List<PrerequisiteInput> prerequisites
) {
    public static AchievementOutput of(Achievement a) {
        List<PrerequisiteInput> prs = a.getPrerequisites() == null ? List.of() : a.getPrerequisites().stream()
                .map(p -> new PrerequisiteInput(p.getType().name(), p.getRequiredAmount()))
                .collect(Collectors.toList());
        return new AchievementOutput(a.getUuid(), a.getName(), a.getDescription(), a.getRewardPoints(), prs);
    }
}

