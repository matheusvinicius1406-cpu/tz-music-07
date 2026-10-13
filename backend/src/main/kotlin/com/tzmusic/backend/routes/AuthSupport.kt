package com.tzmusic.backend.routes

import com.tzmusic.backend.domain.ErrorResponse
import com.tzmusic.backend.security.AuthService
import com.tzmusic.backend.security.AuthUser
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

/**
 * Bearer-token helper: routes call [authUser] and reject anonymous
 * requests with401 when a mutation requires an account.
 */
suspend fun ApplicationCall.authUser(auth: AuthService): AuthUser? {
    val header = request.headers[HttpHeaders.Authorization] ?: return null
    if (!header.startsWith("Bearer ")) return null
    return auth.verify(header.removePrefix("Bearer ").trim())
}

suspend fun ApplicationCall.respondUnauthorized(): Unit =
    respond(HttpStatusCode.Unauthorized, ErrorResponse("unauthorized", "A valid Bearer token is required"))
