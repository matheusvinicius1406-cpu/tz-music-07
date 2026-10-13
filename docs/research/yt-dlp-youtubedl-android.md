# yt-dlp / youtubedl-android

- Repo / commit estudado: https://github.com/yt-dlp/yt-dlp and https://github.com/yausername/youtubedl-android (commits not pinned yet).
- Licença (e compatibilidade com GPL-3.0): yt-dlp uses a permissive public-domain style license; youtubedl-android license must be pinned before dependency decisions.
- Pergunta de pesquisa: how can extractor binaries be embedded, updated, and isolated on Android?
- Evidência (arquivo:linha, trecho curto, link permalink): yt-dlp is a maintained downloader/extractor project; youtubedl-android packages youtube-dl style tooling for Android; exact file permalinks pending.
- Padrão extraído: if used, binary/extractor updates need signature verification, version pinning, rollback, and clear user warnings.
- Decisão para o tz'music 07: only evaluate for `:provider:unofficial-youtube` in the `full` flavor after legal warning and isolation ADR.
- Riscos (legal, manutenção, desempenho): high ToS/legal risk; binary size; update supply-chain risk; stream URL expiration.
