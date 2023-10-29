@file:Suppress("unused")

package com.alok.security.identity.security.webauthn

import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFinishRequest
import org.springframework.security.authentication.AbstractAuthenticationToken

class WebAuthNAuthenticationToken (
    val username: String,
    val loginFinishRequest: WebAuthNLoginFinishRequest
): AbstractAuthenticationToken(mutableSetOf()) {
    override fun getCredentials(): Any {
        return loginFinishRequest
    }

    override fun getPrincipal(): Any {
        return username
    }
}