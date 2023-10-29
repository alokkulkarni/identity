@file:Suppress("unused")

package com.alok.security.identity.utils

import com.alok.security.identity.models.userModels.TokenUserDetails
import com.alok.security.identity.models.userModels.UserIdentity
import com.alok.security.identity.security.jose.Jwks
import com.nimbusds.jose.EncryptionMethod
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWEAlgorithm
import com.nimbusds.jose.JWEHeader
import com.nimbusds.jose.crypto.RSADecrypter
import com.nimbusds.jose.crypto.RSAEncrypter
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.EncryptedJWT
import com.nimbusds.jwt.JWTClaimsSet
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.expression.ParseException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


class JwtSupport {

    private val key = Keys.hmacShaKeyFor("7d4qID4dDnUBtTHZPHYHKqzUXrXPX/d1Q2rTR1BKm9Y=".toByteArray())

    companion object {
        val log = LoggerFactory.getLogger(JwtSupport::class.java)
    }

    val rsaKey: RSAKey = Jwks().generateRSAKey()

    private fun getJwtParser(): JwtParser {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(rsaKey.toRSAPublicKey())
                .build()
        } catch (e: JOSEException) {
            throw RuntimeException(e)
        }
    }

    private val claimsMap: MutableMap<String, String?> = HashMap()

    fun generate(user: UserIdentity): String {
        TokenUserDetails(user).authorities.forEach { grantedAuthority ->
            claimsMap[grantedAuthority.authority.substring(5).lowercase(Locale.getDefault())] =
                grantedAuthority.authority.substring(5).lowercase(Locale.getDefault())
        }
        val jwtBuilder: JwtBuilder = try {
            Jwts.builder()
                .setClaims(claimsMap)
                .setSubject(user.username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                .setAudience("mobileApp")
                .setIssuer("localhost")
                .setHeaderParam("type", "JWS")
                .setId(rsaKey.keyID)
                .signWith(rsaKey.toRSAPrivateKey())
        } catch (e: JOSEException) {
            throw RuntimeException(e)
        }
        return jwtBuilder.compact()
    }

    fun generateJWE(user: UserIdentity): String {
        TokenUserDetails(user).authorities.forEach { grantedAuthority ->
            claimsMap[grantedAuthority.authority.substring(5).lowercase(Locale.getDefault())] =
                grantedAuthority.authority.substring(5).lowercase(Locale.getDefault())
        }
        val claimsSet = JWTClaimsSet.Builder()
        claimsSet.audience("mobileApp")
        claimsSet.expirationTime(Date(Date().time + 1000 * 60 * 10))
        claimsSet.notBeforeTime(Date())
        claimsSet.jwtID(UUID.randomUUID().toString())
        claimsSet.subject(user.username)
        claimsSet.issueTime(Date.from(Instant.now()))
        claimsSet.issuer("localhost")
        claimsSet.claim("KeyID", rsaKey.keyID)
        TokenUserDetails(user).authorities.forEach { grantedAuthority ->
            claimsSet.claim(
                "scope",
                grantedAuthority.authority.substring(5).lowercase(Locale.getDefault())
            )
        }
        val header = JWEHeader(JWEAlgorithm.RSA_OAEP_512, EncryptionMethod.A256CBC_HS512)
        val jwt = EncryptedJWT(header, claimsSet.build())
        return try {
            val encrypter = RSAEncrypter(rsaKey.toRSAPublicKey())
            jwt.encrypt(encrypter)
            jwt.serialize()
        } catch (e: JOSEException) {
            throw RuntimeException(e)
        }
    }

    fun getUserName(token: String?): String {
        return try {
            val jwt = EncryptedJWT.parse(token)
            val decrypter = RSADecrypter(rsaKey.toPrivateKey())
            jwt.decrypt(decrypter)
            jwt.getJWTClaimsSet().subject
        } catch (e: ParseException) {
            throw RuntimeException(e)
        } catch (e: JOSEException) {
            throw RuntimeException(e)
        }
    }

    fun isValid(token: String?, user: UserIdentity): Boolean {
        /*
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        boolean after = claims.getExpiration().after(Date.from(Instant.now()));
        return after && (Objects.equals(user.getUsername(), getUserName(token)));
        */
        return try {
            val jwt = EncryptedJWT.parse(token)
            val decrypter = RSADecrypter(rsaKey.toPrivateKey())
            jwt.decrypt(decrypter)
            val after = jwt.getJWTClaimsSet().expirationTime.after(Date.from(Instant.now()))
            val username = jwt.getJWTClaimsSet().subject
            after && Objects.equals(user.username, username)
        } catch (e: ParseException) {
            throw RuntimeException(e)
        } catch (e: JOSEException) {
            throw RuntimeException(e)
        }
    }
}