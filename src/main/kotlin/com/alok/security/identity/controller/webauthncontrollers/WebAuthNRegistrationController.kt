package com.alok.security.identity.controller.webauthncontrollers

import com.alok.security.identity.models.webauthnModels.RegistrationFinishRequest
import com.alok.security.identity.models.webauthnModels.RegistrationFinishResponse
import com.alok.security.identity.models.webauthnModels.RegistrationStartRequest
import com.alok.security.identity.models.webauthnModels.RegistrationStartResponse
import com.alok.security.identity.service.webauthnServices.WebAuthNRegistrationService
import com.fasterxml.jackson.core.JsonProcessingException
import com.yubico.webauthn.exception.RegistrationFailedException
import jakarta.servlet.http.HttpSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class WebAuthNRegistrationController(private val webAuthNRegistrationService: WebAuthNRegistrationService) {

    companion object{
        val logger: Logger = LoggerFactory.getLogger(WebAuthNRegistrationController::class.java)
        private var START_REG_REQUEST = "start_reg_request"
        private var FINISH_REG_REQUEST = "finish_reg_request"
    }

    @PostMapping("/webauthn/register/start", consumes = ["application/json"], produces = ["application/json"])
    @Throws(JsonProcessingException::class, RuntimeException::class)
    @ResponseBody
    fun startRegistration(
        @RequestBody request: RegistrationStartRequest, session: HttpSession
    ): RegistrationStartResponse {
        val response: RegistrationStartResponse = this.webAuthNRegistrationService.startRegistration(request)
        session.setAttribute(START_REG_REQUEST, response)
        return response
    }

    @PostMapping("/webauthn/register/finish", consumes = ["application/json"], produces = ["application/json"])
    @Throws(RegistrationFailedException::class, JsonProcessingException::class, RuntimeException::class)
    @ResponseBody
    fun finishRegistration(
        @RequestBody request: RegistrationFinishRequest, session: HttpSession
    ): RegistrationFinishResponse {
        val response = session.getAttribute(START_REG_REQUEST) as RegistrationStartResponse
        Assert.notNull(response, "Cloud Not find the original request")
        return webAuthNRegistrationService.finishRegistration(
            request, response.credentialCreationOptions
        )
    }
}