package com.tzmusic.backend.data

import com.tzmusic.backend.data.db.Database
import java.sql.SQLException
import java.util.UUID

data class UserRecord(
    val id: String,
    val username: String,
    val passwordHash: String,
    val salt: String,
    val subsonicMd5: String? = null,
)

class UserRepository(private val db: Database) {

    fun create(username: String, passwordHash: String, salt: String, subsonicMd5: String?): UserRecord? {
        val id = "u-" + UUID.randomUUID().toString().take(8)
        return try {
            db.query { conn ->
                conn.prepareStatement(
                    "INSERT INTO users (id, username, password_hash, salt, subsonic_md5) VALUES (?, ?, ?, ?, ?)",
                ).use { ps ->
                    ps.setString(1, id)
                    ps.setString(2, username)
                    ps.setString(3, passwordHash)
                    ps.setString(4, salt)
                    ps.setString(5, subsonicMd5)
                    ps.executeUpdate()
                }
            }
            UserRecord(id, username, passwordHash, salt, subsonicMd5)
        } catch (_: SQLException) {
            null
        }
    }

    fun findByUsername(username: String): UserRecord? = db.query { conn ->
        conn.prepareStatement("SELECT id, username, password_hash, salt, subsonic_md5 FROM users WHERE username = ?").use { ps ->
            ps.setString(1, username)
            ps.executeQuery().use { rs ->
                if (rs.next()) {
                    UserRecord(
                        id = rs.getString("id"),
                        username = rs.getString("username"),
                        passwordHash = rs.getString("password_hash"),
                        salt = rs.getString("salt"),
                        subsonicMd5 = rs.getString("subsonic_md5"),
                    )
                } else {
                    null
                }
            }
        }
    }
}
