package com.alok.security.identity.models.userModels

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*


@Entity(name = "authenticators")
@Table(name = "authenticators")
data class Authenticators(

    @Id
    val id: UUID,
    val username: String,
    val name: String,
    val credentialId: ByteArray,
    val publicKey: ByteArray,
    val signCount: Long,
    val aaguid: ByteArray,
) {
    constructor() : this(UUID.randomUUID(), " ", " ", ByteArray(0), ByteArray(0), 0, ByteArray(0))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Authenticators

        if (id != other.id) return false
        if (username != other.username) return false
        if (name != other.name) return false
        if (!credentialId.contentEquals(other.credentialId)) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        if (signCount != other.signCount) return false
        if (!aaguid.contentEquals(other.aaguid)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + credentialId.contentHashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + signCount.hashCode()
        result = 31 * result + aaguid.contentHashCode()
        return result
    }
}