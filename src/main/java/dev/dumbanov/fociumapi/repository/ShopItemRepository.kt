package dev.dumbanov.fociumapi.repository

import dev.dumbanov.fociumapi.entity.ShopItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ShopItemRepository : JpaRepository<ShopItem, UUID> {
    fun findAllByIsActiveTrue(): List<ShopItem>
    fun existsByName(name: String): Boolean
}
