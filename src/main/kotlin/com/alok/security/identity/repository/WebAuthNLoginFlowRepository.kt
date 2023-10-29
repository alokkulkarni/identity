package com.alok.security.identity.repository

import com.alok.security.identity.models.webauthnModels.WebAuthNLoginFlowEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WebAuthNLoginFlowRepository : JpaRepository<WebAuthNLoginFlowEntity, UUID>