package com.tzmusic.backend.library

import com.tzmusic.backend.data.Catalog
import com.tzmusic.backend.data.DemoData
import com.tzmusic.backend.domain.TrackDto
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.nameWithoutExtension

data class LocalTrack(val track: TrackDto, val path: Path)

/**
 * Scans a user-owned directory for audio files and maps them into
 * catalog entries. Metadata extraction is intentionally minimal in the
 * MVP: the "Artist - Title" filename convention plus folder structure
 * ("Artista/Álbum/faixa.mp3"). Tag parsing lands in a later milestone.
 */
class LibraryScanner(private val root: Path) {
    private val log = LoggerFactory.getLogger(LibraryScanner::class.java)

    fun scan(): List<LocalTrack> {
        if (!Files.isDirectory(root)) {
            log.info("Music dir {} not found; serving catalog without local tracks", root)
            return emptyList()
        }
        val found = Files.walk(root).use { stream ->
            stream.filter { it.isRegularFile() }
                .filter { it.extension.lowercase() in AUDIO_EXTENSIONS }
                .map { toLocalTrack(it) }
                .toList()
        }
        log.info("Scanned {} local audio file(s) under {}", found.size, root)
        return found
    }

    private fun toLocalTrack(path: Path): LocalTrack {
        val relative = root.toAbsolutePath().normalize().relativize(path.toAbsolutePath().normalize())
        val segments = relative.map { it.toString() }
        val (folderArtist, folderAlbum) = when {
            segments.size >= 3 -> segments[segments.size - 3] to segments[segments.size - 2]
            segments.size == 2 -> "Desconhecido" to segments[0]
            else -> "Desconhecido" to "Arquivos locais"
        }
        val rawName = path.nameWithoutExtension
        val (artist, title) = if (rawName.contains(" - ")) {
            val parts = rawName.split(" - ", limit = 2)
            parts[0].trim() to parts[1].trim()
        } else {
            folderArtist to rawName.trim()
        }
        val id = "f-" + Catalog.sha1(path.toAbsolutePath().normalize().toString()).take(12)
        val gradient = DemoData.gradients[(Catalog.sha1(id).hashCode().mod(DemoData.gradients.size) + DemoData.gradients.size) % DemoData.gradients.size]
        return LocalTrack(
            track = TrackDto(
                id = id,
                title = title,
                artist = artist,
                album = folderAlbum,
                duration = 0,
                cover = gradient,
                streamUrl = "/v1/stream/$id",
                local = true,
            ),
            path = path,
        )
    }

    companion object {
        val AUDIO_EXTENSIONS = setOf("mp3", "flac", "ogg", "opus", "m4a", "aac", "wav", "webm")
    }
}
