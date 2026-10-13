# SimpMusic

- Repo / commit estudado: https://github.com/maxrave-dev/SimpMusic (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): GPL-compatible status to be pinned from upstream before code-level study.
- Pergunta de pesquisa: how are synced lyrics and multiple provider integrations organized in a Kotlin Android music app?
- Evidência (arquivo:linha, trecho curto, link permalink): public repository describes an Android music app with synced lyrics and provider integration; source permalinks pending.
- Padrão extraído: treat lyrics as provider capability and cacheable metadata, not as a hard player dependency.
- Decisão para o tz'music 07: adapt into `Capability.LYRICS`, `Lyrics` canonical model, and a dedicated lyrics feature.
- Riscos (legal, manutenção, desempenho): lyrics sources have separate licensing/terms; synced lyric matching can be wrong without duration/timestamp validation.
