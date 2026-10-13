# Architecture

M0 architecture is being derived from public source research before feature implementation.

Initial direction:

- Android app in `android/` with native Kotlin, Compose, Media3, Room, DataStore, WorkManager, Hilt, Ktor Client, and Paging.
- Optional backend in `backend/` for sync, metadata caching, OpenSubsonic compatibility, and self-hosted libraries.
- Provider boundary separates legal built-in sources, user-owned servers, community catalogs, and any opt-in unofficial provider.
- Spotify is metadata import only through official OAuth.

Detailed ADRs will be added before modules are implemented.
