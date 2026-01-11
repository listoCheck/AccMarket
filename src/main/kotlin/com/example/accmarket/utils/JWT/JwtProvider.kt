package com.example.accmarket.utils.JWT

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.text.ParseException
import java.util.Date
import java.util.UUID

@Component
class JwtProvider(private val rsaKey: RSAKey) {
    private val signerRefresh = RSASSASigner(rsaKey.toPrivateKey())
    private val signerAccess = RSASSASigner(rsaKey.toPrivateKey())

    fun createRefreshToken(username: String, roles: Collection<String>, userId: UUID? = null, lifetimeSeconds: Long = 60 * 60 * 24 * 30): String {
        val now = Date()
        val exp = Date(now.time + lifetimeSeconds * 1000)
        val claimsBuilder = JWTClaimsSet.Builder()
            .subject(username)
            .issueTime(now)
            .expirationTime(exp)
            .claim("roles", roles)
        
        if (userId != null) {
            claimsBuilder.claim("userId", userId.toString())
        }
        
        val claims = claimsBuilder.build()
        val signedJWT = SignedJWT(JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.keyID).build(), claims)
        signedJWT.sign(signerRefresh)
        return signedJWT.serialize()
    }

    fun createAccessToken(username: String, roles: Collection<String>, userId: UUID? = null, lifetimeSeconds: Long = 3600): String {
        val now = Date()
        val exp = Date(now.time + lifetimeSeconds * 1000)
        val claimsBuilder = JWTClaimsSet.Builder()
            .subject(username)
            .issueTime(now)
            .expirationTime(exp)
            .claim("roles", roles)
        
        if (userId != null) {
            claimsBuilder.claim("userId", userId.toString())
        }
        
        val claims = claimsBuilder.build()
        val signedJWT = SignedJWT(JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.keyID).build(), claims)
        signedJWT.sign(signerAccess)
        return signedJWT.serialize()
    }

    fun verifyToken(token: String): Boolean {
        return try {
            val signedJWT = SignedJWT.parse(token)
            val verifier = RSASSAVerifier(rsaKey.toRSAPublicKey())
            if (!signedJWT.verify(verifier)) {
                return false
            }
            val exp = signedJWT.jwtClaimsSet.expirationTime
            if (exp.before(Date())) {
                return false
            }
            true
        } catch (e: ParseException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun parseAndValidateToken(token: String): JWTClaimsSet? {
        return try {
            val signedJWT = SignedJWT.parse(token)
            val verifier = RSASSAVerifier(rsaKey.toRSAPublicKey())
            if (!signedJWT.verify(verifier)) return null
            val claims = signedJWT.jwtClaimsSet
            if (claims.expirationTime.before(Date())) return null
            claims
        } catch (e: Exception) {
            null
        }
    }
    fun getUserId(authorizationHeader: String): UUID {
        val token = authorizationHeader.removePrefix("Bearer ").trim()

        val claims = parseAndValidateToken(token)
            ?: throw IllegalArgumentException("Invalid or expired token")

        val userId = claims.getStringClaim("userId")
            ?: throw IllegalStateException("userId claim not found")

        return UUID.fromString(userId)
    }


}
