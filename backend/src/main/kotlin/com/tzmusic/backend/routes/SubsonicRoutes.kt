package com.tzmusic.backend.routes

import com.tzmusic.backend.data.Catalog
import com.tzmusic.backend.data.PlaylistRepository
import com.tzmusic.backend.domain.TrackDto
import com.tzmusic.backend.security.AuthService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

private const val SERVER_VERSION = "1.16.1"
private const val SERVER_NAME = "tzmusic"
private const val API_VERSION = "0.1.0"

/**
 * OpenSubsonic-compatible REST ".view" surface (JSON mode), so
 * Navidrome-style clients can point at this server. Implemented from
 * the public OpenSubsonic specification with contract tests; the
 * read-only subset needed for browsing and playback is exposed.
 *
 * Authentication: only the legacy `p=enc:<md5hex(password)>` scheme is
 * verified (compared against the MD5 stored at registration). The newer
 * `t`/`s` token scheme requires the plaintext password and is out of
 * scope until reversible credential storage is designed.
 */
fun Route.subsonicRoutes(catalog: Catalog, playlists: PlaylistRepository, auth: AuthService) {
    route("/rest") {
        get("/ping.view") {
            if (!call.subsonicAuth(auth)) return@get
            call.subsonicOk()
        }
        get("/getLicense.view") {
            if (!call.subsonicAuth(auth)) return@get
            call.subsonicOk(
                "license" to buildJsonObject {
                    put("licenseExpires", "2030-01-01T00:00:00.000Z")
                    put("trial", false)
                    put("trialSeconds", 0)
                    put("rights", "self-hosted")
                },
            )
        }
        get("/getMusicFolders.view") {
            if (!call.subsonicAuth(auth)) return@get
            call.subsonicOk(
                "musicFolders" to buildJsonArray {
                    add(
                        buildJsonObject {
                            put("id", "1")
                            put("name", "tz'music")
                        },
                    )
                },
            )
        }
        get("/getIndexes.view") {
            if (!call.subsonicAuth(auth)) return@get
            call.subsonicOk("indexes" to artistIndexes(catalog))
        }
        get("/getArtists.view") {
            if (!call.subsonicAuth(auth)) return@get
            call.subsonicOk("artists" to artistIndexes(catalog))
        }
        get("/getMusicDirectory.view") {
            if (!call.subsonicAuth(auth)) return@get
            val id = call.request.queryParameters["id"]
                ?: return@get call.subsonicError(10, "Missing parameter 'id'")
            val children = directoryChildren(catalog, id)
                ?: return@get call.subsonicError(70, "Directory not found")
            call.subsonicOk(
                "directory" to buildJsonObject {
                    put("id", id)
                    put("parent", "root")
                    put("name", id)
                    put("child", JsonArray(children))
                },
            )
        }
        get("/search3.view") {
            if (!call.subsonicAuth(auth)) return@get
            val query = call.request.queryParameters["query"].orEmpty()
            call.subsonicOk(
                "searchResult3" to buildJsonObject {
                    put("artist", JsonArray(emptyList()))
                    put("album", JsonArray(emptyList()))
                    put("song", JsonArray(catalog.tracks(query).map { songEntry(it) }))
                },
            )
        }
        get("/getPlaylists.view") {
            if (!call.subsonicAuth(auth)) return@get
            call.subsonicOk(
                "playlists" to buildJsonArray {
                    playlists.list(null).forEach { playlist ->
                        val tracks = playlist.trackIds.mapNotNull { catalog.track(it) }
                        add(
                            buildJsonObject {
                                put("id", playlist.id)
                                put("name", playlist.name)
                                put("owner", playlist.owner)
                                put("public", true)
                                put("songCount", tracks.size)
                                put("duration", tracks.sumOf { it.duration })
                                put("created", "2026-01-01T00:00:00.000Z")
                                put("entry", JsonArray(tracks.map { songEntry(it) }))
                            },
                        )
                    }
                },
            )
        }
        get("/stream.view") {
            if (!call.subsonicAuth(auth)) return@get
            val id = call.request.queryParameters["id"]
                ?: return@get call.subsonicError(10, "Missing parameter 'id'")
            val file = catalog.fileFor(id)
            if (file == null) {
                call.subsonicError(70, "Requested data not found (demonstration track has no audio)")
                return@get
            }
            call.response.header(HttpHeaders.AcceptRanges, "bytes")
            call.respondFile(file.toFile())
        }
    }
}

// ---------- call helpers ----------

/** Returns true when authenticated; otherwise responds with the standard failure envelope. */
private suspend fun ApplicationCall.subsonicAuth(auth: AuthService): Boolean {
    val username = request.queryParameters["u"]
    val passwordParam = request.queryParameters["p"]
    val user = if (!username.isNullOrBlank() && passwordParam != null) {
        auth.verifySubsonic(username, passwordParam.removePrefix("enc:"))
    } else {
        null
    }
    if (user == null) {
        subsonicError(40, "Wrong username or password (use p=enc:<md5hex(password)>)")
        return false
    }
    return true
}

private suspend fun ApplicationCall.subsonicOk(vararg extra: Pair<String, kotlinx.serialization.json.JsonElement>) {
    val body = buildJsonObject {
        put("subsonic-response", buildJsonObject {
            put("status", "ok")
            put("version", SERVER_VERSION)
            put("type", SERVER_NAME)
            put("serverVersion", API_VERSION)
            put("openSubsonic", true)
            extra.forEach { (key, value) -> put(key, value) }
        })
    }
    respond(HttpStatusCode.OK, body)
}

private suspend fun ApplicationCall.subsonicError(code: Int, message: String) {
    val body = buildJsonObject {
        put("subsonic-response", buildJsonObject {
            put("status", "failed")
            put("version", SERVER_VERSION)
            put("type", SERVER_NAME)
            put("serverVersion", API_VERSION)
            put("openSubsonic", true)
            put("error", buildJsonObject {
                put("code", code)
                put("message", message)
            })
        })
    }
    respond(HttpStatusCode.OK, body)
}

// ---------- catalog helpers ----------

private fun artistIndexes(catalog: Catalog): JsonObject {
    val byInitial = catalog.artists().groupBy { it.name.firstOrNull()?.uppercaseChar()?.toString() ?: "#" }
    return buildJsonObject {
        put(
            "index",
            buildJsonArray {
                byInitial.forEach { (initial, artists) ->
                    add(
                        buildJsonObject {
                            put("name", initial)
                            put(
                                "artist",
                                buildJsonArray {
                                    artists.forEach { artist ->
                                        add(
                                            buildJsonObject {
                                                put("id", artist.id)
                                                put("name", artist.name)
                                                put("albumCount", artist.trackCount)
                                                put("coverArt", artist.cover)
                                            },
                                        )
                                    }
                                },
                            )
                        },
                    )
                }
            },
        )
    }
}

private fun directoryChildren(catalog: Catalog, id: String): List<JsonObject>? {
    if (id == "root") {
        return catalog.artists().map { artist ->
            buildJsonObject {
                put("id", artist.id)
                put("parent", "root")
                put("isDir", true)
                put("title", artist.name)
                put("artist", artist.name)
                put("albumCount", artist.trackCount)
            }
        }
    }
    val artist = catalog.artist(id)
    if (artist != null) {
        val artistName = artist.artist.name
        return catalog.albums().filter { it.artist == artistName }.map { album ->
            buildJsonObject {
                put("id", album.id)
                put("parent", id)
                put("isDir", true)
                put("title", album.title)
                put("artist", album.artist)
                put("album", album.title)
                put("songCount", album.trackCount)
            }
        }
    }
    val album = catalog.album(id) ?: return null
    return album.tracks.map { track -> songEntry(track, parent = id) }
}

private fun songEntry(track: TrackDto, parent: String = "root"): JsonObject =
    buildJsonObject {
        put("id", track.id)
        put("parent", parent)
        put("isDir", false)
        put("title", track.title)
        put("artist", track.artist)
        put("album", track.album)
        put("duration", track.duration)
        put("suffix", "mp3")
        put("contentType", "audio/mpeg")
        put("coverArt", track.cover)
        put("path", track.id)
        put("type", "music")
    }
