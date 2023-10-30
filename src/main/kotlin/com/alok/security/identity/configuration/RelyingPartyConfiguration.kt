package com.alok.security.identity.configuration

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.RelyingPartyIdentity
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RelyingPartyConfiguration(val webauthNProperties: WebauthNProperties) {

    @Bean
    fun relyingParty(credentialRepository: CredentialRepository): RelyingParty {
        val relyingPartyIdentity = RelyingPartyIdentity.builder()
            .id(webauthNProperties.rpId)
            .name(webauthNProperties.rpName)
            .build()

        return RelyingParty.builder()
            .identity(relyingPartyIdentity)
            .credentialRepository(credentialRepository)
            .validateSignatureCounter(false)
            .allowUntrustedAttestation(false)
            .allowOriginPort(true)
            .allowOriginSubdomain(true)
            .origins(webauthNProperties.origin.toSet())
            .build()
    }
}