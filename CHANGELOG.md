# Changelog

## Unreleased

- Add OpenSubsonic `/rest/*.view` compatibility (JSON mode) behind `TZMUSIC_SUBSONIC_ENABLED`: ping, license, music folders, indexes/artists, music directory walk, search3, playlists, and byte-range stream; legacy `p=enc:<md5hex>` auth with contract tests.
- Add login/register screen to the web frontend: session persisted in `localStorage`, Bearer token attached to playlist writes, user chip + logout in the top bar.
- Add auth and persistence to the backend: JWT register/login/me with PBKDF2 passwords, SQLite + Flyway migrations, per-user playlist CRUD with owner checks and read-only demo playlists.
- Wire the web frontend to the backend API (catalog, playlists, auth, real audio streaming for local tracks).
- Implement the backend MVP (Ktor 3 + Kotlin): in-memory catalog with demo data, local-library scanner, byte-range streaming, playlist CRUD, CORS for the web frontend, and API tests.
- Add backend CI job (Gradle tests on JDK 21).
- Add the planned backend contract (stack, module layout, API surface, storage) in `backend/README.md`.
- Document planned Docker assets and shared KMP contracts.
- Add web frontend prototype in `frontend/` (React + Vite), dark streaming-UI style with hot reload.
- Bootstrap repository structure for M0 research.
- Move the starter Android project into `android/`.
- Add initial Gradle version catalog entries for the planned Android stack.
- Add CI skeleton and initial research documents.
