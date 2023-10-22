package com.alok.security.identity.models.apicontracts

data class UserRegistrationRequest(
        val username: String,
        val password: String,
        val confirmPassword: String,
        val firstName: String,
        val middleName: String?,
        val lastName: String,
        val email: String,
        val phoneNumber: String
)