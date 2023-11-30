@file:Suppress("unused", "USELESS_ELVIS")

package com.alok.security.identity.service.webauthnServices

import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFinishRequest
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFlowEntity
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginStartRequest
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginStartResponse
import com.alok.security.identity.repository.WebAuthNLoginFlowRepository
import com.alok.security.identity.service.UserService
import com.alok.security.identity.utils.ByteArrayUtils
import com.alok.security.identity.utils.JsonUtils
import com.fasterxml.jackson.core.JsonProcessingException
import com.yubico.webauthn.*
import com.yubico.webauthn.data.Extensions
import com.yubico.webauthn.data.UserVerificationRequirement
import com.yubico.webauthn.exception.AssertionFailedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class WebAuthNLoginService(val relyingParty: RelyingParty,
                           val webAuthNLoginFlowRepository: WebAuthNLoginFlowRepository,
                           val userService: UserService)
{

    companion object{
        private val logger = org.slf4j.LoggerFactory.getLogger(WebAuthNLoginService::class.java)
    }
    /**
     * Receives the solution to the math challenge from the start method, validates that the solution is correct
     * applies the validation logic of the FIDO protocol, and then it produces a result.
     *
     * @param loginFinishRequest
     * @return
     * @throws AssertionFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Throws(AssertionFailedException::class)
    fun finishLogin(loginFinishRequest: WebAuthNLoginFinishRequest): AssertionResult {

        val loginFlowEntity: WebAuthNLoginFlowEntity = this.webAuthNLoginFlowRepository
                                                        .findById(loginFinishRequest.flowId)
                                                        .orElseThrow {
                                                            RuntimeException(
                                                                ("flow id " + loginFinishRequest.flowId) + " not found"
                                                            )
                                                        }
        val assertionRequestJson: String = loginFlowEntity.assertionRequest ?: throw RuntimeException("Assertion request not found")
        logger.warn("Assertion JSON request: $assertionRequestJson")
        val credentials =  loginFinishRequest.credential ?: throw RuntimeException("Credentials not found")
        logger.warn("Credentials: $credentials")

        val assertionRequest: AssertionRequest? = try {
                                                            AssertionRequest.fromJson(assertionRequestJson)
                                                      } catch (e: JsonProcessingException) {
                                                            throw IllegalArgumentException("Could not deserialize the assertion Request")
                                                      }
        logger.warn("Assertion request: $assertionRequest")
        val options = FinishAssertionOptions.builder()
            .request(assertionRequest)
            .response(credentials)
            .build()

        logger.warn("Options: $options")
        val assertionResult = relyingParty.finishAssertion(options)
        logger.warn("Assertion result: $assertionResult")
        loginFlowEntity.assertionResult = JsonUtils.toJson(assertionResult)
        loginFlowEntity.successfulLogin = assertionResult.isSuccess
        this.webAuthNLoginFlowRepository.save(loginFlowEntity)
        return assertionResult
    }

    /**
     * This method is used to determine if a user exists and then sends back to the browser a list of
     * public keys that can be used to log in this way the browser can pick the right authenticator and
     * complete the login process. The response includes a math challenge that the authenticator needs
     * to solve using the users private key so that the server can tell that the user is who they say
     * they are.
     *
     * @param loginStartRequest info containing the user that wants to login
     * @return configuration for the browser to use to interact with the FIDO2 authenticator using WebAuthn browser API
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun startLogin(loginStartRequest: WebAuthNLoginStartRequest): WebAuthNLoginStartResponse {

        // Find the user in the user database
        val userIdentity = userService.findUserEmail(loginStartRequest.username) ?: throw RuntimeException("User not found")

        // make the assertion request to send to the client
        val options: StartAssertionOptions = StartAssertionOptions.builder()
            .timeout(60000)
            .username(userIdentity.username)
//            .userHandle(ByteArrayUtils().toByteArray(userIdentity.username))
            .userVerification(UserVerificationRequirement.REQUIRED)
            .build()
        val assertionRequest = relyingParty.startAssertion(options)
        val loginStartResponse = WebAuthNLoginStartResponse(
            UUID.randomUUID(),
            assertionRequest
        )
        val loginFlowEntity = WebAuthNLoginFlowEntity(
            loginStartResponse.flowid,
            JsonUtils.toJson(loginStartRequest),
            JsonUtils.toJson(loginStartResponse),
            false,
            assertionRequest.toJson(),
            " "
        )
        this.webAuthNLoginFlowRepository.save(loginFlowEntity)
        return loginStartResponse
    }
}