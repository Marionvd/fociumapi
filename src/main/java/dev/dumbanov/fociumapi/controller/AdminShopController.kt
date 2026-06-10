package dev.dumbanov.fociumapi.controller

import dev.dumbanov.fociumapi.dto.shop.ShopItemCreateInput
import dev.dumbanov.fociumapi.dto.shop.ShopItemOutput
import dev.dumbanov.fociumapi.dto.shop.ShopItemUpdateInput
import dev.dumbanov.fociumapi.exception.BadRequestException
import dev.dumbanov.fociumapi.service.ShopService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/admin/shop")
class AdminShopController(private val shopService: ShopService) {

    /** POST /api/admin/shop — create a new shop item */
    @PostMapping
    fun createItem(@RequestBody input: ShopItemCreateInput): ResponseEntity<Any> = try {
        val item = shopService.createItem(input)
        ResponseEntity
            .created(URI.create("/api/admin/shop/${item.uuid}"))
            .body(ShopItemOutput.of(item))
    } catch (ex: BadRequestException) {
        ResponseEntity.badRequest().body(mapOf("error" to ex.message))
    } catch (ex: Exception) {
        ResponseEntity.internalServerError().body(mapOf("error" to ex.message))
    }

    /** PUT /api/admin/shop/{uuid} — update an existing item */
    @PutMapping("/{uuid}")
    fun updateItem(
        @PathVariable uuid: UUID,
        @RequestBody input: ShopItemUpdateInput
    ): ResponseEntity<Any> = try {
        ResponseEntity.ok(ShopItemOutput.of(shopService.updateItem(uuid, input)))
    } catch (ex: BadRequestException) {
        ResponseEntity.badRequest().body(mapOf("error" to ex.message))
    } catch (ex: Exception) {
        ResponseEntity.internalServerError().body(mapOf("error" to ex.message))
    }

    /** DELETE /api/admin/shop/{uuid} — remove an item */
    @DeleteMapping("/{uuid}")
    fun deleteItem(@PathVariable uuid: UUID): ResponseEntity<Any> = try {
        shopService.deleteItem(uuid)
        ResponseEntity.ok(mapOf("message" to "Shop item deleted"))
    } catch (ex: BadRequestException) {
        ResponseEntity.badRequest().body(mapOf("error" to ex.message))
    } catch (ex: Exception) {
        ResponseEntity.internalServerError().body(mapOf("error" to ex.message))
    }
}
