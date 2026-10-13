# Echo

- Repo / commit estudado: https://github.com/brahmkshatriya/echo (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): GPL-compatible status must be pinned from upstream `LICENSE` before code-level reuse.
- Pergunta de pesquisa: how does a native Android music app isolate source extensions and expose provider capabilities?
- Evidência (arquivo:linha, trecho curto, link permalink): public repo and docs indicate an Android music player organized around extensions; source permalinks pending.
- Padrão extraído: keep source integrations behind a stable extension/provider contract, with lifecycle and capability declarations separate from UI.
- Decisão para o tz'music 07: adapt the extension idea into `:core:provider-api` plus DI-selected providers. Do not copy implementation.
- Riscos (legal, manutenção, desempenho): extension APIs can become unstable; untrusted plugins increase privacy/security risk; license must be pinned before deeper reuse.
