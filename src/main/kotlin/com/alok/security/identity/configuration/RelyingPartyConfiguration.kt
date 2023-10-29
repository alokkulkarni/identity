package com.alok.security.identity.configuration

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.RelyingPartyIdentity
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RelyingPartyConfiguration {

    @Bean
    fun relyingParty(credentialRepository: CredentialRepository): RelyingParty {
        val relyingPartyIdentity = RelyingPartyIdentity.builder()
            .id("localhost")
            .name("localhost")
            .build()

        return RelyingParty.builder()
            .identity(relyingPartyIdentity)
            .credentialRepository(credentialRepository)
            .validateSignatureCounter(true)
            .allowUntrustedAttestation(false)
            .allowOriginPort(true)
            .allowOriginSubdomain(true)
            .build()
    }

}