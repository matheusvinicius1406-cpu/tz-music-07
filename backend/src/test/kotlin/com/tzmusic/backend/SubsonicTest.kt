package com.tzmusic.backend

import com.tzmusic.backend.data.Catalog
import com.tzmusic.backend.security.AuthService
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

class SubsonicTest {
    private val password = "senha-forte-1"
    private val md5Hex = AuthService.md5Hex(password)
    private val authQuery = "u=testador&p=enc:$md5Hex&v=1.16.1&c=tzmusic-test&f=json"

    private suspend fun io.ktor.client.HttpClient.register(username: String) {
        post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"$username","password":"$password"}""")
        }
    }

    @Test
    fun pingSucceedsWithValidLegacyPassword() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")
        val response = client.get("/rest/ping.view?$authQuery")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"status\":\"ok\""))
        assertTrue(body.contains("\"openSubsonic\":true"))
    }

    @Test
    fun pingFailsWithWrongPassword() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")
        val response = client.get("/rest/ping.view?u=testador&p=enc:00000000000000000000000000000000&f=json")
        val body = response.bodyAsText()
        assertTrue(body.contains("\"status\":\"failed\""))
        assertTrue(body.contains("\"code\":40"))
    }

    @Test
    fun pingFailsWithoutCredentials() = testApplication {
        application { module(Catalog(emptyList())) }
        val response = client.get("/rest/ping.view?f=json")
        assertTrue(response.bodyAsText().contains("\"code\":40"))
    }

    @Test
    fun musicDirectoryWalksArtistAlbumTracks() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")

        val root = client.get("/rest/getMusicDirectory.view?id=root&$authQuery").bodyAsText()
        assertTrue(root.contains("Aurora Vale"))
        assertTrue(root.contains("\"isDir\":true"))

        val artistId = Regex("\"id\":\"(art-[a-f0-9]+)\"").find(root)!!.groupValues[1]
        val albums = client.get("/rest/getMusicDirectory.view?id=$artistId&$authQuery").bodyAsText()
        assertTrue(albums.contains("Cidades Acesas"))

        val albumId = Regex("\"id\":\"(alb-[a-f0-9]+)\"").find(albums)!!.groupValues[1]
        val songs = client.get("/rest/getMusicDirectory.view?id=$albumId&$authQuery").bodyAsText()
        assertTrue(songs.contains("Neon na Bruma"))
        assertTrue(songs.contains("\"suffix\":\"mp3\""))
    }

    @Test
    fun search3ReturnsSongs() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")
        val body = client.get("/rest/search3.view?query=ins&$authQuery").bodyAsText()
        assertTrue(body.contains("searchResult3"))
        assertTrue(body.contains("Insônia"))
    }

    @Test
    fun getPlaylistsReturnsDemoEntries() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")
        val body = client.get("/rest/getPlaylists.view?$authQuery").bodyAsText()
        assertTrue(body.contains("Mix Diário"))
        assertTrue(body.contains("\"songCount\""))
    }

    @Test
    fun streamOfDemoTrackReturnsError70() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")
        val response = client.get("/rest/stream.view?id=t1&$authQuery")
        val body = response.bodyAsText()
        assertTrue(body.contains("\"code\":70"))
    }

    @Test
    fun missingDirectoryReturnsError70() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")
        val body = client.get("/rest/getMusicDirectory.view?id=nao-existe&$authQuery").bodyAsText()
        assertTrue(body.contains("\"code\":70"))
    }

    @Test
    fun corsPreflightStillWorksOnRest() = testApplication {
        application { module(Catalog(emptyList())) }
        client.register("testador")
        val response = client.get("/rest/ping.view?$authQuery") {
            header(HttpHeaders.Origin, "http://localhost:5173")
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
