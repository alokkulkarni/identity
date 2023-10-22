package com.alok.security.identity.security.qrCode

import com.fasterxml.jackson.databind.ObjectWriter
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage

@Component
class QRCodeGenerator(private val writer: QRCodeWriter) {

    fun generateQRCodeImage(email: String, secret: String): BufferedImage {
        val issuer = "Spring Identity"
        val uri = "otpauth://totp/$issuer:$email?secret=$secret&issuer=$issuer"
        val matrix = writer.encode(uri, BarcodeFormat.QR_CODE, 200, 200)
        return MatrixToImageWriter.toBufferedImage(matrix)
    }
}