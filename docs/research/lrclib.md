# LRCLIB

- Repo / commit estudado: https://github.com/tranxuanthang/lrclib and https://lrclib.net/docs (commit/API version not pinned yet).
- Licença (e compatibilidade com GPL-3.0): upstream project/content/API terms must be pinned before implementation.
- Pergunta de pesquisa: how can synced lyrics be fetched and matched by title, artist, album, and duration?
- Evidência (arquivo:linha, trecho curto, link permalink): LRCLIB publishes docs for synced and plain lyrics lookup; exact endpoint permalinks pending.
- Padrão extraído: lyrics matching should include duration and metadata normalization, and the result should remain nullable.
- Decisão para o tz'music 07: adopt as a lyrics provider behind `Capability.LYRICS`, with cache and manual correction later.
- Riscos (legal, manutenção, desempenho): lyric copyright/terms; wrong matches; offline cache retention policy.
