package dev.dumbanov.fociumapi.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import dev.dumbanov.fociumapi.entity.ShopItem
import dev.dumbanov.fociumapi.entity.types.PerkType
import dev.dumbanov.fociumapi.repository.ShopItemRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.core.io.ClassPathResource

@Configuration
class ShopItemSeeder(
    private val shopItemRepository: ShopItemRepository
) : ApplicationListener<ContextRefreshedEvent> {

    private val log = LoggerFactory.getLogger(ShopItemSeeder::class.java)
    private val mapper = ObjectMapper()

    private data class Seed(
        val name: String,
        val description: String,
        val icon: String,
        val perkType: String,
        val price: Int,
        val durationMinutes: Int?,
        val pointsBoostPercent: Int?,
        val isActive: Boolean = true
    )

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val resource = ClassPathResource("seed/shop_items.json")
        if (!resource.exists()) {
            log.info("No shop_items.json seed file found — skipping")
            return
        }

        try {
            val seeds: List<Seed> = mapper.readValue(
                resource.inputStream,
                mapper.typeFactory.constructCollectionType(List::class.java, Seed::class.java)
            )

            for (seed in seeds) {
                if (shopItemRepository.existsByName(seed.name)) {
                    log.debug("Shop item already seeded: {}", seed.name)
                    continue
                }
                val item = ShopItem().also {
                    it.name = seed.name
                    it.description = seed.description
                    it.icon = seed.icon
                    it.perkType = PerkType.valueOf(seed.perkType)
                    it.price = seed.price
                    it.durationMinutes = seed.durationMinutes
                    it.pointsBoostPercent = seed.pointsBoostPercent
                    it.isActive = seed.isActive
                }
                shopItemRepository.save(item)
                log.info("Seeded shop item: {}", seed.name)
            }
        } catch (e: Exception) {
            log.warn("Failed to seed shop items: {}", e.message)
        }
    }
}
