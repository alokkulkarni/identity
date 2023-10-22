package com.alok.security.identity.controller

import com.alok.security.identity.models.userModels.TokenUserDetails
import com.alok.security.identity.security.qrCode.QRCodeGenerator
import com.alok.security.identity.service.UserService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.awt.image.BufferedImage
import java.security.Principal


@RestController
class CodeController(private val qrCodeGenerator: QRCodeGenerator, private val userService: UserService) {

    companion object {
        val log = org.slf4j.LoggerFactory.getLogger(CodeController::class.java)
    }

    @GetMapping("/qr", produces = [MediaType.IMAGE_PNG_VALUE])
    fun generateQRCodeImage(principal: Principal): BufferedImage? {
        val user = userService.loadUserByUsername(principal.name) as TokenUserDetails
        log.info("Generating QR Code for ${user.getEmail()} ${user.getDevice()?.secret()}")
        return user.getDevice()?.let { qrCodeGenerator.generateQRCodeImage(user.getEmail(), it.secret()) }
    }
}