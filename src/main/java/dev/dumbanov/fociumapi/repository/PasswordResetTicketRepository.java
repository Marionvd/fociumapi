package dev.dumbanov.fociumapi.repository;

import dev.dumbanov.fociumapi.entity.PasswordResetTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordResetTicketRepository extends JpaRepository<PasswordResetTicket, UUID> {
    PasswordResetTicket findPasswordResetTicketByUuid(UUID uuid);
}
