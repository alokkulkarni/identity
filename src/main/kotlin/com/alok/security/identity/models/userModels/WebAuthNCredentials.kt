@file:Suppress("unused")

package com.alok.security.identity.models.userModels

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity(name = "webauthn_credentials")
@Table(name = "webauthn_credentials")
data class WebAuthNCredentials(
    @Id
    val id: UUID,
    @Column(name = "key_id")
    val keyId: String,
    @Column(name = "username")
    val userId: Long,
    @Column(name = "type")
    val type: String,
    @Column(name = "public_key")
    val publicKey: String,
    @Column(name = "sign_count")
    val signCount: Int
) {
    constructor() : this(UUID.randomUUID(), "", 0, "", "", 0)
}
