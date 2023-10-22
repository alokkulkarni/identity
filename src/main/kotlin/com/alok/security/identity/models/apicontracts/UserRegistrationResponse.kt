package com.alok.security.identity.models.apicontracts

data class UserRegistrationResponse(
        val username: String,
        val firstName: String,
        val middleName: String?,
        val lastName: String,
        val email: String,
        val phoneNumber: String
)