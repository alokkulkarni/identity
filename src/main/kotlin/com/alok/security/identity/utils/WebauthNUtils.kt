package com.alok.security.identity.utils

import java.nio.ByteBuffer
import com.yubico.webauthn.data.ByteArray as ByteArray1


class WebauthNUtils {

    fun toByteArray(uuid: Long?): ByteArray1 {
        val buffer: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
        if (uuid != null) {
            buffer.putLong(uuid)
        }
        return ByteArray1(buffer.array())
    }

    fun toUUID(byteArray: ByteArray1): Long {
        val byteBuffer: ByteBuffer = ByteBuffer.wrap(byteArray.getBytes())
        return byteBuffer.getLong()
    }
}