package com.tzmusic.backend.domain

import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val cover: String,
    val streamUrl: String? = null,
    val local: Boolean = false,
)

@Serializable
data class AlbumDto(
    val id: String,
    val title: String,
    val artist: String,
    val cover: String,
    val trackCount: Int,
)

@Serializable
data class ArtistDto(
    val id: String,
    val name: String,
    val cover: String,
    val trackCount: Int,
)

@Serializable
data class AlbumWithTracksDto(
    val album: AlbumDto,
    val tracks: List<TrackDto>,
)

@Serializable
data class ArtistWithTracksDto(
    val artist: ArtistDto,
    val tracks: List<TrackDto>,
)

@Serializable
data class PlaylistDto(
    val id: String,
    val name: String,
    val description: String,
    val owner: String,
    val trackIds: List<String>,
    val canEdit: Boolean = false,
)

@Serializable
data class NewPlaylistRequest(
    val name: String,
    val description: String = "",
)

@Serializable
data class UpdatePlaylistRequest(
    val name: String? = null,
    val description: String? = null,
    val trackIds: List<String>? = null,
)

@Serializable
data class AddTrackRequest(
    val trackId: String,
)

@Serializable
data class SearchResultDto(
    val query: String,
    val tracks: List<TrackDto>,
    val albums: List<AlbumDto>,
    val artists: List<ArtistDto>,
    val playlists: List<PlaylistDto>,
)

@Serializable
data class HealthDto(
    val status: String,
    val version: String,
    val musicDir: String,
    val localTracks: Int,
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String,
)

@Serializable
data class RegisterRequest(
    val username: String = "",
    val password: String = "",
)

@Serializable
data class LoginRequest(
    val username: String = "",
    val password: String = "",
)

@Serializable
data class TokenResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val userId: String,
    val username: String,
)

@Serializable
data class UserDto(
    val id: String,
    val username: String,
)
