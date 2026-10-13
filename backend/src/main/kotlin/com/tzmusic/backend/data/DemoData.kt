package com.tzmusic.backend.data

import com.tzmusic.backend.domain.PlaylistDto
import com.tzmusic.backend.domain.TrackDto

/**
 * Built-in demonstration catalog. All names are fictional and exist only
 * so the API and the web frontend can be developed before any real
 * library is mounted. No audio is shipped for these entries.
 */
object DemoData {
    val gradients = listOf(
        "linear-gradient(135deg, #4b0f2e 0%, #e8115b 100%)",
        "linear-gradient(135deg, #1e3264 0%, #8d67ab 100%)",
        "linear-gradient(135deg, #0d73ec 0%, #1ed760 100%)",
        "linear-gradient(135deg, #7358ff 0%, #b49bff 100%)",
        "linear-gradient(135deg, #ba5d07 0%, #ffd264 100%)",
        "linear-gradient(135deg, #148a08 0%, #66d15f 100%)",
        "linear-gradient(135deg, #8c1932 0%, #e61e32 100%)",
        "linear-gradient(135deg, #056952 0%, #2dd4a7 100%)",
        "linear-gradient(135deg, #27856a 0%, #8bb369 100%)",
        "linear-gradient(135deg, #503750 0%, #a476b4 100%)",
    )

    val tracks: List<TrackDto> = listOf(
        track("t1", "Neon na Bruma", "Aurora Vale", "Cidades Acesas", 222, 0),
        track("t2", "Selva de Concreto", "Aurora Vale", "Cidades Acesas", 245, 0),
        track("t3", "Maré Baixa", "Coletivo Maré", "Água Morna", 198, 7),
        track("t4", "Vento Norte", "Coletivo Maré", "Água Morna", 236, 7),
        track("t5", "Pulso", "Kaya Sol", "Frequência", 178, 1),
        track("t6", "Insônia", "Kaya Sol", "Frequência", 213, 1),
        track("t7", "Ruído Branco", "Duo Ruído", "Sinal", 261, 3),
        track("t8", "Estática", "Duo Ruído", "Sinal", 227, 3),
        track("t9", "Café das Seis", "João Mirante", "Manhãs Lentas", 164, 4),
        track("t10", "Rua Vazia", "João Mirante", "Manhãs Lentas", 192, 4),
        track("t11", "Órbita", "Luma Prisma", "Satélites", 242, 2),
        track("t12", "Gravidade Zero", "Luma Prisma", "Satélites", 209, 2),
        track("t13", "Fogo Fátuo", "Vagalume", "Fogo Fátuo", 231, 6),
        track("t14", "Campo Aberto", "Vagalume", "Fogo Fátuo", 254, 6),
        track("t15", "Batida Antiga", "DJ Sereno", "Cassette Club", 307, 9),
        track("t16", "Fita Rebobinada", "DJ Sereno", "Cassette Club", 273, 9),
        track("t17", "Cordas de Fevereiro", "Orquestra Pequena", "Sessões", 206, 8),
        track("t18", "Sessão da Tarde", "Orquestra Pequena", "Sessões", 288, 8),
    )

    val playlists: List<PlaylistDto> = listOf(
        playlist("p1", "Mix Diário", "Seu mix diário de favoritos. Atualizado hoje.", "t1", "t5", "t11", "t13", "t3", "t15"),
        playlist("p2", "Brasil Indie", "O melhor do indie brasileiro, atualizado toda semana.", "t2", "t4", "t9", "t13", "t17"),
        playlist("p3", "Foco Profundo", "Batidas calmas e constantes para concentrar.", "t7", "t8", "t11", "t12", "t16"),
        playlist("p4", "Chilltarde", "Vibe leve para o fim da tarde.", "t3", "t10", "t14", "t17", "t6"),
        playlist("p5", "Pop de Meia-Noite", "Sintetizadores, luzes e madrugada.", "t5", "t6", "t1", "t12"),
        playlist("p6", "Clássicos da Semana", "Os mais tocados dos últimos sete dias.", "t15", "t16", "t18", "t7", "t8"),
        playlist("p7", "Descobertas da Semana", "Faixas novas escolhidas para você.", "t4", "t8", "t10", "t12", "t14", "t16"),
        playlist("p8", "Em Casa", "Trilhas para dias lentos dentro de casa.", "t9", "t10", "t17", "t18"),
        playlist("p9", "Treino Total", "Energia alta do começo ao fim.", "t15", "t5", "t2", "t13", "t1"),
        playlist("p10", "Sessões Acústicas", "Versões íntimas e cordas ao vivo.", "t17", "t18", "t9", "t3"),
    )

    private fun track(
        id: String,
        title: String,
        artist: String,
        album: String,
        duration: Int,
        gradient: Int,
    ) = TrackDto(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        cover = gradients[gradient],
        streamUrl = null,
        local = false,
    )

    private fun playlist(id: String, name: String, description: String, vararg trackIds: String) =
        PlaylistDto(
            id = id,
            name = name,
            description = description,
            owner = "tz'music",
            trackIds = trackIds.toList(),
        )
}
