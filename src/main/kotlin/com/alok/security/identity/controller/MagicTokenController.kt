package com.alok.security.identity.controller

import com.alok.security.identity.service.MagicTokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class MagicTokenController(private val magicTokenService: MagicTokenService) {

    companion object {
        val log = org.slf4j.LoggerFactory.getLogger(MagicTokenController::class.java)
    }

    @GetMapping("/me")
    fun me(principal: Principal): String {
        return "Hello, ${principal.name}!"
    }

    @Async
    @PostMapping("/auth")
    fun login(@RequestBody request: AuthRequest) {
        try {
            magicTokenService.issueMagicToken(request.username)
        } catch (cause: Exception) {
            log.error("Failed to issue token for ${request.username}")
            log.error(cause.message)
        }
    }

    @GetMapping("/auth/{token}")
    fun authenticate(@PathVariable token: String, request: HttpServletRequest, response: HttpServletResponse){
        magicTokenService.authenticateTokens(token, request, response)
    }
}

data class AuthRequest(val username: String)