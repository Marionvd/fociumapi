package dev.dumbanov.fociumapi.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

/**
 * Records a single perk purchase by a user.
 * Each row is one activation — the same item can be bought multiple times.
 */
@Entity
@Table(name = "profile_perk_activation")
class ProfilePerkActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var uuid: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_uuid", nullable = false)
    lateinit var profile: Profile

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_item_uuid", nullable = false)
    lateinit var shopItem: ShopItem

    @Column(nullable = false)
    var purchasedAt: LocalDateTime = LocalDateTime.now()

    /**
     * When this activation expires.
     * Null for one-time-use perks (STREAK_SHIELD) — expiry is controlled by [consumed].
     */
    var expiresAt: LocalDateTime? = null

    /**
     * For one-time-use perks (STREAK_SHIELD): true once the perk has been spent.
     */
    @Column(nullable = false)
    var consumed: Boolean = false

    /** Computed convenience — not persisted. */
    @get:Transient
    val isActive: Boolean
        get() = !consumed && (expiresAt == null || expiresAt!!.isAfter(LocalDateTime.now()))
}
