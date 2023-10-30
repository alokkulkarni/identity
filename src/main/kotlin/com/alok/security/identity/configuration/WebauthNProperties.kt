package com.alok.security.identity.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "webauthn")
class WebauthNProperties {

    val hostname: String = "localhost"
    val displayName: String = "WebauthN Demo"
    val rpName: String = "WebauthN Demo"
    val origin: MutableList<String> = mutableListOf("http://localhost:9898")
    val rpId: String = "localhost"
    val icon: String = "https://avatars.githubusercontent.com/u/24622181?s=200&v=4"
    val attestation: String = "none"
    val challengeSize: Int = 32
    val timeout: Long = 60000
    val userVerification: String = "preferred"
    val userVerificationFallback: String = "required"
    val userVerificationOptional: String = "preferred"
    val userVerificationDiscouraged: String = "discouraged"
    val userVerificationRequired: String = "required"
    val userVerificationPreferred: String = "preferred"
    val userVerificationSupported: String = "supported"
    val userVerificationUnavailable: String = "unavailable"


}