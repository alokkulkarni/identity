package com.alok.security.identity.utils

import java.nio.ByteBuffer

class ByteArrayUtils {

    fun toByteArray(uuid: Long?): com.yubico.webauthn.data.ByteArray {
        val buffer: ByteBuffer = ByteBuffer.wrap(ByteArray(16))
        if (uuid != null) {
            buffer.putLong(uuid)
        }
        return com.yubico.webauthn.data.ByteArray(buffer.array())
    }

    fun toUUID(byteArray: com.yubico.webauthn.data.ByteArray): Long {
        val byteBuffer: ByteBuffer = ByteBuffer.wrap(byteArray.getBytes())
        return byteBuffer.getLong()
    }
}