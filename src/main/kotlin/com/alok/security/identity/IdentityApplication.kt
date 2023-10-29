package com.alok.security.identity

import com.alok.security.identity.configuration.WebauthNProperties
import com.alok.security.identity.models.apicontracts.UserRegistrationRequest
import com.alok.security.identity.models.userModels.WebAuthNCredentials
import com.alok.security.identity.repository.UserIdentityRepository
import com.alok.security.identity.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.util.*

@SpringBootApplication
@EnableJdbcRepositories
@EnableJpaRepositories
@EnableConfigurationProperties(WebauthNProperties::class)
class IdentityApplication {

	@Bean
	fun run(users: UserService, userIdentityRepository: UserIdentityRepository) = CommandLineRunner {
		userIdentityRepository.deleteAll()
		val username = "kulkarni.alok@gmail.com"
		users.registerUser(UserRegistrationRequest(username,
				"swordfish",
				"swordfish",
				"Alok",
				"",
				"kulkarni",
				"kulkarni.alok@gmail.com",
				"1234567890"))
		users.attachConfirmedDevice(username, "Google Authenticator", "AKMW3WXXWBHAMAHC")
	}
}

fun main(args: Array<String>) {
	runApplication<IdentityApplication>(*args)
}
