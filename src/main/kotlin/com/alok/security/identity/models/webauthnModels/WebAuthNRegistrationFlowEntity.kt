package com.alok.security.identity.models.webauthnModels

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*


@Entity(name = "webauthn_registration_flow")
@Table(name = "webauthn_registration_flow")
data class WebAuthNRegistrationFlowEntity(
    @Id
    @Column(name = "id")
    val id: UUID, // This is the flowId
    @Column(name = "start_request", length = 2048)
    val startRequest: String,
    @Column(name = "start_response", length = 2048)
    val startResponse: String,
    @Column(name = "finish_request", length = 2048)
    var finishRequest: String,
    @Column(name = "finish_response", length = 2048)
    var finishResponse: String,
    @Column(name = "registration_result", length = 2048)
    var registrationResult: String,
    @Column(name = "credential_options", length = 2048)
    val credentialOptions: String)
{

   constructor() : this(UUID.randomUUID(), " ", " ", " ", " ", " ", " ")
}

