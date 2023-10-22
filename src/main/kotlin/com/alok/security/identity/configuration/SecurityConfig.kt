@file:Suppress("removal")

package com.alok.security.identity.configuration

import com.alok.security.identity.security.OTP.OneTimePasswordAuthFilter
import com.alok.security.identity.service.UserService
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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

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
                    .requestMatchers("/register", "/error", "/auth", "/auth**").permitAll()
                    .anyRequest().authenticated()
            }
            .authenticationProvider(DaoAuthenticationProvider().apply {
                setUserDetailsService(userService)
                setPasswordEncoder(passwordEncoder())
            })
            .httpBasic(withDefaults())
            .addFilterAfter(OneTimePasswordAuthFilter(userService), BasicAuthenticationFilter::class.java)
            .logout { logout -> logout.disable() }
        return http.build()
    }
}