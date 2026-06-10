package dev.dumbanov.fociumapi.dto.shop

import dev.dumbanov.fociumapi.entity.ShopItem
import java.util.UUID

data class ShopItemOutput(
    val uuid: UUID?,
    val name: String,
    val description: String,
    val icon: String,
    val perkType: String,
    val price: Int,
    val durationMinutes: Int?,
    val pointsBoostPercent: Int?,
    val isActive: Boolean
) {
    companion object {
        fun of(item: ShopItem) = ShopItemOutput(
            uuid = item.uuid,
            name = item.name,
            description = item.description,
            icon = item.icon,
            perkType = item.perkType.name,
            price = item.price,
            durationMinutes = item.durationMinutes,
            pointsBoostPercent = item.pointsBoostPercent,
            isActive = item.isActive
        )
    }
}
