@file:Suppress("unused")

package com.alok.security.identity.models.webauthnModels

import com.yubico.webauthn.AssertionRequest
import java.util.UUID

data class WebAuthNLoginStartResponse(
     var flowid: UUID,
    var assertionRequest: AssertionRequest
)