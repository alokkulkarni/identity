@file:Suppress("unused")

package com.alok.security.identity.security.webauthn

import com.alok.security.identity.service.webauthnServices.WebAuthNLoginService
import com.yubico.webauthn.AssertionResult
import com.yubico.webauthn.exception.AssertionFailedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException


class WebAuthNAuthenticationManager(private val webAuthNLoginService: WebAuthNLoginService) : AuthenticationManager {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val fidoToken: WebAuthNAuthenticationToken = authentication as WebAuthNAuthenticationToken
        try {
            val assertionResult: AssertionResult = webAuthNLoginService.finishLogin(fidoToken.loginFinishRequest)
            if (assertionResult.isSuccess) {
                return WebAuthNAuthentication(assertionResult.toString(), fidoToken.loginFinishRequest, assertionResult.toString())
            }
            throw BadCredentialsException("WebAuthn failed")
        } catch (e: AssertionFailedException) {
            throw BadCredentialsException("unable to login", e)
        }
    }
}