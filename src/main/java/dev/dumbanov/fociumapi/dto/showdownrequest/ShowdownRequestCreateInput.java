package dev.dumbanov.fociumapi.dto.showdownrequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShowdownRequestCreateInput(
        @NotNull UUID receiverUuid,
        @Min(1) int durationMinutes
) {}
