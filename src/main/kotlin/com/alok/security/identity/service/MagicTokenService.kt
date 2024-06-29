package com.alok.security.identity.service

import com.alok.security.identity.models.magictokens.MagicToken
import com.alok.security.identity.repository.MagicTokenRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Instant.*
/*
* */
@Service
class MagicTokenService(private val userService: UserService, private val magicTokenRepository: MagicTokenRepository) {

    private val secureRandom = SecureRandom()
    private val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    private val tokenLength = 128
    private val strategy = SecurityContextHolder.getContextHolderStrategy()
    private val repo = HttpSessionSecurityContextRepository()

    companion object {
        private val log = LoggerFactory.getLogger(MagicTokenService::class.java)
    }

    fun authenticateTokens(token: String, request: HttpServletRequest, response: HttpServletResponse) {
        val magicToken = magicTokenRepository.findByToken(token)

        if (magicToken != null) {
            val user = userService.loadUserByUsername(magicToken.username)
            val auth = UsernamePasswordAuthenticationToken(user, user.password, user.authorities)
            strategy.createEmptyContext()
            strategy.context?.authentication = auth
            repo.saveContext(strategy.context!!, request, response)
            magicToken.id?.let { magicTokenRepository.deleteById(it) }
        }
    }

    fun issueMagicToken(username: String) {
        val token = generateToken()
        val magicToken = magicTokenRepository.save(MagicToken().apply {
            this.token = token
            this.username = username
            this.created = now()
        })
        mail(magicToken)
    }

    private fun generateToken(): String {
        val sb = StringBuilder()
        for (i in 0 until tokenLength) {
            sb.append(alphabet[secureRandom.nextInt(alphabet.length)])
        }
        return sb.toString()
    }

    private fun mail(token: MagicToken) {
        log.info("${token.username} has magic link http://localhost:8080/auth/${token.token}")
    }
}