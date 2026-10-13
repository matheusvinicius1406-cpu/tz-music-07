# tz'music 07

Android-first, offline-first music app and optional self-hosted backend.

The project is open source, GPL-3.0-only, privacy-first, and designed around legal/free catalogs, local libraries, user-owned servers, radio, podcasts, independent artists, and official metadata importers.

## Status

M0 research/bootstrap is in progress. No product feature has been implemented yet.

## Repository Layout

- `android/`: native Android project.
- `backend/`: optional self-hosted Ktor backend.
- `shared/`: shared contracts and DTOs planned for Kotlin Multiplatform.
- `docs/`: architecture, legal/privacy notes, ADRs, and research evidence.
- `docker/`: deployment assets for the backend.
- `scripts/`: local automation.

## Legal Boundaries

Spotify support is limited to official OAuth metadata import. This project will not fetch protected Spotify audio, bypass DRM, extract official app secrets, or reimplement closed protocols for unauthorized playback.
