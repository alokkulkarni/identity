package com.alok.security.identity.models.webauthnModels

import java.util.*

data class RegistrationFinishResponse(
    val flowId: UUID,
    val registrationComplete: Boolean
)
