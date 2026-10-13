package com.tzmusic.backend

import com.tzmusic.backend.config.AppConfig
import com.tzmusic.backend.data.Catalog
import com.tzmusic.backend.data.PlaylistRepository
import com.tzmusic.backend.data.UserRepository
import com.tzmusic.backend.data.db.Database
import com.tzmusic.backend.library.LibraryScanner
import com.tzmusic.backend.routes.authRoutes
import com.tzmusic.backend.routes.catalogRoutes
import com.tzmusic.backend.routes.healthRoutes
import com.tzmusic.backend.routes.playlistRoutes
import com.tzmusic.backend.routes.streamRoutes
import com.tzmusic.backend.routes.subsonicRoutes
import com.tzmusic.backend.security.AuthService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.partialcontent.PartialContent
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.nio.file.Files

fun main() {
    val config = AppConfig.fromEnv()
    val catalog = Catalog(LibraryScanner(config.musicDir).scan())
    embeddedServer(Netty, port = config.port, host = config.host, module = { module(catalog, config) })
        .start(wait = true)
}

fun Application.module(catalog: Catalog, config: AppConfig = AppConfig.forTest()) {
    val log = LoggerFactory.getLogger("com.tzmusic.backend.Application")
    config.dbPath.parent?.let { Files.createDirectories(it) }

    val database = Database(config.jdbcUrl)
    database.migrate()

    val users = UserRepository(database)
    val auth = AuthService(users, config)
    val playlists = PlaylistRepository(database)
    playlists.seedDemoIfNeeded()
    catalog.playlistProvider = { query -> playlists.search(query) }

    if (config.jwtSecret == AppConfig.DEFAULT_DEV_SECRET) {
        log.warn("TZMUSIC_JWT_SECRET is not set; using the insecure development secret")
    }

    install(CallLogging)

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = false
                ignoreUnknownKeys = true
                encodeDefaults = true
            },
        )
    }

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHost("localhost:5173")
        allowHost("127.0.0.1:5173")
    }

    install(PartialContent)

    routing {
        healthRoutes(catalog, config)
        authRoutes(auth)
        catalogRoutes(catalog)
        playlistRoutes(catalog, playlists, auth)
        streamRoutes(catalog)
        if (config.subsonicEnabled) {
            subsonicRoutes(catalog, playlists, auth)
        }
    }
}
