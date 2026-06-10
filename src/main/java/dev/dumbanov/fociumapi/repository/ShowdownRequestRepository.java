package dev.dumbanov.fociumapi.repository;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.ShowdownRequest;
import dev.dumbanov.fociumapi.entity.types.ShowdownRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShowdownRequestRepository extends JpaRepository<ShowdownRequest, UUID> {
    List<ShowdownRequest> findByReceiverOrderByCreatedAtDesc(Profile receiver);
    List<ShowdownRequest> findBySenderOrderByCreatedAtDesc(Profile sender);
    List<ShowdownRequest> findByReceiverAndStatusOrderByCreatedAtDesc(Profile receiver, ShowdownRequestStatus status);
}
