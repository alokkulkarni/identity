@file:Suppress("unused", "SpringDataMethodInconsistencyInspection")

package com.alok.security.identity.repository

import com.alok.security.identity.models.userModels.Authenticators
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AuthenticatorRepository: JpaRepository<Authenticators, UUID> {

    fun findAuthenticatorByCredentialId(credentialId: ByteArray): AuthenticatorRepository?
    fun findAllByCredentialId(credentialId: ByteArray): List<AuthenticatorRepository>
    fun findAllByUsername(username: String): List<AuthenticatorRepository>

}