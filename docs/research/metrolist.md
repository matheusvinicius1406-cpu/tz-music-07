# Metrolist

- Repo / commit estudado: https://github.com/mostafaalagamy/Metrolist (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): GPL-compatible status to be pinned from upstream before implementation decisions.
- Pergunta de pesquisa: how does a Kotlin/Compose/Media3 YouTube Music client organize queue, lyrics, cache, and sync?
- Evidência (arquivo:linha, trecho curto, link permalink): public repository and app positioning show a Kotlin Android music client focused on YouTube Music playback and modern Android UI; file permalinks pending.
- Padrão extraído: separate playback queue state from browse/search screens, and keep cache/sync behavior explicit rather than hidden in UI components.
- Decisão para o tz'music 07: adapt the queue/cache separation for `:core:player` and `:core:data`; reject any protected or ToS-sensitive audio path for Play flavor.
- Riscos (legal, manutenção, desempenho): provider behavior can break when upstream web behavior changes; legal boundary must remain visible to users.
