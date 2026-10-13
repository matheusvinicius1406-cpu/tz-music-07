# ListenBrainz

- Repo / commit estudado: https://github.com/metabrainz/listenbrainz-server (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): license and API terms must be pinned before implementation.
- Pergunta de pesquisa: how can opt-in scrobbling and open recommendations work without default telemetry?
- Evidência (arquivo:linha, trecho curto, link permalink): ListenBrainz publishes server source and APIs for listens/recommendation workflows; endpoint permalinks pending.
- Padrão extraído: scrobbling is an explicit account integration, separate from local history and disabled by default.
- Decisão para o tz'music 07: adopt as opt-in metadata/recommendation provider; never enable background submission without consent.
- Riscos (legal, manutenção, desempenho): privacy; account token storage; recommendation quality depends on metadata matching.
