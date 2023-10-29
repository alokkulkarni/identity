@file:Suppress("unused", "SimplifyBooleanWithConstants")

package com.alok.security.identity.models.userModels

import com.alok.security.identity.models.mfaDevice.GoogleAuthenticatorDevice
import com.alok.security.identity.models.mfaDevice.OneTimePasswordDevice
import com.alok.security.identity.models.webauthnModels.WebAuthNCredentials
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class TokenUserDetails(private val userIdentity: UserIdentity) : UserDetails {

    companion object {
        private const val serialVersionUID = 1L
        private val log = org.slf4j.LoggerFactory.getLogger(TokenUserDetails::class.java)
    }
    override fun toString(): String = userIdentity.username

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return userIdentity.authorities.map { SimpleGrantedAuthority(it.authority) }.toMutableList()
    }

    override fun getPassword(): String {
        return userIdentity.password
    }

    override fun getUsername(): String {
        return userIdentity.username
    }

    override fun isAccountNonExpired(): Boolean {
        return userIdentity.accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return userIdentity.accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return userIdentity.credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return userIdentity.enabled
    }

    fun getEmail(): String {
        return userIdentity.email
    }

    fun getMobileNumber(): String {
        return userIdentity.phoneNumber
    }

    fun getDevice(): OneTimePasswordDevice? {
        return userIdentity.device?.let { GoogleAuthenticatorDevice(it.id, userIdentity.device!!.name, userIdentity.device!!.type, userIdentity.device!!.secret, userIdentity.device!!.confirmed) }
    }

    fun getWebAuthNCredentials(): MutableList<WebAuthNCredentials>? {
        return userIdentity.webAuthNCredentials
    }

    fun requiresMfa(): Boolean {
        log.info("Checking if user ${userIdentity.username} requires MFA")
        log.info("User ${userIdentity.username} has device ${userIdentity.device}")
        val googleAuthenticatorDevice = userIdentity.device?.let { GoogleAuthenticatorDevice(it.id, userIdentity.device!!.name, userIdentity.device!!.type, userIdentity.device!!.secret, userIdentity.device!!.confirmed) }
        if (googleAuthenticatorDevice != null) return googleAuthenticatorDevice.confirmed() == true
        return false
    }
}