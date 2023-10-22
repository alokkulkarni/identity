@file:Suppress("unused")

package com.alok.security.identity.security.OTP

import com.alok.security.identity.models.userModels.TokenUserDetails
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.filter.OncePerRequestFilter


class OneTimePasswordAuthFilter(private val users: UserDetailsService) : OncePerRequestFilter() {

    companion object {
        private val log = LoggerFactory.getLogger(OneTimePasswordAuthFilter::class.java)
    }


    private val securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()


    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {


        try {
            val initial = SecurityContextHolder.getContext().authentication

            if (initial == null) {
                log.info("No authentication token found or initial authentication is null")
                this.securityContextHolderStrategy.createEmptyContext()
                val anonymousAuthenticationToken = AnonymousAuthenticationToken(
                    "key",
                    "anonymousUser",
                    listOf(SimpleGrantedAuthority("ROLE_ANONYMOUS"))
                )
                SecurityContextHolder.getContext().authentication = anonymousAuthenticationToken
                filterChain.doFilter(request, response)
                return
            } else {
                if (initial.isAuthenticated) {
                    val user = users.loadUserByUsername(initial.name) as TokenUserDetails
                    log.info("User ${user.username} is authenticated")
                    log.info("User ${user.username} requires MFA ${user.requiresMfa()}")

                    if (user.requiresMfa()) {
                        log.info("User ${user.username} requires MFA")
                        val token = request.getHeader("X-OTP")
                        if (token != null && token.isNotEmpty()) {
                            log.info("Token received ${user.username}")
                            try {
                                val auth = OneTimePasswordAuthentication(initial, token)
                                SecurityContextHolder.getContext().authentication = auth
                            } catch (cause: AuthenticationException) {
                                log.info("Token validation failed for user ${user.username}")
                                response.status = HttpServletResponse.SC_UNAUTHORIZED
                                return
                            }
                        } else {
                            log.info("Token not received for ${user.username}")
                            response.status = HttpServletResponse.SC_UNAUTHORIZED
                            response.setHeader("WWW-Authenticate", "OTP")
                            return
                        }
                    }
                    filterChain.doFilter(request, response)
                }
            }
        } catch (e: Exception) {
            this.securityContextHolderStrategy.clearContext()
            log.info("No authentication token found")
            filterChain.doFilter(request, response)
            return
        }
    }

}