package dev.dumbanov.fociumapi.service

import dev.dumbanov.fociumapi.dto.shop.ShopItemCreateInput
import dev.dumbanov.fociumapi.dto.shop.ShopItemUpdateInput
import dev.dumbanov.fociumapi.entity.ProfilePerkActivation
import dev.dumbanov.fociumapi.entity.ShopItem
import dev.dumbanov.fociumapi.entity.types.PerkType
import dev.dumbanov.fociumapi.exception.BadRequestException
import dev.dumbanov.fociumapi.repository.ProfilePerkActivationRepository
import dev.dumbanov.fociumapi.repository.ProfileRepository
import dev.dumbanov.fociumapi.repository.ShopItemRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class ShopService(
    private val shopItemRepository: ShopItemRepository,
    private val profileRepository: ProfileRepository,
    private val activationRepository: ProfilePerkActivationRepository
) {

    // ── Public catalogue ──────────────────────────────────────────────────────

    fun getShopItems(): List<ShopItem> = shopItemRepository.findAllByIsActiveTrue()

    // ── Purchase ──────────────────────────────────────────────────────────────

    @Transactional
    fun purchaseItem(itemUuid: UUID, email: String): ProfilePerkActivation {
        val profile = profileRepository.findProfileByEmail(email)
            ?: throw UsernameNotFoundException("Profile not found")

        val item = shopItemRepository.findById(itemUuid)
            .orElseThrow { BadRequestException("Shop item not found") }

        if (!item.isActive) throw BadRequestException("This item is no longer available")

        val currentPoints = profile.points ?: 0
        if (currentPoints < item.price) throw BadRequestException("Not enough points")

        // Deduct points
        profile.points = currentPoints - item.price
        profileRepository.save(profile)

        // Record activation
        val activation = ProfilePerkActivation()
        activation.profile = profile
        activation.shopItem = item
        activation.purchasedAt = LocalDateTime.now()
        activation.expiresAt = item.durationMinutes?.let {
            activation.purchasedAt.plusMinutes(it.toLong())
        }
        activation.consumed = false

        return activationRepository.save(activation)
    }

    // ── User perks ────────────────────────────────────────────────────────────

    /** Returns only non-expired, non-consumed activations. */
    fun getMyActivePerks(email: String): List<ProfilePerkActivation> {
        val profile = profileRepository.findProfileByEmail(email)
            ?: throw UsernameNotFoundException("Profile not found")
        return activationRepository.findActiveByProfile(profile, LocalDateTime.now())
    }

    /** Returns the full history (including expired/consumed). */
    fun getMyAllPerks(email: String): List<ProfilePerkActivation> {
        val profile = profileRepository.findProfileByEmail(email)
            ?: throw UsernameNotFoundException("Profile not found")
        return activationRepository.findAllByProfileOrderByPurchasedAtDesc(profile)
    }

    // ── Admin CRUD ────────────────────────────────────────────────────────────

    fun createItem(input: ShopItemCreateInput): ShopItem {
        val perkType = runCatching { PerkType.valueOf(input.perkType) }.getOrElse {
            throw BadRequestException(
                "Invalid perkType '${input.perkType}'. Must be one of: ${PerkType.entries.joinToString()}"
            )
        }
        if (input.name.isBlank()) throw BadRequestException("Name cannot be blank")
        if (input.price < 0) throw BadRequestException("Price cannot be negative")
        if (perkType == PerkType.POINTS_BOOST && input.pointsBoostPercent == null)
            throw BadRequestException("pointsBoostPercent is required for POINTS_BOOST items")

        val item = ShopItem()
        item.name = input.name.trim()
        item.description = input.description.trim()
        item.icon = input.icon.trim()
        item.perkType = perkType
        item.price = input.price
        item.durationMinutes = input.durationMinutes
        item.pointsBoostPercent = input.pointsBoostPercent
        item.isActive = input.isActive
        return shopItemRepository.save(item)
    }

    fun updateItem(uuid: UUID, input: ShopItemUpdateInput): ShopItem {
        val item = shopItemRepository.findById(uuid)
            .orElseThrow { BadRequestException("Shop item not found") }

        input.name?.let {
            if (it.isBlank()) throw BadRequestException("Name cannot be blank")
            item.name = it.trim()
        }
        input.description?.let { item.description = it.trim() }
        input.icon?.let { item.icon = it.trim() }
        input.price?.let {
            if (it < 0) throw BadRequestException("Price cannot be negative")
            item.price = it
        }
        input.durationMinutes?.let { item.durationMinutes = it }
        input.pointsBoostPercent?.let { item.pointsBoostPercent = it }
        input.isActive?.let { item.isActive = it }

        return shopItemRepository.save(item)
    }

    fun deleteItem(uuid: UUID) {
        val item = shopItemRepository.findById(uuid)
            .orElseThrow { BadRequestException("Shop item not found") }
        shopItemRepository.delete(item)
    }
}
