package dev.dumbanov.fociumapi.service

import dev.dumbanov.fociumapi.entity.Profile
import dev.dumbanov.fociumapi.entity.types.PerkType
import dev.dumbanov.fociumapi.repository.ProfilePerkActivationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PerkService(
    private val activationRepo: ProfilePerkActivationRepository
) {

    /**
     * Returns the sum of all active POINTS_BOOST percentages for a profile.
     * E.g. two boosts of 50% and 100% → 150. Returns 0 when no boost is active.
     */
    fun getTotalPointsBoostPercent(profile: Profile): Int =
        activationRepo
            .findActiveByProfileAndPerkType(profile, PerkType.POINTS_BOOST, LocalDateTime.now())
            .sumOf { it.shopItem.pointsBoostPercent ?: 0 }

    /**
     * Returns true if the profile has at least one active APPLICATION_ACCESS perk.
     */
    fun hasMobileAccess(profile: Profile): Boolean =
        activationRepo
            .findActiveByProfileAndPerkType(profile, PerkType.APPLICATION_ACCESS, LocalDateTime.now())
            .isNotEmpty()

    /**
     * Consumes the first available STREAK_SHIELD for the profile.
     * @return true if a shield was available and is now consumed; false otherwise.
     */
    @Transactional
    fun consumeStreakShield(profile: Profile): Boolean {
        val shield = activationRepo
            .findActiveByProfileAndPerkType(profile, PerkType.STREAK_SHIELD, LocalDateTime.now())
            .firstOrNull() ?: return false

        shield.consumed = true
        activationRepo.save(shield)
        return true
    }
}
