package com.tzmusic.backend

import com.tzmusic.backend.data.Catalog
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthTest {
    @Test
    fun registerLoginAndMeFlow() = testApplication {
        application { module(Catalog(emptyList())) }

        val registered = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"marcelo","password":"senha-forte-1"}""")
        }
        assertEquals(HttpStatusCode.Created, registered.status)
        assertTrue(registered.bodyAsText().contains("\"username\":\"marcelo\""))

        val login = client.post("/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"marcelo","password":"senha-forte-1"}""")
        }
        assertEquals(HttpStatusCode.OK, login.status)
        val token = Regex("\"token\":\"([^\"]+)\"").find(login.bodyAsText())!!.groupValues[1]

        val me = client.get("/v1/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, me.status)
        assertTrue(me.bodyAsText().contains("marcelo"))
    }

    @Test
    fun duplicateUsernameReturns409() = testApplication {
        application { module(Catalog(emptyList())) }
        client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"repetido","password":"senha-forte-1"}""")
        }
        val second = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"repetido","password":"outra-senha-2"}""")
        }
        assertEquals(HttpStatusCode.Conflict, second.status)
    }

    @Test
    fun weakPasswordReturns400() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"fraco","password":"123"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun wrongPasswordReturns401() = testApplication {
        application { module(Catalog(emptyList())) }
        client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"segredo","password":"senha-forte-1"}""")
        }
        val response = client.post("/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"segredo","password":"senha-errada-9"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun meWithoutTokenReturns401() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.get("/v1/me")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun forgedTokenReturns401() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.get("/v1/me") {
            header(HttpHeaders.Authorization, "Bearer token-fabricado.bob.assinatura")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
