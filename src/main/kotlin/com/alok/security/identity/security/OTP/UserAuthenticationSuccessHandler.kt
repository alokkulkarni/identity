package com.alok.security.identity.security.OTP

import com.alok.security.identity.models.userModels.TokenUserDetails
import com.alok.security.identity.repository.UserIdentityRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
open class UserAuthenticationSuccessHandler(private val userIdentityRepository: UserIdentityRepository ) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        val tokenUserDetails = authentication?.principal as TokenUserDetails
        userIdentityRepository.findByUsername(tokenUserDetails.username)?.let {
            it.lastloggedin = LocalDateTime.now()
            userIdentityRepository.save(it)
        }
    }
}