@file:Suppress("unused")

package com.alok.security.identity.models.mfaDevice

import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator
import jakarta.persistence.*
import java.util.UUID

@Entity(name = "one_time_password_devices")
data class OneTimePasswordDeviceEntity (
        @Id
        val id: UUID,
        @Column(name = "name")
        val name: String,
        @Column(name = "type")
        val type: String,
        @Column(name = "secret")
        val secret: String,
        @Column(name = "confirmed")
        var confirmed: Boolean
) {
    constructor() : this(UUID.randomUUID(), "", "", "", false)  // JPA requires a default constructor)
}


interface OneTimePasswordDevice {
    val id: UUID
    val name: String
    val type: String

    fun secret(): String

    fun confirm(code: String): Boolean

    fun confirmed(): Boolean

    fun accepts(code: String): Boolean
}

class GoogleAuthenticatorDevice(
        override val id: UUID = UUID.randomUUID(),
        override val name: String,
        override val type: String = "google-authenticator",
        private val secret: ByteArray = GoogleAuthenticator.createRandomSecretAsByteArray(),
        private var confirmed: Boolean = false
) : OneTimePasswordDevice {

    private val authenticator = GoogleAuthenticator(secret)

    override fun confirm(code: String): Boolean {
        if (accepts(code)) {
            confirmed = true
        }
        return confirmed
    }

    override fun confirmed(): Boolean  = confirmed

    override fun accepts(code: String): Boolean {
        return authenticator.generate() == code
    }

    override fun secret(): String = secret.toString(Charsets.UTF_8)

}