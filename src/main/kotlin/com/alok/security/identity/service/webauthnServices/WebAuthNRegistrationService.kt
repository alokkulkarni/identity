@file:Suppress("unused")

package com.alok.security.identity.service.webauthnServices

import com.alok.security.identity.models.userModels.UserIdentity
import com.alok.security.identity.models.webauthnModels.WebAuthNCredentials
import com.alok.security.identity.models.webauthnModels.*
import com.alok.security.identity.repository.UserIdentityRepository
import com.alok.security.identity.repository.WebauthNRegistrationFlowRepository
import com.alok.security.identity.service.UserService
import com.alok.security.identity.utils.ByteArrayUtils
import com.alok.security.identity.utils.JsonUtils
import com.fasterxml.jackson.core.JsonProcessingException
import com.yubico.webauthn.FinishRegistrationOptions
import com.yubico.webauthn.RegistrationResult
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.StartRegistrationOptions
import com.yubico.webauthn.data.*
import com.yubico.webauthn.data.UserIdentity.*
import com.yubico.webauthn.exception.RegistrationFailedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
class WebAuthNRegistrationService(
    private val relyingParty: RelyingParty,
    private val userService: UserService,
    private val userIdentityRepository: UserIdentityRepository,
    private val registrationFlowRepository: WebauthNRegistrationFlowRepository
) {

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(WebAuthNRegistrationService::class.java)
    }

    /**
     * Kicks off the registration process by creating a new user account adding it to the database. Then the server
     * configures how the WebAuthn api should be called, this way the server can be as strict as it wants for example
     * the server can demand info about the authenticator that will be used so that it can only accept approved
     * authenticators.
     *
     *
     * The rest of this method needs to be saved into the http session because the finish step requires the excat
     * java object that was returned from this method as an input.
     *
     * @param startRequest the json request sent from the browser
     * @return a json object with configuration details for the javascript in the browser to use to call the webAuthn api
     */
    @Transactional(propagation = Propagation.REQUIRED)
    fun startRegistration(startRequest: RegistrationStartRequest): RegistrationStartResponse {
        val user: UserIdentity = userIdentityRepository.findByUsername(startRequest.username)
            ?: throw IllegalStateException("User does not exist")
        val options: PublicKeyCredentialCreationOptions = createPublicKeyCredentialCreationOptions(user)
        val startResponse: RegistrationStartResponse = createRegistrationStartResponse(options)
        logWorkflow(startRequest, startResponse)
        return startResponse
    }

    @Throws(JsonProcessingException::class)
    private fun logWorkflow(
        startRequest: RegistrationStartRequest, startResponse: RegistrationStartResponse
    ) {
        val registrationEntity = WebAuthNRegistrationFlowEntity(
            startResponse.flowId,
            JsonUtils.toJson(startRequest),
            JsonUtils.toJson(startResponse),
            " ",
            " ",
            JsonUtils.toJson(startResponse.credentialCreationOptions),
            " "
        )
        registrationFlowRepository.save<WebAuthNRegistrationFlowEntity>(registrationEntity)
    }

    private fun createRegistrationStartResponse(
        options: PublicKeyCredentialCreationOptions
    ): RegistrationStartResponse {
        return RegistrationStartResponse(
            UUID.randomUUID(),
            options
        )
    }

    private fun createPublicKeyCredentialCreationOptions(
        user: UserIdentity
    ): PublicKeyCredentialCreationOptions {
        val userIdentity = builder()
            .name(user.username)
            .displayName(user.username)
            .id(ByteArrayUtils().toByteArray(user.id))
            .build()
        val authenticatorSelectionCriteria = AuthenticatorSelectionCriteria.builder()
            .userVerification(UserVerificationRequirement.REQUIRED)
            .residentKey(ResidentKeyRequirement.REQUIRED)
            .authenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM)
            .build()
        val startRegistrationOptions = StartRegistrationOptions.builder()
            .user(userIdentity)
            .timeout(30000)
            .authenticatorSelection(authenticatorSelectionCriteria)
            .build()
        return relyingParty.startRegistration(startRegistrationOptions)
    }

    /**
     * This method associates a FIDO2 authenticator with a user account, by saving the details of the authenticator
     * generated public key and other metadata in the database.
     *
     * @param finishRequest the json request sen from the browser contains the public key of the user
     * @param credentialCreationOptions the options generated from the call to startRegistration() and should have been
     * pulled out of the http session by the controller that calls this method
     * @return JSON object indicating success or failure of the registration
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Throws(
        RegistrationFailedException::class
    )
    fun finishRegistration(
        finishRequest: RegistrationFinishRequest,
        credentialCreationOptions: PublicKeyCredentialCreationOptions
    ): RegistrationFinishResponse {

        val options = FinishRegistrationOptions.builder()
            .request(credentialCreationOptions)
            .response(finishRequest.credential)
            .build()

        val registrationResult = this.relyingParty.finishRegistration(options)

        val fidoCredential = WebAuthNCredentials(
            UUID.randomUUID(),
            registrationResult.keyId.id.base64Url,
            ByteArrayUtils().toUUID(credentialCreationOptions.user.id),
            registrationResult.keyId.type.name,
            registrationResult.publicKeyCose.base64Url,
            registrationResult.signatureCount.toInt()
        )

        this.userService.addCredential(fidoCredential)

        val registrationFinishResponse = RegistrationFinishResponse(
            finishRequest.flowId,
            registrationComplete = true
        )

        logFinishStep(finishRequest, registrationResult, registrationFinishResponse)
        return registrationFinishResponse
    }

    private fun logFinishStep(
        finishRequest: RegistrationFinishRequest,
        registrationResult: RegistrationResult,
        registrationFinishResponse: RegistrationFinishResponse
    ) {
        val registrationFlow: WebAuthNRegistrationFlowEntity = registrationFlowRepository
            .findById(finishRequest.flowId)
            .orElseThrow {
                RuntimeException(
                    "Cloud not find a registration flow with id: "
                            + finishRequest.flowId
                )
            }

        registrationFlow.finishRequest = JsonUtils.toJson(finishRequest)
        registrationFlow.finishResponse = JsonUtils.toJson(registrationFinishResponse)
        registrationFlow.registrationResult = JsonUtils.toJson(registrationResult)
        registrationFlowRepository.save(registrationFlow)
    }

}