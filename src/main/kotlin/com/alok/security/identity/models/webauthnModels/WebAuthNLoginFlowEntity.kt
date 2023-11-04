@file:Suppress("unused", "DuplicatedCode")

package com.alok.security.identity.models.webauthnModels

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity(name = "webauthn_login_flow")
@Table(name = "webauthn_login_flow")
class WebAuthNLoginFlowEntity(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "start_request", length = 2048)
    val startRequest: String,

    @Column(name = "start_response", length = 2048)
    val startResponse: String,

    @Column(name = "successful_login")
    var successfulLogin: Boolean,

    @Column(name = "assertion_request", length = 2048)
    val assertionRequest: String,

    @Column(name = "assertion_result", length = 2048)
    var assertionResult : String
) {
    constructor() : this(UUID.randomUUID(), " ", " ", false, " ", " ")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WebAuthNLoginFlowEntity) return false

        return when {
            id != other.id -> false
            startRequest != other.startRequest -> false
            startResponse != other.startResponse -> false
            successfulLogin != other.successfulLogin -> false
            assertionRequest != other.assertionRequest -> false
            assertionResult != other.assertionResult -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        var result: Int = id.hashCode()
        result = 31 * result + startRequest.hashCode()
        result = 31 * result + startResponse.hashCode()
        result = 31 * result + successfulLogin.hashCode()
        result = 31 * result + assertionRequest.hashCode()
        result = 31 * result + assertionResult.hashCode()
        return result
    }

    override fun toString(): String {
        return "WebAuthNLoginFlowEntity(id=$id, startRequest='$startRequest', startResponse='$startResponse', successfulLogin=$successfulLogin, assertionRequest='$assertionRequest', assertionResult='$assertionResult')"
    }
}