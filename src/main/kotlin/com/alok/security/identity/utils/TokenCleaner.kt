package com.alok.security.identity.utils

import com.alok.security.identity.repository.MagicTokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit.*

@Component
class TokenCleaner(private val magicTokenRepository: MagicTokenRepository)  {

    @Scheduled(cron = "0 */1 * * * ?")
    fun cleanExpiredTokens() {
        val expiryThreshold = Instant.now().minus(5, MINUTES)
        magicTokenRepository.deleteExpiredTokens(expiryThreshold)
    }
}