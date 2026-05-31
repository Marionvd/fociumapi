package dev.dumbanov.fociumapi.dto.showdown;

import java.util.UUID;

public record ShowdownCreateInput(
        UUID profile2Uuid,
        int durationMinutes
) {
}

