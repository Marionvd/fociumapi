package dev.dumbanov.fociumapi.entity

import dev.dumbanov.fociumapi.entity.types.PerkType
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "shop_item")
class ShopItem {

    @Id
    @Column(name="uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var uuid: UUID

    @Column(nullable = false)
    var name: String = ""

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String = ""

    /** Emoji or icon identifier shown in the UI. */
    @Column(nullable = false)
    var icon: String = ""

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    var perkType: PerkType = PerkType.POINTS_BOOST

    @Column(nullable = false)
    var price: Int = 0

    /**
     * How long the perk stays active after purchase, in minutes.
     * Null means the perk is permanent / one-time-use (e.g. STREAK_SHIELD).
     */
    var durationMinutes: Int? = null

    /**
     * Extra points percentage for POINTS_BOOST perks (e.g. 50 = +50%).
     * Null for other perk types.
     */
    var pointsBoostPercent: Int? = null

    /** Admins can toggle this to hide an item from the shop without deleting it. */
    @Column(nullable = false)
    var isActive: Boolean = true
}
