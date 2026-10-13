# MusicBrainz

- Repo / commit estudado: https://musicbrainz.org/doc/MusicBrainz_API and https://github.com/metabrainz/musicbrainz-server (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): MusicBrainz server license and data license must be pinned; API usage policy must be followed.
- Pergunta de pesquisa: how should metadata lookup, ISRC matching, MBID matching, and cover art enrichment work?
- Evidência (arquivo:linha, trecho curto, link permalink): MusicBrainz documents its web service API and rate/user-agent expectations; Cover Art Archive provides artwork by release identifiers.
- Padrão extraído: identify the app with a User-Agent, rate-limit requests per host, and cache metadata locally/backend-side.
- Decisão para o tz'music 07: adopt for metadata enrichment and resolver signals, not as an audio source.
- Riscos (legal, manutenção, desempenho): 1 req/s behavior affects UX; fuzzy matching can create false positives; data licenses require attribution.
