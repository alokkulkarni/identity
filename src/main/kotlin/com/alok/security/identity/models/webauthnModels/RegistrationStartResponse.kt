package com.alok.security.identity.models.webauthnModels

import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import jakarta.persistence.Id
import java.util.UUID

class RegistrationStartResponse(
    val flowId: UUID,
    val credentialCreationOptions: PublicKeyCredentialCreationOptions
)