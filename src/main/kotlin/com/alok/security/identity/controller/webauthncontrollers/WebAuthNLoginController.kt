@file:Suppress("UNUSED_VARIABLE", "USELESS_ELVIS", "MayBeConstant", "unused")

package com.alok.security.identity.controller.webauthncontrollers

import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFinishRequest
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginStartRequest
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginStartResponse
import com.alok.security.identity.service.webauthnServices.WebAuthNLoginService
import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.AssertionResult
import com.yubico.webauthn.exception.AssertionFailedException
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*


@RestController
class WebAuthNLoginController(val webAuthNLoginService: WebAuthNLoginService) {

    companion object{
        private val logger: Logger = LoggerFactory.getLogger(WebAuthNLoginController::class.java)
        private val START_LOGIN_REQUEST = "start_login_request"
        private val FINISH_LOGIN_REQUEST = "finish_login_request"
    }

    @GetMapping("/webauthn/login")
    fun get(): String {
        return "fido-login"
    }

    @ResponseBody
    @PostMapping("/webauthn/login/start")
    fun loginStartResponse(
        @RequestBody request: WebAuthNLoginStartRequest, session: HttpSession
    ): WebAuthNLoginStartResponse {
        val response: WebAuthNLoginStartResponse = this.webAuthNLoginService.startLogin(request)
        session.setAttribute(START_LOGIN_REQUEST, response.assertionRequest)
        return response
    }

    @ResponseBody
    @PostMapping("/webauthn/login/finish")
    @Throws(AssertionFailedException::class)
    fun loginStartResponse(@RequestBody request: WebAuthNLoginFinishRequest, session: HttpSession): AssertionResult {
        val assertionRequest = session.getAttribute(START_LOGIN_REQUEST) as AssertionRequest ?: throw RuntimeException("Assertion request not found")
        val result: AssertionResult = this.webAuthNLoginService.finishLogin(request)
        if (result.isSuccess) {
            session.setAttribute(AssertionRequest::class.java.getName(), result)
        }
        return result
    }

}