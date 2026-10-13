package com.tzmusic.backend.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Password hashing with PBKDF2-HMAC-SHA256. Salt is random per user and
 * stored next to the hash; iterations are kept high enough for a
 * self-hosted service without hurting login latency.
 */
object PasswordHasher {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256

    data class HashedPassword(val hash: String, val salt: String)

    fun hash(password: String): HashedPassword {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return HashedPassword(toHex(pbkdf2(password, salt)), toHex(salt))
    }

    fun verify(password: String, saltHex: String, expectedHashHex: String): Boolean =
        toHex(pbkdf2(password, fromHex(saltHex))) == expectedHashHex

    private fun pbkdf2(password: String, salt: ByteArray): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS))
            .encoded

    private fun toHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

    private fun fromHex(value: String): ByteArray =
        ByteArray(value.length / 2) { value.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
}
