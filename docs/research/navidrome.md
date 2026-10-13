# Navidrome

- Repo / commit estudado: https://github.com/navidrome/navidrome (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): GPL-compatible status to be pinned from upstream `LICENSE`.
- Pergunta de pesquisa: how does a self-hosted music server implement Subsonic/OpenSubsonic, scan libraries, and transcode?
- Evidência (arquivo:linha, trecho curto, link permalink): public repository and documentation identify Navidrome as a self-hosted music server compatible with Subsonic clients; source permalinks pending.
- Padrão extraído: keep library scanning, metadata indexing, auth, transcoding, and API compatibility as separable backend concerns.
- Decisão para o tz'music 07: adapt for backend service boundaries and `:provider:subsonic`; do not copy code.
- Riscos (legal, manutenção, desempenho): transcoding requires ffmpeg hardening; scan performance matters on Raspberry Pi; compatibility quirks require contract tests.
