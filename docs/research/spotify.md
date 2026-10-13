# Spotify Official Import Boundary

- Repo / docs estudado: https://developer.spotify.com/documentation/web-api (API docs; no code copied).
- Licença/termos: official developer terms must be reviewed before implementation.
- Pergunta de pesquisa: how can tz'music 07 be complete for Spotify users without protected audio or closed protocol abuse?
- Evidência (arquivo:linha, trecho curto, link permalink): Spotify Web API provides OAuth-based access to user/library/playlist metadata under official terms; protected audio playback is not part of this project's allowed scope.
- Padrão extraído: import metadata only, then resolve tracks through local files, user servers, legal catalogs, and explicit opt-in sources.
- Decisão para o tz'music 07: implement Spotify as `:provider:importer`, not as an audio provider.
- Riscos (legal, manutenção, desempenho): API endpoint availability changes; OAuth token storage; user expectations must be managed in UI copy.
