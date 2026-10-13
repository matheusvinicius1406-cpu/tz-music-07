package com.tzmusic.backend.routes

import com.tzmusic.backend.domain.ErrorResponse
import com.tzmusic.backend.domain.LoginRequest
import com.tzmusic.backend.domain.RegisterRequest
import com.tzmusic.backend.domain.UserDto
import com.tzmusic.backend.security.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(auth: AuthService) {
    route("/v1/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            if (!auth.registerValid(request)) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        "invalid_request",
                        "Username must be 3-32 chars (letters, digits, '_', '.', '-') and password at least ${AuthService.MIN_PASSWORD_LENGTH} chars",
                    ),
                )
                return@post
            }
            val user = auth.register(request)
            if (user == null) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("username_taken", "Username already exists"))
                return@post
            }
            call.respond(HttpStatusCode.Created, user)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val token = auth.login(request)
            if (token == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse("invalid_credentials", "Wrong username or password"),
                )
                return@post
            }
            call.respond(token)
        }
    }

    route("/v1") {
        get("/me") {
            val user = call.authUser(auth)
            if (user == null) {
                call.respondUnauthorized()
                return@get
            }
            call.respond(UserDto(id = user.id, username = user.username))
        }
    }
}
