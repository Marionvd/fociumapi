package dev.dumbanov.fociumapi.dto.shop

import dev.dumbanov.fociumapi.entity.ProfilePerkActivation
import java.time.LocalDateTime
import java.util.UUID

data class PerkActivationOutput(
    val uuid: UUID?,
    val shopItem: ShopItemOutput,
    val purchasedAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val consumed: Boolean,
    val active: Boolean
) {
    companion object {
        fun of(a: ProfilePerkActivation) = PerkActivationOutput(
            uuid = a.uuid,
            shopItem = ShopItemOutput.of(a.shopItem),
            purchasedAt = a.purchasedAt,
            expiresAt = a.expiresAt,
            consumed = a.consumed,
            active = a.isActive
        )
    }
}
