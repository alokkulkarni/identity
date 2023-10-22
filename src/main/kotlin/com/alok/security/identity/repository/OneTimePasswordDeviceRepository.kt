package com.alok.security.identity.repository

import com.alok.security.identity.models.mfaDevice.OneTimePasswordDeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OneTimePasswordDeviceRepository : JpaRepository<OneTimePasswordDeviceEntity, UUID> {
}