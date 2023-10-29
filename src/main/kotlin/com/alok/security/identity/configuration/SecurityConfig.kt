@file:Suppress("removal", "UNUSED_ANONYMOUS_PARAMETER")

package com.alok.security.identity.configuration

import com.alok.security.identity.security.OTP.OneTimePasswordAuthFilter
import com.alok.security.identity.security.webauthn.WebAuthNAuthenticationConverter
import com.alok.security.identity.security.webauthn.WebAuthNAuthenticationManager
import com.alok.security.identity.security.webauthn.WebAuthNLoginSuccessHandler
import com.alok.security.identity.service.UserService
import com.alok.security.identity.utils.JwtSupport
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity(debug = true)
class SecurityConfig {


    @Bean
    fun passwordEncoder(): PasswordEncoder  = BCryptPasswordEncoder()


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun chain(http: HttpSecurity,userService: UserService): SecurityFilterChain {

        http
            .csrf { csrf ->
                csrf.disable()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/","/register", "/error", "/auth", "/auth**").permitAll()
                    .anyRequest().authenticated()
            }
            .authenticationProvider(DaoAuthenticationProvider().apply {
                setUserDetailsService(userService)
                setPasswordEncoder(passwordEncoder())
            })
            .httpBasic(withDefaults())
            .addFilterAfter(OneTimePasswordAuthFilter(userService), BasicAuthenticationFilter::class.java)
            .logout { logout -> logout.clearAuthentication(true) }
        return http.build()
    }

    @Bean
    @Order(2)
    fun otpFilterChain(http: HttpSecurity,userService: UserService): SecurityFilterChain {

        http
            .csrf { csrf ->
                csrf.disable()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers( "/auth", "/auth**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterAfter(OneTimePasswordAuthFilter(userService), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    @Order(1)
    @Throws(Exception::class)
    fun webAuthnSecurityFilterChain(
        http: HttpSecurity,
        webAuthNAuthenticationManager: WebAuthNAuthenticationManager
    ): SecurityFilterChain {

        http
            .csrf { csrf ->
                csrf.disable()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/webauthn/login/start",
                        "/webauthn/login/finish",
                        "/webauthn/register/start",
                        "/webauthn/register/finish",
                        "/webauthn/login",
                        "favicon.ico", "/static/js/**", "/js/**"
                    )
                    .permitAll()
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
        val authenticationFilter =
            AuthenticationFilter(webAuthNAuthenticationManager, WebAuthNAuthenticationConverter())
        authenticationFilter.setRequestMatcher(AntPathRequestMatcher("/fido/login"))
        authenticationFilter.setSuccessHandler(WebAuthNLoginSuccessHandler())
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey: RSAKey = JwtSupport().rsaKey
        val jwkSet = JWKSet(rsaKey)
        return JWKSource<SecurityContext> { jwkSelector, securityContext ->
            jwkSelector.select(
                jwkSet
            )
        }
    }

}