package com.alok.security.identity.security.OTP

import com.alok.security.identity.models.userModels.TokenUserDetails
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component


@Component
class OneTimeAuthProvider(private val userService: UserDetailsService) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        val auth = authentication as OneTimePasswordAuthentication
        val userDetails = userService.loadUserByUsername(/* username = */ auth.name) as TokenUserDetails
        val device = userDetails.getDevice() ?: throw IllegalStateException("No device attached")
        val token = auth.credentials as String
        if (userDetails.requiresMfa() && device.accepts(token)) {
            return auth.initial
        }
        throw BadCredentialsException("Invalid token")
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication == OneTimePasswordAuthentication::class.java
    }
}