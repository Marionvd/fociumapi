package dev.dumbanov.fociumapi.repository

import dev.dumbanov.fociumapi.entity.ProfileActivationTicket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProfileActivationTicketRepository : JpaRepository<ProfileActivationTicket, UUID> {
}