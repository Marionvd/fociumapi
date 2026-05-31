package dev.dumbanov.fociumapi.dto.task;

import dev.dumbanov.fociumapi.entity.Task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskOutput(
        UUID uuid,
        String title,
        String description,
        LocalDateTime deadline,
        Integer awardedPoints,
        String aiFeedback,
        UUID profileUuid
) {
    public static TaskOutput of(Task task) {
        return new TaskOutput(
                task.getUuid(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getAwardedPoints(),
                task.getAiFeedback(),
                task.getProfile().getUuid()
        );
    }
}

