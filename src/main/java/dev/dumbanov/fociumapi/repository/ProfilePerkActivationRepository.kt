package dev.dumbanov.fociumapi.repository

import dev.dumbanov.fociumapi.entity.Profile
import dev.dumbanov.fociumapi.entity.ProfilePerkActivation
import dev.dumbanov.fociumapi.entity.types.PerkType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface ProfilePerkActivationRepository : JpaRepository<ProfilePerkActivation, UUID> {

    /** All activations for a profile, newest first. */
    fun findAllByProfileOrderByPurchasedAtDesc(profile: Profile): List<ProfilePerkActivation>

    /**
     * Active (non-consumed, non-expired) activations for a profile,
     * optionally filtered by perk type.
     */
    @Query("""
        SELECT a FROM ProfilePerkActivation a
        WHERE a.profile = :profile
          AND a.consumed = false
          AND (a.expiresAt IS NULL OR a.expiresAt > :now)
    """)
    fun findActiveByProfile(
        @Param("profile") profile: Profile,
        @Param("now") now: LocalDateTime
    ): List<ProfilePerkActivation>

    @Query("""
        SELECT a FROM ProfilePerkActivation a
        WHERE a.profile = :profile
          AND a.shopItem.perkType = :perkType
          AND a.consumed = false
          AND (a.expiresAt IS NULL OR a.expiresAt > :now)
    """)
    fun findActiveByProfileAndPerkType(
        @Param("profile") profile: Profile,
        @Param("perkType") perkType: PerkType,
        @Param("now") now: LocalDateTime
    ): List<ProfilePerkActivation>
}
