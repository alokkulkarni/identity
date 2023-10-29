package com.alok.security.identity.service.webauthnServices

import com.alok.security.identity.models.userModels.TokenUserDetails
import com.alok.security.identity.models.userModels.WebAuthNCredentials
import com.alok.security.identity.service.UserService
import com.alok.security.identity.utils.WebauthNUtils
import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import com.yubico.webauthn.data.PublicKeyCredentialType
import com.yubico.webauthn.data.exception.Base64UrlException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*


@Component
class CredentialRepositoryImpl(val userService: UserService): CredentialRepository {

    companion object {
        val log: Logger = LoggerFactory.getLogger(CredentialRepositoryImpl::class.java)
    }
    override fun getCredentialIdsForUsername(username: String?): MutableSet<PublicKeyCredentialDescriptor> {
        val user: TokenUserDetails = userService.loadUserByUsername(username!!) as TokenUserDetails
        val credentialIds = mutableSetOf<PublicKeyCredentialDescriptor>()
        user.getWebAuthNCredentials()?.forEach {
            credentialIds.add(toPublicKeyCredentialDescriptor(it))
        }
        return credentialIds
    }

    override fun getUserHandleForUsername(username: String): Optional<ByteArray> {
        this.userService.findUserEmail(username)?.let {
            return Optional.of(WebauthNUtils().toByteArray(it.id))
        }
        return Optional.empty()
    }

    override fun getUsernameForUserHandle(userHandle: ByteArray): Optional<String> {
        if (userHandle.isEmpty) {
            return Optional.empty()
        }
        val findUserById = this.userService
            .findUserById(WebauthNUtils().toUUID(userHandle))
        return Optional.of(findUserById.get().username)
    }

    override fun lookup(credentialId: ByteArray, userHandle: ByteArray): Optional<RegisteredCredential>? {
        return userService.findUserById(WebauthNUtils().toUUID(userHandle)).let {
            it.get().webAuthNCredentials
                ?.stream()
                ?.filter { cred ->
                    try {
                        return@filter credentialId == ByteArray.fromBase64Url(cred.keyId)
                    } catch (e: Base64UrlException) {
                        throw java.lang.RuntimeException(e)
                    }
                }
                ?.findFirst()
                ?.map { cred -> toRegisteredCredential(cred) }
        }
    }

    override fun lookupAll(credentialId: ByteArray): MutableSet<RegisteredCredential> {
        return this.userService
            .findCredentialById(credentialId.base64Url)
            .map { cred -> toRegisteredCredential(cred) }
            .map { cred -> mutableSetOf(cred) }
            .orElseGet { mutableSetOf() }
    }

    private fun toRegisteredCredential(fidoCredential: WebAuthNCredentials): RegisteredCredential {
        return try {
            RegisteredCredential.builder()
                .credentialId(ByteArray.fromBase64Url(fidoCredential.keyId))
                .userHandle(WebauthNUtils().toByteArray(fidoCredential.userId))
                .publicKeyCose(ByteArray.fromBase64Url(fidoCredential.publicKey))
                .build()
        } catch (e: Base64UrlException) {
            throw RuntimeException(e)
        }
    }

    private fun toPublicKeyCredentialDescriptor(
        cred: WebAuthNCredentials
    ): PublicKeyCredentialDescriptor {
        return try {
            PublicKeyCredentialDescriptor.builder()
                .id(ByteArray.fromBase64Url(cred.keyId))
                .type(PublicKeyCredentialType.valueOf(cred.type))
                .build()
        } catch (e: Base64UrlException) {
            throw RuntimeException(e)
        }
    }

}