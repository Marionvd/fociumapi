package dev.dumbanov.fociumapi.dto.showdown;

import dev.dumbanov.fociumapi.entity.Showdown;
import dev.dumbanov.fociumapi.entity.types.ShowdownStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShowdownOutput(
        UUID uuid,
        UUID profile1Uuid,
        UUID profile2Uuid,
        Integer profile1Points,
        Integer profile2Points,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ShowdownStatus status,
        UUID winnerUuid
) {
    public static ShowdownOutput of(Showdown s) {
        return new ShowdownOutput(
                s.getUuid(),
                s.getProfile1().getUuid(),
                s.getProfile2().getUuid(),
                s.getProfile1Points(),
                s.getProfile2Points(),
                s.getStartTime(),
                s.getEndTime(),
                s.getStatus(),
                s.getWinner() != null ? s.getWinner().getUuid() : null
        );
    }
}

