package com.tzmusic.backend.routes

import com.tzmusic.backend.data.Catalog
import com.tzmusic.backend.data.PlaylistRepository
import com.tzmusic.backend.data.PlaylistResult
import com.tzmusic.backend.domain.AddTrackRequest
import com.tzmusic.backend.domain.ErrorResponse
import com.tzmusic.backend.domain.NewPlaylistRequest
import com.tzmusic.backend.domain.UpdatePlaylistRequest
import com.tzmusic.backend.security.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

/**
 * Playlist CRUD backed by SQLite. Reads are public (demo playlists for
 * everyone, plus the caller's own when a token is supplied); writes
 * require an authenticated owner. Demo playlists are read-only.
 */
fun Route.playlistRoutes(catalog: Catalog, playlists: PlaylistRepository, auth: AuthService) {
    route("/v1/playlists") {
        get {
            val user = call.authUser(auth)
            call.respond(mapOf("playlists" to playlists.list(user?.id)))
        }

        get("/{id}") {
            val user = call.authUser(auth)
            val playlist = call.parameters["id"]?.let { playlists.get(it, user?.id) }
            if (playlist == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", "Playlist not found"))
                return@get
            }
            call.respond(playlist)
        }

        post {
            val user = call.authUser(auth) ?: return@post call.respondUnauthorized()
            val request = call.receive<NewPlaylistRequest>()
            if (request.name.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("bad_request", "Playlist name must not be blank"),
                )
                return@post
            }
            call.respond(HttpStatusCode.Created, playlists.create(user, request))
        }

        put("/{id}") {
            val user = call.authUser(auth) ?: return@put call.respondUnauthorized()
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("bad_request", "Missing playlist id"),
            )
            val request = call.receive<UpdatePlaylistRequest>()
            call.respondResult(playlists.update(id, user.id, request))
        }

        delete("/{id}") {
            val user = call.authUser(auth) ?: return@delete call.respondUnauthorized()
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("bad_request", "Missing playlist id"),
            )
            when (val result = playlists.delete(id, user.id)) {
                PlaylistResult.Deleted -> call.respond(HttpStatusCode.NoContent)
                is PlaylistResult.NotFound ->
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", result.message))
                PlaylistResult.Forbidden -> call.respondForbidden()
                is PlaylistResult.Ok -> call.respond(result.playlist)
            }
        }

        post("/{id}/tracks") {
            val user = call.authUser(auth) ?: return@post call.respondUnauthorized()
            val id = call.parameters["id"] ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("bad_request", "Missing playlist id"),
            )
            val request = call.receive<AddTrackRequest>()
            val result = playlists.addTrack(id, user.id, request.trackId, trackExists = catalog.track(request.trackId) != null)
            call.respondResult(result)
        }

        delete("/{id}/tracks/{trackId}") {
            val user = call.authUser(auth) ?: return@delete call.respondUnauthorized()
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("bad_request", "Missing playlist id"),
            )
            val trackId = call.parameters["trackId"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse("bad_request", "Missing track id"),
            )
            call.respondResult(playlists.removeTrack(id, user.id, trackId))
        }
    }
}

private suspend fun ApplicationCall.respondResult(result: PlaylistResult) {
    when (result) {
        is PlaylistResult.Ok -> respond(result.playlist)
        PlaylistResult.Deleted -> respond(HttpStatusCode.NoContent)
        is PlaylistResult.NotFound ->
            respond(HttpStatusCode.NotFound, ErrorResponse("not_found", result.message))
        PlaylistResult.Forbidden -> respondForbidden()
    }
}

private suspend fun ApplicationCall.respondForbidden(): Unit =
    respond(HttpStatusCode.Forbidden, ErrorResponse("forbidden", "You cannot modify this playlist"))
