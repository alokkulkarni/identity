@file:Suppress("unused")

package com.alok.security.identity.service

import com.alok.security.identity.models.apicontracts.UserRegistrationRequest
import com.alok.security.identity.models.mfaDevice.GoogleAuthenticatorDevice
import com.alok.security.identity.models.mfaDevice.OneTimePasswordDevice
import com.alok.security.identity.models.mfaDevice.OneTimePasswordDeviceEntity
import com.alok.security.identity.models.userModels.Authorities
import com.alok.security.identity.models.userModels.TokenUserDetails
import com.alok.security.identity.models.userModels.UserIdentity
import com.alok.security.identity.models.webauthnModels.WebAuthNCredentials
import com.alok.security.identity.repository.OneTimePasswordDeviceRepository
import com.alok.security.identity.repository.UserIdentityRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(val encoder: PasswordEncoder,
                  val securedUserRepository: UserIdentityRepository,
                  val oneTimePasswordDeviceRepository: OneTimePasswordDeviceRepository) : UserDetailsService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        if (username.isEmpty()) throw BadCredentialsException("Username is mandatory")
        val user = securedUserRepository.findByUsername(username)
        return user?.let { TokenUserDetails(it) } ?: throw UsernameNotFoundException("User not found")
    }

    fun validateUserExists(username: String): Boolean {
        return securedUserRepository.existsByUsername(username)
    }


    fun registerUser(userRegistrationRequest: UserRegistrationRequest): UserIdentity {
        securedUserRepository.findByUsername(userRegistrationRequest.username).apply { if (this != null) throw IllegalStateException("User already exists") }
        val identity = UserIdentity(
                UUID.randomUUID().mostSignificantBits,
                userRegistrationRequest.username,
                encoder.encode(userRegistrationRequest.password),
                userRegistrationRequest.firstName,
                userRegistrationRequest.middleName.orEmpty(),
                userRegistrationRequest.lastName,
                userRegistrationRequest.email,
                userRegistrationRequest.phoneNumber,
                enabled = true,
                accountNonExpired = true,
                credentialsNonExpired = true,
                accountNonLocked = true,
                registrationDateTime = LocalDateTime.now(),
                lastloggedin = LocalDateTime.now(),
                device = null,
                authorities = mutableListOf(Authorities(UUID.randomUUID(),"ROLE_USER")),
                webAuthNCredentials = mutableListOf()
        )
        securedUserRepository.save(identity)
        return identity
    }

    fun attachDevice(username: String, name: String): OneTimePasswordDevice {
        val user = securedUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")
        val device = GoogleAuthenticatorDevice(name = name)
        val deviceEntity = OneTimePasswordDeviceEntity(
                UUID.randomUUID(),
                device.name,
                device.type,
                device.secret(),
                device.confirmed())
        user.device = deviceEntity
        securedUserRepository.save(user)
        return device
    }

    fun confirmDevice(username: String, code: String): OneTimePasswordDevice {
        val user = securedUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")
        val device = user.device ?: throw IllegalStateException("No device attached")
        val googleAuthenticatorDevice = GoogleAuthenticatorDevice(device.id, device.name, device.type, device.secret, device.confirmed)
        if (googleAuthenticatorDevice.confirm(code)) {
            return googleAuthenticatorDevice
        }
        throw IllegalStateException("Invalid code")
    }

    fun attachConfirmedDevice(username: String, name: String, secret: String): OneTimePasswordDevice {
        val user = securedUserRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")
        val device = GoogleAuthenticatorDevice(name = name, secret = secret, confirmed = true)
        val deviceEntity = OneTimePasswordDeviceEntity(
                device.id,
                device.name,
                device.type,
                device.secret(),
                device.confirmed())
        user.device = deviceEntity
        securedUserRepository.save(user)
        return device
    }


    @Transactional(propagation = Propagation.REQUIRED)
    fun addCredential(fidoCredential: WebAuthNCredentials) {
        val account: UserIdentity = this.securedUserRepository.findById(fidoCredential.userId)
            .orElseThrow { RuntimeException("can't add a credential to a user that does not exist") }
        account.webAuthNCredentials?.add(fidoCredential)
    }

    fun findCredentialById(credentialId: String?): Optional<WebAuthNCredentials> {
        return Optional.empty()
    }

    fun findUserById(userId: Long): Optional<UserIdentity> {
        return this.securedUserRepository.findById(userId)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun findUserEmail(username: String): UserIdentity? {
        return this.securedUserRepository.findByUsername(username)
    }
}