package dev.dumbanov.fociumapi.dto.shop

/** All fields are optional — only provided fields are updated. */
data class ShopItemUpdateInput(
    val name: String? = null,
    val description: String? = null,
    val icon: String? = null,
    val price: Int? = null,
    val durationMinutes: Int? = null,
    val pointsBoostPercent: Int? = null,
    val isActive: Boolean? = null
)
