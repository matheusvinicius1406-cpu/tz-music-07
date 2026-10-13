# Docker

Planned container assets for the optional backend.

## Planned layout

- `docker/docker-compose.yml` — single-service stack:
  - `backend` built from `backend/` (image `tzmusic/backend`), port `8080:8080`.
  - volume `tzmusic-data:/data` for the SQLite database and metadata cache.
  - read-only bind mount of the user's music root, e.g. `~/Music:/media:ro`.
  - env: `TZMUSIC_DB=jdbc:sqlite:/data/tzmusic.db`, `TZMUSIC_SUBSONIC_ENABLED=true`.
- `docker/Dockerfile` — multi-stage build (Gradle wrapper -> JRE 21 runtime image).

No image is built yet; this pins the contract used by `backend/README.md`.
