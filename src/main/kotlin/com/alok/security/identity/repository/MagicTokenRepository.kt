package com.alok.security.identity.repository

import com.alok.security.identity.models.magictokens.MagicToken
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface MagicTokenRepository: JpaRepository<MagicToken, Long> {

    fun findByToken(token: String): MagicToken?

    fun findByUsername(username: String): MagicToken?

    @Modifying
    @Transactional
    @Query("DELETE FROM magic_tokens t WHERE t.created < ?1")
    fun deleteExpiredTokens(expiryThreshold: Instant)

}