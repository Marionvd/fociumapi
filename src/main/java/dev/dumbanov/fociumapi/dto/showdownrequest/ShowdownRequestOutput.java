package dev.dumbanov.fociumapi.dto.showdownrequest;

import dev.dumbanov.fociumapi.entity.ShowdownRequest;
import dev.dumbanov.fociumapi.entity.types.ShowdownRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShowdownRequestOutput(
        UUID uuid,
        UUID senderUuid,
        UUID receiverUuid,
        int durationMinutes,
        ShowdownRequestStatus status,
        LocalDateTime createdAt
) {
    public static ShowdownRequestOutput of(ShowdownRequest r) {
        return new ShowdownRequestOutput(
                r.getUuid(),
                r.getSender().getUuid(),
                r.getReceiver().getUuid(),
                r.getDurationMinutes(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}
