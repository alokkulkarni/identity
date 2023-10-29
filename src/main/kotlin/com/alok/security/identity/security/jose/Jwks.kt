package com.alok.security.identity.security.jose

import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jose.jwk.RSAKey
import java.security.Key
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*


class Jwks {
    var keys: List<Key>? = null

    fun generateRSAKey(): RSAKey {
        val keyPair = KeyGeneratorUtils().generateRsaKey()
        val publicKey = keyPair.public
        val privateKey = keyPair.private
        return RSAKey.Builder(publicKey as RSAPublicKey)
            .privateKey(privateKey as RSAPrivateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
    }

    fun generateECKey(): ECKey {
        val keyPair = KeyGeneratorUtils().generateECKey()
        val publicKey = keyPair.public
        val privateKey = keyPair.private
        val forECParameterSpec = Curve.forECParameterSpec((publicKey as ECPublicKey).params)
        return ECKey.Builder(/* crv = */ forECParameterSpec, /* pub = */ publicKey)
            .privateKey(privateKey as ECPrivateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
    }

    fun generateOctateKey(): OctetSequenceKey {
        val generateSecretKey = KeyGeneratorUtils().generateSecretKey()
        return OctetSequenceKey.Builder(generateSecretKey)
            .keyID(UUID.randomUUID().toString())
            .build()
    }
}