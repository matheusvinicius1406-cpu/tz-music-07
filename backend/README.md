# Backend

Optional self-hosted backend for tz'music: sync, metadata caching, OpenSubsonic compatibility, and streaming of user-owned files.

## Status

**MVP + auth/persistence + OpenSubsonic implemented**: in-memory catalog with local-library scanner and byte-range streaming; JWT auth (`register`/`login`/`me`, PBKDF2 passwords); SQLite + Flyway persistence with per-user playlist CRUD (demo playlists are read-only); OpenSubsonic `/rest/*.view` compatibility (JSON mode) behind a config flag, with contract tests.

## Planned stack

- Kotlin 2.x and Ktor 3 (Netty engine), built with Gradle (Kotlin DSL).
- kotlinx.serialization for JSON via Ktor Content Negotiation.
- Auth: JWT access tokens issued by `POST /v1/auth/login`; Subsonic token auth for the `/rest` surface.
- Database: SQLite by default, PostgreSQL optional; schema migrations with Flyway.
- Config via environment variables (`TZMUSIC_*`) with sane defaults for local development.

## How to run (MVP)

```bash
cd backend
TZMUSIC_MUSIC_DIR=/caminho/para/sua/musica ./gradlew run   # Windows: set TZMUSIC_MUSIC_DIR e rode gradlew.bat run
./gradlew test                                             # API tests
```

Environment variables: `TZMUSIC_PORT` (default `8080`), `TZMUSIC_HOST` (default `0.0.0.0`), `TZMUSIC_MUSIC_DIR` (default `./music`; scanned for `mp3`, `flac`, `ogg`, `opus`, `m4a`, `aac`, `wav`, `webm`), `TZMUSIC_DB` (default `data/tzmusic.db`), `TZMUSIC_JWT_SECRET` (default is an insecure dev secret — always override in production), `TZMUSIC_TOKEN_TTL_MINUTES` (default `1440`), `TZMUSIC_SUBSONIC_ENABLED` (default `true`; set `false` to disable the `/rest` surface). When the music directory is missing, the server still starts and serves only the built-in demonstration catalog (fictional entries without audio).

Auth and playlists: `POST /v1/auth/register` (username 3–32 chars `[A-Za-z0-9_.-]`, password min 8), `POST /v1/auth/login` → JWT Bearer token, `GET /v1/me`. Reads on `/v1/playlists` are public; `POST`/`PUT`/`DELETE` and track membership require the owner's token (demo playlists are read-only → 403).

### OpenSubsonic surface (`/rest/*.view`, JSON mode)

Read-only subset implemented from the public OpenSubsonic specification and covered by contract tests: `ping.view`, `getLicense.view`, `getMusicFolders.view`, `getIndexes.view`, `getArtists.view`, `getMusicDirectory.view` (artist → album → track), `search3.view`, `getPlaylists.view`, `stream.view` (byte-range; demonstration tracks return error `70`).

Authentication follows the legacy scheme `p=enc:<md5hex(password)>`, compared against an MD5 stored at registration (migration `V2`). The newer `t`/`s` token scheme requires the plaintext password and is therefore not supported yet — this trade-off is documented because Subsonic clients cannot produce PBKDF2-based tokens, and reversible credential storage is out of scope for now. Point clients at `http://SEU-SERVIDOR:8080/rest/` with a registered username/password.

The web `frontend/` dev server already proxies `/api/*` here (`/api/health` → `:8080/health`, `/api/v1/...` → `:8080/v1/...`).

## Planned module layout

```
backend/
  src/main/kotlin/com/tzmusic/backend/
    Application.kt        # entry point, engine, port 8080
    plugins/              # CORS, serialization, auth, status pages, metrics
    routes/               # v1 REST + OpenSubsonic /rest adapters
    domain/               # Track, Album, Artist, Playlist entities
    data/                 # repositories, DAOs, migrations
    library/              # filesystem scanner + metadata reader
    stream/               # HTTP byte-range streaming of local files
```

## Planned API surface (v1)

- `GET /health` — liveness/readiness probe used by docker/CI.
- `POST /v1/auth/login`, `GET /v1/me` — JWT issue and verification.
- `GET /v1/tracks`, `/v1/albums`, `/v1/artists`, `/v1/search` — catalog reads.
- `GET|POST|PUT|DELETE /v1/playlists` — playlist CRUD and track membership.
- `GET /v1/stream/{trackId}` — HTTP byte-range streaming; local/user-owned files only.
- OpenSubsonic `/rest/*.view` endpoints (`ping`, `getMusicDirectory`, `search3`, ...) behind a config flag, for Navidrome-compatible clients.

JSON contracts live in `shared/`. The web `frontend/` proxies `/api` to this service in development (Vite on 5173 -> backend on 8080).

## Storage

- Library roots are user-configured directories mounted into the container (read-only).
- Metadata cache and user data live under a single volume (`/data`), SQLite file by default.

## Legal boundaries

- Streams only files the user already owns and pointed the server at.
- No DRM circumvention, no Spotify audio fetching, no closed-protocol reimplementation.
- Spotify stays metadata import only, through official OAuth, on the client side.

## References

See `docs/ARCHITECTURE.md`, `docs/PROVIDER_GUIDE.md`, and the research notes `docs/research/navidrome.md` and `docs/research/opensubsonic.md`.
