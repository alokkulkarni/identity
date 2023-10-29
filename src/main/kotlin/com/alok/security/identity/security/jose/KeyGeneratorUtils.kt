package com.alok.security.identity.security.jose

import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECFieldFp
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeyGeneratorUtils {

    fun generateSecretKey(): SecretKey {
        val hmacKey: SecretKey
        try {
            hmacKey = KeyGenerator.getInstance("HmacSha256").generateKey()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return hmacKey
    }

    fun generateRsaKey(): KeyPair {
        val keyPair: KeyPair
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            keyPair = keyPairGenerator.genKeyPair()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return keyPair
    }


    fun generateECKey() : KeyPair {

        val ellipticCurve = EllipticCurve(
            ECFieldFp(
                BigInteger(
                    "115792089210356248762697446949407573530086143415290314195533631308867097853951",
                    16
                )
            ),
            BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948", 16),
            BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291", 16)
        )

        val ecPoint = ECPoint(
            BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109"),
            BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291")
        )

        val ecParameterSpec = ECParameterSpec(
            ellipticCurve,
            ecPoint,
            BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"),
            1
        )


        val keyPair: KeyPair
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("EC")
            keyPairGenerator.initialize(ecParameterSpec)
            keyPair = keyPairGenerator.genKeyPair()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return keyPair
    }

}