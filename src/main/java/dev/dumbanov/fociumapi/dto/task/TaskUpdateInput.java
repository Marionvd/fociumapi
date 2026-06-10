package dev.dumbanov.fociumapi.dto.task;

import java.time.LocalDateTime;

public record TaskUpdateInput(
        String title,
        String description,
        LocalDateTime deadline,
        Integer awardedPoints,
        String aiFeedback
) {
}

