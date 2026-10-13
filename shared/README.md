# Shared

Planned Kotlin Multiplatform module with the contracts shared between `android/` and `backend/`.

## Status

No shared model has been implemented yet.

## Planned contents

- DTOs serialized with kotlinx.serialization: `TrackDto`, `AlbumDto`, `ArtistDto`, `PlaylistDto`, `SearchResultDto`, `UserDto`.
- Versioned contract (`v1`) mirrored by the backend REST routes and consumed as plain JSON by `frontend/`.
- Small pure-Kotlin helpers safe on every platform (pagination params, ID types, time formatting).

## Rules

- DTOs stay transport-only: no Android or database types leak in.
- Once `v1` ships, changes are additive and backward compatible.
