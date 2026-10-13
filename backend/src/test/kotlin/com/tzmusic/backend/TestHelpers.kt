package com.tzmusic.backend

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

/** Registers (ignoring409) and logs in, returning a Bearer token. */
suspend fun HttpClient.authToken(username: String): String {
    post("/v1/auth/register") {
        contentType(ContentType.Application.Json)
        setBody("""{"username":"$username","password":"senha-forte-1"}""")
    }
    val login = post("/v1/auth/login") {
        contentType(ContentType.Application.Json)
        setBody("""{"username":"$username","password":"senha-forte-1"}""")
    }
    return Regex("\"token\":\"([^\"]+)\"").find(login.bodyAsText())!!.groupValues[1]
}
