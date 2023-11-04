package com.alok.security.identity.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "webauthn")
class WebauthNProperties {

    val hostname: String = "dba34d200f1cb35f.ngrok.app"
    val displayName: String = "WebauthN Demo"
    val rpName: String = "WebauthN Demo"
    val origin: MutableList<String> = mutableListOf("http://localhost:9898","https://dba34d200f1cb35f.ngrok.app/")
    val rpId: String = "localhost"
    val icon: String = "https://avatars.githubusercontent.com/u/24622181?s=200&v=4"
    val attestation: String = "none"
    val challengeSize: Int = 64
    val timeout: Long = 60000
    val userVerification: String = "preferred"
    val userVerificationFallback: String = "required"
}