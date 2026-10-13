package com.tzmusic.backend.config

import java.nio.file.Files
import java.nio.file.Path

data class AppConfig(
    val port: Int,
    val host: String,
    val musicDir: Path,
    val dbPath: Path,
    val jwtSecret: String,
    val tokenTtlMillis: Long,
    val subsonicEnabled: Boolean = true,
) {
    val jdbcUrl: String get() = "jdbc:sqlite:$dbPath"

    companion object {
        const val DEFAULT_DEV_SECRET = "dev-only-secret-troque-em-producao"

        fun fromEnv(): AppConfig =
            AppConfig(
                port = intEnv("TZMUSIC_PORT", 8080),
                host = strEnv("TZMUSIC_HOST", "0.0.0.0"),
                musicDir = Path.of(strEnv("TZMUSIC_MUSIC_DIR", "music")),
                dbPath = Path.of(strEnv("TZMUSIC_DB", "data/tzmusic.db")),
                jwtSecret = strEnv("TZMUSIC_JWT_SECRET", DEFAULT_DEV_SECRET),
                tokenTtlMillis = intEnv("TZMUSIC_TOKEN_TTL_MINUTES", 60 * 24).toLong() * 60_000L,
                subsonicEnabled = boolEnv("TZMUSIC_SUBSONIC_ENABLED", true),
            )

        fun forTest(): AppConfig {
            val dir = Files.createTempDirectory("tzmusic-test")
            return AppConfig(
                port = 0,
                host = "127.0.0.1",
                musicDir = Path.of("does-not-exist"),
                dbPath = dir.resolve("test.db"),
                jwtSecret = "test-secret",
                tokenTtlMillis = 3_600_000L,
            )
        }

        private fun strEnv(name: String, default: String): String =
            System.getenv(name)?.takeIf { it.isNotBlank() } ?: default

        private fun intEnv(name: String, default: Int): Int =
            System.getenv(name)?.toIntOrNull() ?: default

        private fun boolEnv(name: String, default: Boolean): Boolean =
            System.getenv(name)?.toBooleanStrictOrNull() ?: default
    }
}
