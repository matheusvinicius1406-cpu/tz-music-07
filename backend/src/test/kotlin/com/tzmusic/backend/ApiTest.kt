package com.tzmusic.backend

import com.tzmusic.backend.data.Catalog
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
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

class ApiTest {
    @Test
    fun healthReportsOk() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\":\"ok\""))
    }

    @Test
    fun trackListContainsDemoEntries() = testApplication {
        application { module(Catalog(emptyList())) }
        val body = client.get("/v1/tracks").bodyAsText()
        assertTrue(body.contains("\"id\":\"t1\""))
        assertTrue(body.contains("Neon na Bruma"))
    }

    @Test
    fun searchMatchesArtistIgnoringAccents() = testApplication {
        application { module(Catalog(emptyList())) }
        val body = client.get("/v1/search?q=aurora").bodyAsText()
        assertTrue(body.contains("\"id\":\"t1\""))
        assertTrue(body.contains("\"id\":\"t2\""))
    }

    @Test
    fun searchIncludesDemoPlaylists() = testApplication {
        application { module(Catalog(emptyList())) }
        val body = client.get("/v1/search?q=chilltarde").bodyAsText()
        assertTrue(body.contains("Chilltarde"))
    }

    @Test
    fun unknownTrackReturns404() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.get("/v1/tracks/nope")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("not_found"))
    }

    @Test
    fun demoTrackStreamReturnsClearError() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.get("/v1/stream/t1")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("no_audio"))
    }

    @Test
    fun demoPlaylistsArePubliclyListed() = testApplication {
        application { module(Catalog(emptyList())) }
        val body = client.get("/v1/playlists").bodyAsText()
        assertTrue(body.contains("\"id\":\"p1\""))
        assertTrue(body.contains("\"canEdit\":false"))
    }

    @Test
    fun playlistLifecycleWithAuth() = testApplication {
        application { module(Catalog(emptyList())) }
        val token = client.authToken("donatella")
        val authHeader = "Authorization" to "Bearer $token"

        val created = client.post("/v1/playlists") {
            header(authHeader.first, authHeader.second)
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Minha lista"}""")
        }
        assertEquals(HttpStatusCode.Created, created.status)
        val id = Regex("\"id\":\"(p-[a-f0-9]+)\"").find(created.bodyAsText())!!.groupValues[1]

        val added = client.post("/v1/playlists/$id/tracks") {
            header(authHeader.first, authHeader.second)
            contentType(ContentType.Application.Json)
            setBody("""{"trackId":"t3"}""")
        }
        assertEquals(HttpStatusCode.OK, added.status)
        assertTrue(added.bodyAsText().contains("\"t3\""))

        val renamed = client.put("/v1/playlists/$id") {
            header(authHeader.first, authHeader.second)
            contentType(ContentType.Application.Json)
            setBody("""{"description":"lista testada"}""")
        }
        assertEquals(HttpStatusCode.OK, renamed.status)
        assertTrue(renamed.bodyAsText().contains("lista testada"))

        val removed = client.delete("/v1/playlists/$id/tracks/t3") {
            header(authHeader.first, authHeader.second)
        }
        assertEquals(HttpStatusCode.OK, removed.status)
        assertTrue(!removed.bodyAsText().contains("\"t3\""))

        val deleted = client.delete("/v1/playlists/$id") {
            header(authHeader.first, authHeader.second)
        }
        assertEquals(HttpStatusCode.NoContent, deleted.status)
    }

    @Test
    fun playlistWriteWithoutTokenReturns401() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.post("/v1/playlists") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Anônima"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun demoPlaylistIsReadOnlyEvenWithToken() = testApplication {
        application { module(Catalog(emptyList())) }
        val token = client.authToken("ousada")
        val response = client.put("/v1/playlists/p1") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Hijack"}""")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun otherUsersPlaylistCannotBeEdited() = testApplication {
        application { module(Catalog(emptyList())) }
        val ownerToken = client.authToken("alice")
        val created = client.post("/v1/playlists") {
            header(HttpHeaders.Authorization, "Bearer $ownerToken")
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Privada"}""")
        }
        val id = Regex("\"id\":\"(p-[a-f0-9]+)\"").find(created.bodyAsText())!!.groupValues[1]

        val intruderToken = client.authToken("eve")
        val response = client.put("/v1/playlists/$id") {
            header(HttpHeaders.Authorization, "Bearer $intruderToken")
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Roubada"}""")
        }
        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun playlistAddUnknownTrackReturns404() = testApplication {
        application { module(Catalog(emptyList())) }
        val token = client.authToken("testador")
        val response = client.post("/v1/playlists/p1/tracks") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""{"trackId":"ghost"}""")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun corsAllowsFrontendOrigin() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.get("/health") {
            header(HttpHeaders.Origin, "http://localhost:5173")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("http://localhost:5173", response.headers[HttpHeaders.AccessControlAllowOrigin])
    }
}
