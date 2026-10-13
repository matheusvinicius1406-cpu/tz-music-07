# NewPipeExtractor

- Repo / commit estudado: https://github.com/TeamNewPipe/NewPipeExtractor (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): GPL-3.0-family project; pin exact upstream license before dependency decisions.
- Pergunta de pesquisa: how does a Java/Kotlin extractor isolate services and regression-test website changes?
- Evidência (arquivo:linha, trecho curto, link permalink): public repository is a standalone extractor used by NewPipe, organized around services/extractors and tests; exact file permalinks pending.
- Padrão extraído: isolate unofficial extraction behind service-specific modules with regression tests that detect upstream website changes.
- Decisão para o tz'music 07: only consider this for the optional `full` flavor, disabled by default, isolated from legal providers.
- Riscos (legal, manutenção, desempenho): third-party terms risk; high maintenance churn; must never be included in Play flavor or required by core modules.
