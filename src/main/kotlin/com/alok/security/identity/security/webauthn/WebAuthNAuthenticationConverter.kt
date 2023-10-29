@file:Suppress("unused")

package com.alok.security.identity.security.webauthn

import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFinishRequest
import com.alok.security.identity.utils.JsonUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.AuthenticationConverter


class WebAuthNAuthenticationConverter : AuthenticationConverter {
    override fun convert(request: HttpServletRequest): Authentication {
        val username = request.getParameter("username")
        if (username == null || username.isBlank()) {
            throw UsernameNotFoundException("login request does not contain a username")
        }
        val finishRequest = request.getParameter("finishRequest")
        if (finishRequest == null || finishRequest.isBlank()) {
            throw BadCredentialsException("fido credentials missing")
        }
        val loginFinishRequest: WebAuthNLoginFinishRequest = JsonUtils.fromJson(finishRequest, WebAuthNLoginFinishRequest::class.java)
        return WebAuthNAuthenticationToken(username, loginFinishRequest)
    }
}