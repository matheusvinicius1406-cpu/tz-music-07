package com.tzmusic.backend.routes

import com.tzmusic.backend.config.AppConfig
import com.tzmusic.backend.data.Catalog
import com.tzmusic.backend.domain.ErrorResponse
import com.tzmusic.backend.domain.HealthDto
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

private const val VERSION = "0.1.0"

fun Route.healthRoutes(catalog: Catalog, config: AppConfig) {
    route("/health") {
        get {
            call.respond(
                HealthDto(
                    status = "ok",
                    version = VERSION,
                    musicDir = config.musicDir.toString(),
                    localTracks = catalog.localTrackCount,
                ),
            )
        }
    }
}

fun Route.catalogRoutes(catalog: Catalog) {
    route("/v1") {
        get("/tracks") {
            val query = call.request.queryParameters["q"]
            call.respond(mapOf("tracks" to catalog.tracks(query)))
        }
        get("/tracks/{id}") {
            val track = call.parameters["id"]?.let { catalog.track(it) }
            if (track == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", "Track not found"))
                return@get
            }
            call.respond(track)
        }
        get("/albums") {
            call.respond(mapOf("albums" to catalog.albums()))
        }
        get("/albums/{id}") {
            val album = call.parameters["id"]?.let { catalog.album(it) }
            if (album == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", "Album not found"))
                return@get
            }
            call.respond(album)
        }
        get("/artists") {
            call.respond(mapOf("artists" to catalog.artists()))
        }
        get("/artists/{id}") {
            val artist = call.parameters["id"]?.let { catalog.artist(it) }
            if (artist == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("not_found", "Artist not found"))
                return@get
            }
            call.respond(artist)
        }
        get("/search") {
            val query = call.request.queryParameters["q"]
            if (query.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_request", "Query parameter 'q' is required"))
                return@get
            }
            call.respond(catalog.search(query))
        }
    }
}

fun Route.streamRoutes(catalog: Catalog) {
    route("/v1") {
        get("/stream/{id}") {
            val id = call.parameters["id"]
            val file = id?.let { catalog.fileFor(it) }
            if (file == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        "no_audio",
                        "This entry is a demonstration track without audio. Mount a music directory and use a local track.",
                    ),
                )
                return@get
            }
            call.response.header(HttpHeaders.AcceptRanges, "bytes")
            call.respondFile(file.toFile())
        }
    }
}
