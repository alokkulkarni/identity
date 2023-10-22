@file:Suppress("SENSELESS_COMPARISON")

package com.alok.security.identity.controller

import com.alok.security.identity.models.apicontracts.UserRegistrationRequest
import com.alok.security.identity.models.apicontracts.UserRegistrationResponse
import com.alok.security.identity.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class UserController(private val userService: UserService)  {

    companion object{
        val log = org.slf4j.LoggerFactory.getLogger(UserController::class.java)
    }
    @GetMapping("/user")
    fun getUser(principal: Principal): Principal {
        log.info(principal.name)
        return principal
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody userRegistrationRequest: UserRegistrationRequest): ResponseEntity<UserRegistrationResponse> {
        if (userRegistrationRequest.password != userRegistrationRequest.confirmPassword)
            return ResponseEntity.badRequest().build()
        if (userRegistrationRequest.username.isEmpty() || userRegistrationRequest.password.isEmpty() || userRegistrationRequest.firstName.isEmpty() || userRegistrationRequest.lastName.isEmpty() || userRegistrationRequest.email.isEmpty() || userRegistrationRequest.phoneNumber.isEmpty())
            return ResponseEntity.badRequest().build()
        if (userService.validateUserExists(userRegistrationRequest.username))
            return ResponseEntity.status(302).build()
        return ResponseEntity.ok(userService.registerUser(userRegistrationRequest).let { UserRegistrationResponse(it.username,
                        it.firstName,
                        it.middleName,
                        it.lastName,
                        it.email,
                        it.phoneNumber)}
                .also { log.info("User registered: ${it.username}") })
    }
}