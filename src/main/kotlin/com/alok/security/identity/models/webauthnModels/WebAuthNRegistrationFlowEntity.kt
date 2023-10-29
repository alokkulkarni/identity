package com.alok.security.identity.models.webauthnModels

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*


@Entity(name = "webauthn_registration_flow")
@Table(name = "webauthn_registration_flow")
data class WebAuthNRegistrationFlowEntity(
    @Id
    val id: UUID, // This is the flowId
    val startRequest: String,
    val startResponse: String,
    var finishRequest: String,
    var finishResponse: String,
    var registrationResult: String,
    val credentialOptions: String)
{

   constructor() : this(UUID.randomUUID(), " ", " ", " ", " ", " ", " ")
}

