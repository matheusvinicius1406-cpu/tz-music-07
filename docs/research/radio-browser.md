# Radio Browser

- Repo / commit estudado: https://www.radio-browser.info/ and https://github.com/segler-alex/radiobrowser-api (commit not pinned yet).
- Licença (e compatibilidade com GPL-3.0): API/database usage terms and client library license must be pinned before implementation.
- Pergunta de pesquisa: how should the app discover legal radio streams, especially Brazil-first results?
- Evidência (arquivo:linha, trecho curto, link permalink): Radio Browser provides a public internet radio catalog/API with country, tag, codec, and station metadata; exact endpoint permalinks pending.
- Padrão extraído: radio is a legal built-in provider with metadata search, station health, and stream URL playback rather than track resolution.
- Decisão para o tz'music 07: adopt as `:provider:radio`, with Brazil-first filters and backend mirror/cache later.
- Riscos (legal, manutenção, desempenho): station URLs can die; metadata quality varies; app needs retries and station health signals.
