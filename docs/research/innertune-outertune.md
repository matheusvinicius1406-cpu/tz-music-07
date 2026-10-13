# InnerTune / OuterTune

- Repo / commit estudado: https://github.com/z-huang/InnerTune and https://github.com/DD3Boh/OuterTune (commits not pinned yet).
- Licença (e compatibilidade com GPL-3.0): GPL-compatible status to be pinned from each upstream `LICENSE`.
- Pergunta de pesquisa: what Compose + Media3 + Room architecture patterns are useful for local library, playback, cache, and player UI?
- Evidência (arquivo:linha, trecho curto, link permalink): public repositories present Android music clients with Compose UI, playback, downloads/cache, and local persistence; exact file permalinks pending.
- Padrão extraído: persist playback/library state independently from the player screen, and let player UI observe canonical state instead of owning it.
- Decisão para o tz'music 07: adapt the state ownership pattern for `:core:player`, `:core:database`, and feature modules.
- Riscos (legal, manutenção, desempenho): unofficial provider coupling must not leak into legal providers; GPL compatibility still needs exact license confirmation.
