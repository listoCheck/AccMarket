package com.example.accmarket.utils.JWT

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtProvider(private val rsaKey: RSAKey) {
    private val signer = RSASSASigner(rsaKey.toPrivateKey())

    fun createToken(username: String, roles: Collection<String>, lifetimeSeconds: Long = 3600): String {
        val now = Date()
        val exp = Date(now.time + lifetimeSeconds * 1000)
        val claims = JWTClaimsSet.Builder()
            .subject(username)
            .issueTime(now)
            .expirationTime(exp)
            .claim("roles", roles)
            .issuer("http://localhost:8080")
            .build()
        val signedJWT = SignedJWT(JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.keyID).build(), claims)
        signedJWT.sign(signer)
        return signedJWT.serialize()
    }
}
