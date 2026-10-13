package com.tzmusic.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.tzmusic.backend.config.AppConfig
import com.tzmusic.backend.data.UserRepository
import com.tzmusic.backend.domain.LoginRequest
import com.tzmusic.backend.domain.RegisterRequest
import com.tzmusic.backend.domain.TokenResponse
import com.tzmusic.backend.domain.UserDto
import java.util.Date

data class AuthUser(val id: String, val username: String)

/**
 * Registration, login and JWT issuing/verification. Tokens carry the
 * user id as `sub` plus a `username` claim, expire per config, and are
 * signed with HMAC-SHA256 using TZMUSIC_JWT_SECRET.
 */
class AuthService(
    private val users: UserRepository,
    private val config: AppConfig,
) {
    private val algorithm = Algorithm.HMAC256(config.jwtSecret)

    fun registerValid(request: RegisterRequest): Boolean =
        request.username.matches(USERNAME_REGEX) && request.password.length >= MIN_PASSWORD_LENGTH

    fun register(request: RegisterRequest): UserDto? {
        if (users.findByUsername(request.username) != null) return null
        val hashed = PasswordHasher.hash(request.password)
        val record = users.create(request.username, hashed.hash, hashed.salt, md5Hex(request.password)) ?: return null
        return UserDto(id = record.id, username = record.username)
    }

    fun login(request: LoginRequest): TokenResponse? {
        val user = users.findByUsername(request.username) ?: return null
        if (!PasswordHasher.verify(request.password, user.salt, user.passwordHash)) return null
        val expiresAt = Date(System.currentTimeMillis() + config.tokenTtlMillis)
        val token = JWT.create()
            .withIssuer(ISSUER)
            .withClaim("sub", user.id)
            .withClaim("username", user.username)
            .withExpiresAt(expiresAt)
            .sign(algorithm)
        return TokenResponse(
            token = token,
            expiresIn = config.tokenTtlMillis / 1000,
            userId = user.id,
            username = user.username,
        )
    }

    fun verify(token: String): AuthUser? {
        val payload = try {
            JWT.require(algorithm).withIssuer(ISSUER).build().verify(token)
        } catch (_: JWTVerificationException) {
            return null
        }
        val id = payload.getClaim("sub").asString()?.takeIf { it.isNotBlank() } ?: return null
        val username = payload.getClaim("username").asString()?.takeIf { it.isNotBlank() } ?: return null
        return AuthUser(id = id, username = username)
    }

    /**
     * OpenSubsonic-compatible check for the `p=enc:<md5hex>` scheme: the
     * client sends the hex MD5 of the password, which is compared against
     * the legacy hash stored at registration. Required because Subsonic
     * clients cannot produce the PBKDF2-based tokens.
     */
    fun verifySubsonic(username: String, md5PasswordHex: String): AuthUser? {
        val user = users.findByUsername(username) ?: return null
        val stored = user.subsonicMd5 ?: return null
        if (!stored.equals(md5PasswordHex, ignoreCase = true)) return null
        return AuthUser(id = user.id, username = user.username)
    }

    companion object {
        const val ISSUER = "tzmusic-backend"
        const val MIN_PASSWORD_LENGTH = 8
        val USERNAME_REGEX = Regex("[A-Za-z0-9_.-]{3,32}")

        fun md5Hex(value: String): String =
            java.security.MessageDigest.getInstance("MD5")
                .digest(value.toByteArray())
                .joinToString("") { "%02x".format(it) }
    }
}
