package dev.dumbanov.fociumapi.configuration

import com.cloudinary.Cloudinary
import lombok.Getter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Getter
class CloudinaryConfiguration (
    @Value("\${cloudinary.cloud-name}") private var cloudName: String,
    @Value("\${cloudinary.api-key}") private val apiKey: String,
    @Value("\${cloudinary.api-secret}") private val apiSecret: String,
){
    @Bean
    fun cloudinary(): Cloudinary {
        val config = mapOf(
            "cloud_name" to cloudName,
            "api_key" to apiKey,
            "api_secret" to apiSecret
        )

        return Cloudinary(config)
    }
}