package dev.dumbanov.fociumapi.dto.shop

/**
 * Input for creating a new shop item (admin only).
 * [perkType] must match a [dev.dumbanov.fociumapi.entity.types.PerkType] constant:
 * APPLICATION_ACCESS, POINTS_BOOST, STREAK_SHIELD.
 */
data class ShopItemCreateInput(
    val name: String,
    val description: String,
    val icon: String,
    val perkType: String,
    val price: Int,
    /** Duration in minutes. Null for one-time-use items (STREAK_SHIELD). */
    val durationMinutes: Int? = null,
    /** Required for POINTS_BOOST; ignored for other types. */
    val pointsBoostPercent: Int? = null,
    val isActive: Boolean = true
)
