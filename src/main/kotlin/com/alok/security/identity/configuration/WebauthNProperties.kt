@file:Suppress("unused")

package com.alok.security.identity.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "webauthn")
class WebauthNProperties(
    var hostname: String = "dba34d200f1cb35f.ngrok.app",
    var displayName: String = "WebauthN Demo",
    var rpName: String = "WebauthN Demo",
    var origin: MutableList<String> = mutableListOf("http://localhost:9898","https://dba34d200f1cb35f.ngrok.app/"),
    var rpId: String = "localhost",
    var icon: String = "https://avatars.githubusercontent.com/u/24622181?s=200&v=4",
    var attestation: String = "none",
    var challengeSize: Int = 64,
    var timeout: Long = 60000,
    var userVerification: String = "preferred",
    var userVerificationFallback: String = "required"
)