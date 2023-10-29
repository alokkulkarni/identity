@file:Suppress("unused")

package com.alok.security.identity.security.webauthn

import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFinishRequest
import org.springframework.security.authentication.AbstractAuthenticationToken

class WebAuthNAuthentication(
    val username: String,
    private val loginFinishRequest: WebAuthNLoginFinishRequest,
    val assertionResultJson: String
) : AbstractAuthenticationToken(mutableSetOf())
{

    override fun isAuthenticated(): Boolean {
        return true
    }
    override fun getCredentials(): Any {
        return loginFinishRequest
    }

    override fun getPrincipal(): Any {
        return username
    }
}