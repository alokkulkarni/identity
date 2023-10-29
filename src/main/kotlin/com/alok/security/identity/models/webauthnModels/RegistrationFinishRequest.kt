package com.alok.security.identity.models.webauthnModels

import com.yubico.webauthn.data.AuthenticatorAttestationResponse
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import java.util.UUID

data class RegistrationFinishRequest(
    val flowId: UUID,
    val credential : PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>
)
