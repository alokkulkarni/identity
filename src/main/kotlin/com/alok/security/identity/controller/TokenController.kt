package com.alok.security.identity.controller

import com.alok.security.identity.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
@RequestMapping("/tokens")
class TokenController(private val userService: UserService) {


    @PostMapping("/token")
    fun registerToken(@RequestBody tokenRegistrationRequest: TokenRegistrationRequest, principal: Principal): TokenRegistrationResponse {
        val attachedDevice = userService.attachDevice(principal.name, tokenRegistrationRequest.name)
        return TokenRegistrationResponse(attachedDevice.id.toString(), attachedDevice.name, attachedDevice.secret())
    }

    @PostMapping("/confirm")
    fun confirm(@RequestBody body: TokenConfirmationRequest, principal: Principal): TokenConfirmationResponse {
        val confirmation = userService.confirmDevice(principal.name, body.code)
        return TokenConfirmationResponse(confirmation.id.toString(), confirmation.name, confirmation.confirmed())
    }
}


data class TokenRegistrationRequest(val name: String)
data class TokenRegistrationResponse(val id: String, val name: String, val secret: String)

data class TokenConfirmationRequest(val code: String)
data class TokenConfirmationResponse(val id: String, val name: String, val confirmed: Boolean)