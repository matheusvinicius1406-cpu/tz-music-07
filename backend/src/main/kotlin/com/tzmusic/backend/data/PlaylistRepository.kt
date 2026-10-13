package com.tzmusic.backend.data

import com.tzmusic.backend.data.db.Database
import com.tzmusic.backend.domain.NewPlaylistRequest
import com.tzmusic.backend.domain.PlaylistDto
import com.tzmusic.backend.domain.UpdatePlaylistRequest
import com.tzmusic.backend.security.AuthUser
import java.sql.Connection
import java.util.UUID

sealed interface PlaylistResult {
    data class Ok(val playlist: PlaylistDto) : PlaylistResult
    data class NotFound(val message: String = "Playlist not found") : PlaylistResult
    data object Forbidden : PlaylistResult
    data object Deleted : PlaylistResult
}

/**
 * SQLite-backed playlist storage. Demo playlists are seeded once with a
 * NULL owner (read-only for everyone); user playlists belong to the
 * account that created them and only the owner may edit them.
 */
class PlaylistRepository(private val db: Database) {

    fun seedDemoIfNeeded() {
        db.query { conn ->
            val count = conn.createStatement().use { st ->
                st.executeQuery("SELECT COUNT(*) FROM playlists").use { rs ->
                    rs.next()
                    rs.getInt(1)
                }
            }
            if (count > 0) return@query

            conn.prepareStatement(
                "INSERT INTO playlists (id, user_id, name, description, owner, is_demo) VALUES (?, NULL, ?, ?, ?, 1)",
            ).use { ps ->
                for (p in DemoData.playlists) {
                    ps.setString(1, p.id)
                    ps.setString(2, p.name)
                    ps.setString(3, p.description)
                    ps.setString(4, p.owner)
                    ps.executeUpdate()
                }
            }
            conn.prepareStatement("INSERT INTO playlist_tracks (playlist_id, track_id, position) VALUES (?, ?, ?)").use { ps ->
                for (p in DemoData.playlists) {
                    p.trackIds.forEachIndexed { index, trackId ->
                        ps.setString(1, p.id)
                        ps.setString(2, trackId)
                        ps.setInt(3, index)
                        ps.executeUpdate()
                    }
                }
            }
        }
    }

    fun list(userId: String?): List<PlaylistDto> = db.query { conn ->
        val sql =
            "SELECT id, user_id, name, description, owner FROM playlists " +
                (if (userId == null) "WHERE user_id IS NULL " else "WHERE user_id IS NULL OR user_id = ? ") +
                "ORDER BY is_demo DESC, id"
        conn.prepareStatement(sql).use { ps ->
            if (userId != null) ps.setString(1, userId)
            ps.executeQuery().use { rs ->
                buildList {
                    while (rs.next()) {
                        val id = rs.getString("id")
                        val ownerId = rs.getString("user_id")
                        add(
                            PlaylistDto(
                                id = id,
                                name = rs.getString("name"),
                                description = rs.getString("description"),
                                owner = rs.getString("owner"),
                                trackIds = trackIds(conn, id),
                                canEdit = ownerId != null && ownerId == userId,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun get(id: String, userId: String?): PlaylistDto? = db.query { conn ->
        conn.prepareStatement(
            "SELECT id, user_id, name, description, owner FROM playlists WHERE id = ? AND (user_id IS NULL OR user_id = ?)",
        ).use { ps ->
            ps.setString(1, id)
            ps.setString(2, userId ?: "")
            ps.executeQuery().use { rs ->
                if (!rs.next()) {
                    null
                } else {
                    val ownerId = rs.getString("user_id")
                    PlaylistDto(
                        id = rs.getString("id"),
                        name = rs.getString("name"),
                        description = rs.getString("description"),
                        owner = rs.getString("owner"),
                        trackIds = trackIds(conn, id),
                        canEdit = ownerId != null && ownerId == userId,
                    )
                }
            }
        }
    }

    fun create(user: AuthUser, request: NewPlaylistRequest): PlaylistDto = db.query { conn ->
        val id = "p-" + UUID.randomUUID().toString().take(8)
        conn.prepareStatement("INSERT INTO playlists (id, user_id, name, description, owner) VALUES (?, ?, ?, ?, ?)").use { ps ->
            ps.setString(1, id)
            ps.setString(2, user.id)
            ps.setString(3, request.name)
            ps.setString(4, request.description)
            ps.setString(5, user.username)
            ps.executeUpdate()
        }
        PlaylistDto(
            id = id,
            name = request.name,
            description = request.description,
            owner = user.username,
            trackIds = emptyList(),
            canEdit = true,
        )
    }

    fun update(id: String, userId: String, request: UpdatePlaylistRequest): PlaylistResult = db.query { conn ->
        if (!exists(conn, id)) return@query PlaylistResult.NotFound()
        if (!isOwner(conn, id, userId)) return@query PlaylistResult.Forbidden
        val currentName = stringColumn(conn, "name", id)!!
        val currentDescription = stringColumn(conn, "description", id)!!
        conn.prepareStatement("UPDATE playlists SET name = ?, description = ? WHERE id = ?").use { ps ->
            ps.setString(1, request.name ?: currentName)
            ps.setString(2, request.description ?: currentDescription)
            ps.setString(3, id)
            ps.executeUpdate()
        }
        PlaylistResult.Ok(get(id, userId)!!)
    }

    fun delete(id: String, userId: String): PlaylistResult = db.query { conn ->
        if (!exists(conn, id)) return@query PlaylistResult.NotFound()
        if (!isOwner(conn, id, userId)) return@query PlaylistResult.Forbidden
        conn.prepareStatement("DELETE FROM playlists WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeUpdate()
        }
        PlaylistResult.Deleted
    }

    fun addTrack(id: String, userId: String, trackId: String, trackExists: Boolean): PlaylistResult =
        db.query { conn ->
            if (!exists(conn, id)) return@query PlaylistResult.NotFound("Playlist not found")
            if (!trackExists) return@query PlaylistResult.NotFound("Track not found")
            if (!isOwner(conn, id, userId)) return@query PlaylistResult.Forbidden
            val already = conn.prepareStatement("SELECT 1 FROM playlist_tracks WHERE playlist_id = ? AND track_id = ?").use { ps ->
                ps.setString(1, id)
                ps.setString(2, trackId)
                ps.executeQuery().use { it.next() }
            }
            if (!already) {
                val nextPosition = conn.prepareStatement("SELECT COALESCE(MAX(position), -1) + 1 FROM playlist_tracks WHERE playlist_id = ?").use { ps ->
                    ps.setString(1, id)
                    ps.executeQuery().use { rs -> rs.next(); rs.getInt(1) }
                }
                conn.prepareStatement("INSERT INTO playlist_tracks (playlist_id, track_id, position) VALUES (?, ?, ?)").use { ps ->
                    ps.setString(1, id)
                    ps.setString(2, trackId)
                    ps.setInt(3, nextPosition)
                    ps.executeUpdate()
                }
            }
            PlaylistResult.Ok(get(id, userId)!!)
        }

    fun removeTrack(id: String, userId: String, trackId: String): PlaylistResult = db.query { conn ->
        if (!exists(conn, id)) return@query PlaylistResult.NotFound()
        if (!isOwner(conn, id, userId)) return@query PlaylistResult.Forbidden
        conn.prepareStatement("DELETE FROM playlist_tracks WHERE playlist_id = ? AND track_id = ?").use { ps ->
            ps.setString(1, id)
            ps.setString(2, trackId)
            ps.executeUpdate()
        }
        PlaylistResult.Ok(get(id, userId)!!)
    }

    fun search(query: String, limit: Int = 10): List<PlaylistDto> {
        val like = "%${escapeLike(query)}%"
        val ids = db.query { conn ->
            conn.prepareStatement(
                "SELECT id FROM playlists WHERE user_id IS NULL AND (LOWER(name) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?)) ORDER BY id LIMIT ?",
            ).use { ps ->
                ps.setString(1, like)
                ps.setString(2, like)
                ps.setInt(3, limit)
                ps.executeQuery().use { rs -> buildList { while (rs.next()) add(rs.getString(1)) } }
            }
        }
        return ids.mapNotNull { get(it, null) }
    }

    private fun trackIds(conn: Connection, playlistId: String): List<String> =
        conn.prepareStatement("SELECT track_id FROM playlist_tracks WHERE playlist_id = ? ORDER BY position").use { ps ->
            ps.setString(1, playlistId)
            ps.executeQuery().use { rs -> buildList { while (rs.next()) add(rs.getString(1)) } }
        }

    private fun exists(conn: Connection, id: String): Boolean =
        conn.prepareStatement("SELECT 1 FROM playlists WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeQuery().use { it.next() }
        }

    private fun isOwner(conn: Connection, id: String, userId: String): Boolean =
        conn.prepareStatement("SELECT user_id FROM playlists WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeQuery().use { rs -> rs.next() && rs.getString(1) == userId }
        }

    private fun stringColumn(conn: Connection, column: String, id: String): String? =
        conn.prepareStatement("SELECT $column FROM playlists WHERE id = ?").use { ps ->
            ps.setString(1, id)
            ps.executeQuery().use { rs -> if (rs.next()) rs.getString(1) else null }
        }

    private fun escapeLike(value: String): String = value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
}
