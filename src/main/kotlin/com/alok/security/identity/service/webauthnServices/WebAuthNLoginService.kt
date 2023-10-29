@file:Suppress("unused")

package com.alok.security.identity.service.webauthnServices

import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFinishRequest
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFlowEntity
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginStartRequest
import com.alok.security.identity.models.webauthnModels.WebAuthNLoginStartResponse
import com.alok.security.identity.repository.WebAuthNLoginFlowRepository
import com.alok.security.identity.service.UserService
import com.fasterxml.jackson.core.JsonProcessingException
import com.yubico.webauthn.*
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
        val loginFlowEntity: WebAuthNLoginFlowEntity? = this.webAuthNLoginFlowRepository
            .findById(loginFinishRequest.flowId)
            .orElseThrow {
                RuntimeException(
                    ("flow id " + loginFinishRequest.flowId) + " not found"
                )
            }
        val assertionRequestJson: String = loginFlowEntity?.assertionRequest ?: throw RuntimeException("Assertion request not found")
        val assertionRequest: AssertionRequest? = try {
                                                            AssertionRequest.fromJson(assertionRequestJson)
                                                      } catch (e: JsonProcessingException) {
                                                            throw IllegalArgumentException("Could not deserialize the assertion Request")
                                                      }
        val options = FinishAssertionOptions.builder()
            .request(assertionRequest)
            .response(loginFinishRequest.credential)
            .build()
        val assertionResult = relyingParty.finishAssertion(options)
        loginFlowEntity.assertionResult = assertionResult.toString()
        loginFlowEntity.successfulLogin = assertionResult.isSuccess
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
        userService.findUserEmail(loginStartRequest.username) ?: throw RuntimeException("User not found")

        // make the assertion request to send to the client
        val options: StartAssertionOptions = StartAssertionOptions.builder()
            .timeout(60000)
            .username(loginStartRequest.username) //     .userHandle(YubicoUtils.toByteArray(user.id()))
            .build()
        val assertionRequest = relyingParty.startAssertion(options)
        val loginStartResponse = WebAuthNLoginStartResponse(
            UUID.randomUUID(),
            assertionRequest
        )
        val loginFlowEntity = WebAuthNLoginFlowEntity(
            loginStartResponse.flowid,
            loginStartRequest.toString(),
            loginStartResponse.toString(),
            false,
            assertionRequest.toString(),
            " "
        )
        this.webAuthNLoginFlowRepository.save(loginFlowEntity)
        return loginStartResponse
    }
}