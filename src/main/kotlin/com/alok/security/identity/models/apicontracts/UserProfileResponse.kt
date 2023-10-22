package com.alok.security.identity.models.apicontracts

data class UserProfileResponse(
        val username: String,
        val name: String,
        val email: String,
        val phoneNumber: String
)