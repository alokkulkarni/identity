package com.alok.security.identity.models.userModels

import jakarta.persistence.*
import java.util.UUID

@Entity(name = "authorities")
data class Authorities(
    @Id
    val id: UUID,
    @Column(name = "authority")
    val authority: String

) {
    constructor() : this(UUID.randomUUID(), " " )
}