package com.tzmusic.backend.data

import com.tzmusic.backend.domain.AlbumDto
import com.tzmusic.backend.domain.AlbumWithTracksDto
import com.tzmusic.backend.domain.ArtistDto
import com.tzmusic.backend.domain.ArtistWithTracksDto
import com.tzmusic.backend.domain.PlaylistDto
import com.tzmusic.backend.domain.SearchResultDto
import com.tzmusic.backend.domain.TrackDto
import com.tzmusic.backend.library.LocalTrack
import java.security.MessageDigest

/**
 * In-memory read model over the demo catalog plus any tracks scanned
 * from the user's music directory. Playlist storage lives in
 * [PlaylistRepository]; the catalog only delegates playlist search to
 * it through [playlistProvider].
 */
class Catalog(localTracks: List<LocalTrack>) {
    private val localById: Map<String, LocalTrack> = localTracks.associateBy { it.track.id }

    val allTracks: List<TrackDto> = DemoData.tracks + localTracks.map { it.track }

    private val tracksById: Map<String, TrackDto> = allTracks.associateBy { it.id }

    var playlistProvider: (String) -> List<PlaylistDto> = { emptyList() }

    fun track(id: String): TrackDto? = tracksById[id]

    fun fileFor(trackId: String) = localById[trackId]?.path

    val localTrackCount: Int get() = localById.size

    fun tracks(query: String?): List<TrackDto> =
        if (query.isNullOrBlank()) allTracks else allTracks.filter { matches(it, query) }

    fun albums(): List<AlbumDto> = deriveAlbums(allTracks)

    fun album(id: String): AlbumWithTracksDto? {
        val group = allTracks.groupBy { albumId(it) }[id] ?: return null
        val album = deriveAlbums(group).firstOrNull() ?: return null
        return AlbumWithTracksDto(album, group)
    }

    fun artists(): List<ArtistDto> = deriveArtists(allTracks)

    fun artist(id: String): ArtistWithTracksDto? {
        val group = allTracks.filter { artistId(it) == id }
        val artist = deriveArtists(group).firstOrNull() ?: return null
        return ArtistWithTracksDto(artist, group)
    }

    fun search(query: String): SearchResultDto {
        val q = normalize(query)
        return SearchResultDto(
            query = query,
            tracks = allTracks.filter { matches(it, q) }.take(20),
            albums = albums().filter { normalize("${it.title} ${it.artist}").contains(q) }.take(10),
            artists = artists().filter { normalize(it.name).contains(q) }.take(10),
            playlists = playlistProvider(q),
        )
    }

    private fun matches(track: TrackDto, query: String): Boolean =
        normalize("${track.title} ${track.artist} ${track.album}").contains(normalize(query))

    private fun deriveAlbums(source: List<TrackDto>): List<AlbumDto> =
        source.groupBy { albumId(it) }
            .map { (id, group) ->
                AlbumDto(
                    id = id,
                    title = group.first().album,
                    artist = group.first().artist,
                    cover = group.first().cover,
                    trackCount = group.size,
                )
            }
            .sortedBy { normalize(it.title) }

    private fun deriveArtists(source: List<TrackDto>): List<ArtistDto> =
        source.groupBy { artistId(it) }
            .map { (id, group) ->
                ArtistDto(
                    id = id,
                    name = group.first().artist,
                    cover = group.first().cover,
                    trackCount = group.size,
                )
            }
            .sortedBy { normalize(it.name) }

    private fun albumId(track: TrackDto): String =
        "alb-" + sha1("${track.album}|${track.artist}").take(10)

    private fun artistId(track: TrackDto): String =
        "art-" + sha1(track.artist).take(10)

    companion object {
        fun normalize(value: String): String =
            java.text.Normalizer.normalize(value.lowercase(), java.text.Normalizer.Form.NFD)
                .replace(Regex("\\p{M}"), "")

        fun sha1(value: String): String =
            MessageDigest.getInstance("SHA-1")
                .digest(value.toByteArray())
                .joinToString("") { "%02x".format(it) }
    }
}
