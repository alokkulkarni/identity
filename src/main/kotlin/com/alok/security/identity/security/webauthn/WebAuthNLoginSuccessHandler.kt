@file:Suppress("unused")

package com.alok.security.identity.security.webauthn

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException


class WebAuthNLoginSuccessHandler : AuthenticationSuccessHandler {

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        this.onAuthenticationSuccess(request, response, authentication)
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val webAuthNAuthenticationToken: WebAuthNAuthentication = authentication as WebAuthNAuthentication
        response.contentType = "application/json"
        response.status = 200
        response.writer.println(webAuthNAuthenticationToken.assertionResultJson)
    }

}