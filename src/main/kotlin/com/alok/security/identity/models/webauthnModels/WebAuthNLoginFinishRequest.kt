package com.alok.security.identity.models.webauthnModels

import com.yubico.webauthn.data.AuthenticatorAssertionResponse
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import java.util.UUID

data class WebAuthNLoginFinishRequest(
    val flowId : UUID,
    val credential: PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>
)
