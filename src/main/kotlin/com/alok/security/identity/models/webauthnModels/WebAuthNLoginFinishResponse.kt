package com.alok.security.identity.models.webauthnModels

data class WebAuthNLoginFinishResponse(
    val success: Boolean,
    val username: String,
    val signatureCounterValid: Boolean,
)