package com.example.accmarket.balance.models.crypto

import org.springframework.beans.factory.annotation.Value
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object BalanceCrypto {

    private const val ALGORITHM = "AES"
    @Value("\${SECRET}")
    private lateinit var secret: String

    private val key = SecretKeySpec(secret.toByteArray(), ALGORITHM)

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.getEncoder().encodeToString(cipher.doFinal(value.toByteArray()))
    }

    fun decrypt(value: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(Base64.getDecoder().decode(value)))
    }
}