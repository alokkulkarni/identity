@file:Suppress("JpaDataSourceORMInspection", "unused")

package com.alok.security.identity.models.magictokens

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.Instant

@Entity(name = "magic_tokens")
class MagicToken {

    @Id
    @GeneratedValue(generator = "UUID")
    val id: Long? = null
    var username: String
    var token: String
    @Column(name = "created_at")
    var created: Instant

    constructor() {
        this.username = ""
        this.token = ""
        this.created = Instant.now()
    }

    constructor(username: String, token: String) {
        this.username = username
        this.token = token
        this.created = Instant.now()
    }

    override fun toString(): String {
        return "MagicToken(username='$username', token='$token', created=$created)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MagicToken) return false

        if (username != other.username) return false
        if (token != other.token) return false
        if (created != other.created) return false

        return true
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + created.hashCode()
        return result
    }
}
