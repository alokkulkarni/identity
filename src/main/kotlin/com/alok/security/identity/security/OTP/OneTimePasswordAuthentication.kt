@file:Suppress("unused")

package com.alok.security.identity.security.OTP

import org.springframework.security.core.Authentication

class OneTimePasswordAuthentication(val initial: Authentication, val code: String) : Authentication by initial {

    override fun getCredentials(): Any {
        return code
    }
}