# OpenSubsonic

- Repo / commit estudado: https://github.com/opensubsonic/open-subsonic-api (commit/spec version not pinned yet).
- Licença (e compatibilidade com GPL-3.0): spec/license must be pinned before embedding generated artifacts.
- Pergunta de pesquisa: what formal API surface should the backend and Android Subsonic provider support?
- Evidência (arquivo:linha, trecho curto, link permalink): OpenSubsonic publishes an open specification for Subsonic-compatible APIs; endpoint-level permalinks pending.
- Padrão extraído: implement compatibility through contract tests against the formal spec, not through one client application's assumptions.
- Decisão para o tz'music 07: adopt as the target contract for `/rest/*` backend and `:provider:subsonic`.
- Riscos (legal, manutenção, desempenho): legacy Subsonic clients vary; authentication and transcoding behavior need compatibility matrices.
