# Internet Archive

- Repo / commit estudado: https://archive.org/developers (developer docs).
- Licença (e compatibilidade com GPL-3.0): APIs and item content have mixed terms; license must be evaluated per item.
- Pergunta de pesquisa: how can public-domain/Creative Commons audio be discovered and streamed lawfully?
- Evidência (arquivo:linha, trecho curto, link permalink): Internet Archive publishes developer documentation for metadata and item access; endpoint permalinks pending.
- Padrão extraído: treat Archive as metadata-rich but license-mixed; expose license/collection provenance in UI.
- Decisão para o tz'music 07: adapt as a legal provider after adding license filters and tests with fixed public-domain/CC fixtures.
- Riscos (legal, manutenção, desempenho): heterogeneous metadata; large files; content terms vary per item.
