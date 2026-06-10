package dev.dumbanov.fociumapi.service

import dev.dumbanov.fociumapi.entity.ProfileActivationTicket
import dev.dumbanov.fociumapi.exception.BadRequestException
import dev.dumbanov.fociumapi.repository.ProfileActivationTicketRepository
import dev.dumbanov.fociumapi.repository.ProfileRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ProfileActivationTicketService(
    private val profileActivationTicketRepository: ProfileActivationTicketRepository,
    private val profileRepository: ProfileRepository,
    private val emailService: EmailService
) {
    fun createTicket(email: String): ProfileActivationTicket {
        if (!profileRepository.existsProfileByEmail(email)) {
            throw UsernameNotFoundException("User with email $email does not exist")
        }
        val profile = profileRepository.findProfileByEmail(email)

        val ticket = ProfileActivationTicket()
        ticket.profile = profile
        val saved = profileActivationTicketRepository.save(ticket)

        emailService.sendProfileActivationEmail(email, saved.uuid)

        return saved
    }

    fun activate(uuid: UUID) {
        if (!profileActivationTicketRepository.existsById(uuid)) {
            throw UsernameNotFoundException("Ticket with uuid $uuid does not exist")
        }
        val ticket = profileActivationTicketRepository.findById(uuid).get()

        if (ticket.expiresAt.isBefore(LocalDateTime.now())) {
            profileActivationTicketRepository.delete(ticket)
            throw BadRequestException("Activation link has expired. Please request a new one.")
        }

        val profile = ticket.profile
        if (!profile.active) {
            profile.active = true
            profileRepository.save(profile)
        }

        profileActivationTicketRepository.delete(ticket)
    }
}
