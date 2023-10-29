@file:Suppress("unused")

package com.alok.security.identity.utils

import java.security.SecureRandom
import com.yubico.webauthn.data.ByteArray as ByteArray1

class RandomGenerator {

    private val random: SecureRandom = SecureRandom()

    private fun RandomGenerator() {}

    fun generateRandom(length: Int): ByteArray1 {
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return ByteArray1(bytes)
    }
}