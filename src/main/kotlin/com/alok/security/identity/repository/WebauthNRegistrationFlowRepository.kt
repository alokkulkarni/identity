package com.alok.security.identity.repository

import com.alok.security.identity.models.webauthnModels.WebAuthNRegistrationFlowEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WebauthNRegistrationFlowRepository: JpaRepository<WebAuthNRegistrationFlowEntity, UUID>