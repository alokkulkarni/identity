@file:Suppress("unused")

package com.alok.security.identity.models.userModels

import com.alok.security.identity.models.mfaDevice.OneTimePasswordDeviceEntity
import com.alok.security.identity.models.webauthnModels.WebAuthNCredentials
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

@Entity(name = "users_identity")
@Table(name = "users_identity")
data class UserIdentity(
        @Id
        val id: Long,
        @Column("username")
        @NotNull
        val username: String,
        @NotNull
        @Column("password")
        val password: String,
        @NotNull
        @Column("first_name")
        val firstName: String,
        @Column("middle_name")
        @Null
        val middleName: String?,
        @Column("last_name")
        @NotNull
        val lastName: String,
        @Column("email")
        @NotNull
        val email: String,
        @Column("phone_number")
        @NotNull
        val phoneNumber: String,
        @Column("enabled")
        @NotNull
        val enabled: Boolean,
        @Column("account_non_expired")
        @NotNull
        val accountNonExpired: Boolean,
        @Column("credentials_non_expired")
        @NotNull
        val credentialsNonExpired: Boolean,
        @Column("account_non_locked")
        @NotNull
        val accountNonLocked: Boolean,
        @Column("registration_date_time")
        @NotNull
        val registrationDateTime: LocalDateTime,
        @Column("last_logged_in")
        @NotNull
        var lastloggedin: LocalDateTime,
        @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true, targetEntity = OneTimePasswordDeviceEntity::class)
        @JoinColumn(name = "device_id", referencedColumnName = "id")
        @Null
        var device: OneTimePasswordDeviceEntity?,
        @OneToMany(targetEntity = Authorities::class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @Null
        var authorities: MutableList<Authorities>,
        @OneToMany(targetEntity = WebAuthNCredentials::class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        var webAuthNCredentials: MutableList<WebAuthNCredentials>? = mutableListOf(WebAuthNCredentials())

) {
    constructor() : this(0, " ", " ", " ", " ", " ", " ", " ", true, true, true, true, LocalDateTime.now(), LocalDateTime.now(), null, mutableListOf(), mutableListOf())
}