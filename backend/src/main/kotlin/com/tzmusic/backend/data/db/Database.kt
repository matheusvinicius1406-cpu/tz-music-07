package com.tzmusic.backend.data.db

import org.flywaydb.core.Flyway
import java.sql.Connection
import java.sql.DriverManager

/**
 * Thin JDBC access to SQLite. One connection per operation is fine for
 * the expected self-hosted load; a pooled setup can replace this later
 * without touching the repositories.
 */
class Database(private val jdbcUrl: String) {

    fun <T> query(block: (Connection) -> T): T =
        DriverManager.getConnection(jdbcUrl).use { conn ->
            conn.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }
            block(conn)
        }

    fun migrate() {
        Flyway.configure()
            .dataSource(jdbcUrl, null, null)
            .locations("classpath:db/migration")
            .load()
            .migrate()
    }
}
