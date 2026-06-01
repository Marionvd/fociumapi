package dev.dumbanov.fociumapi.controller

import dev.dumbanov.fociumapi.dto.shop.PerkActivationOutput
import dev.dumbanov.fociumapi.dto.shop.ShopItemOutput
import dev.dumbanov.fociumapi.exception.BadRequestException
import dev.dumbanov.fociumapi.service.ShopService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/shop")
class ShopController(private val shopService: ShopService) {

    /** GET /api/shop — all active shop items */
    @GetMapping
    fun getShopItems(): ResponseEntity<Any> = try {
        ResponseEntity.ok(shopService.getShopItems().map { ShopItemOutput.of(it) })
    } catch (ex: Exception) {
        ResponseEntity.internalServerError().body(mapOf("error" to ex.message))
    }

    /** POST /api/shop/{uuid}/purchase — spend points to activate a perk */
    @PostMapping("/{uuid}/purchase")
    fun purchaseItem(
        @PathVariable uuid: UUID,
        @AuthenticationPrincipal email: String
    ): ResponseEntity<Any> = try {
        val activation = shopService.purchaseItem(uuid, email)
        ResponseEntity.ok(PerkActivationOutput.of(activation))
    } catch (ex: UsernameNotFoundException) {
        ResponseEntity.status(404).body(mapOf("error" to ex.message))
    } catch (ex: BadRequestException) {
        ResponseEntity.badRequest().body(mapOf("error" to ex.message))
    } catch (ex: Exception) {
        ResponseEntity.internalServerError().body(mapOf("error" to ex.message))
    }

    /** GET /api/shop/my-perks — caller's active (non-expired) perk activations */
    @GetMapping("/my-perks")
    fun getMyPerks(@AuthenticationPrincipal email: String): ResponseEntity<Any> = try {
        ResponseEntity.ok(shopService.getMyActivePerks(email).map { PerkActivationOutput.of(it) })
    } catch (ex: UsernameNotFoundException) {
        ResponseEntity.status(404).body(mapOf("error" to ex.message))
    } catch (ex: Exception) {
        ResponseEntity.internalServerError().body(mapOf("error" to ex.message))
    }

    /** GET /api/shop/my-perks/history — full purchase history */
    @GetMapping("/my-perks/history")
    fun getPerkHistory(@AuthenticationPrincipal email: String): ResponseEntity<Any> = try {
        ResponseEntity.ok(shopService.getMyAllPerks(email).map { PerkActivationOutput.of(it) })
    } catch (ex: UsernameNotFoundException) {
        ResponseEntity.status(404).body(mapOf("error" to ex.message))
    } catch (ex: Exception) {
        ResponseEntity.internalServerError().body(mapOf("error" to ex.message))
    }
}
