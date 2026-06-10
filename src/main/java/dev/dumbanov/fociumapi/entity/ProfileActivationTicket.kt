package dev.dumbanov.fociumapi.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table
class ProfileActivationTicket {
    @Id
    @GeneratedValue
    lateinit var uuid: UUID

    var createdAt: LocalDateTime = LocalDateTime.now()

    var expiresAt: LocalDateTime = LocalDateTime.now().plusHours(24)

    @ManyToOne
    lateinit var profile: Profile
}