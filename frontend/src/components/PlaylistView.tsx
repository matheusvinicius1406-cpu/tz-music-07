import type { Playlist, Track } from '../data/mock'
import { formatLongTime } from '../format'
import { IconClock, IconPause, IconPlay } from '../icons'
import { Cover, TrackRow } from './common'

interface PlaylistViewProps {
  playlist: Playlist
  tracksById: Record<string, Track>
  isPlaying: boolean
  currentTrackId: string | null
  likedIds: Set<string>
  onPlayAll: (playlist: Playlist) => void
  onPlayTrack: (trackId: string, contextIds: string[]) => void
  onToggleLike: (trackId: string) => void
}

export function PlaylistView({
  playlist,
  tracksById,
  isPlaying,
  currentTrackId,
  likedIds,
  onPlayAll,
  onPlayTrack,
  onToggleLike,
}: PlaylistViewProps) {
  const list = playlist.trackIds
    .map((id) => tracksById[id])
    .filter((t): t is NonNullable<typeof t> => Boolean(t))
  const total = list.reduce((acc, t) => acc + t.duration, 0)
  const playingHere = isPlaying && list.some((t) => t.id === currentTrackId)

  return (
    <div className="playlist">
      <header
        className="playlist__header"
        style={{
          background: `linear-gradient(180deg, rgba(0,0,0,0.35) 0%, #121212 100%), ${playlist.cover}`,
        }}
      >
        <Cover gradient={playlist.cover} size={232} />
        <div className="playlist__header-meta">
          <span className="playlist__kind">Playlist</span>
          <h1 className="playlist__name">{playlist.name}</h1>
          <p className="playlist__desc">{playlist.description}</p>
          <span className="playlist__stats">
            {playlist.owner} · {list.length} músicas · {formatLongTime(total)}
          </span>
        </div>
      </header>

      <div className="playlist__body">
        <div className="playlist__actions">
          <button
            className="big-play"
            onClick={() => onPlayAll(playlist)}
            disabled={list.length === 0}
            aria-label={playingHere ? 'Pausar' : 'Tocar playlist'}
          >
            {playingHere ? <IconPause size={24} /> : <IconPlay size={24} />}
          </button>
          <button className="pill-btn" disabled>
            Seguir
          </button>
          <button className="pill-btn" disabled>
            ⋯
          </button>
        </div>

        {list.length === 0 ? (
          <div className="empty-state">
            <h3>Nada por aqui ainda</h3>
            <p>Busque músicas e adicione em breve. Esta playlist está esperando faixas.</p>
          </div>
        ) : (
          <div className="track-table track-table--full">
            <div className="track-table__head">
              <span className="track-table__head-idx">#</span>
              <span>Título</span>
              <span>Álbum</span>
              <span className="track-table__head-end">
                <IconClock size={16} />
              </span>
            </div>
            {list.map((t, i) => (
              <TrackRow
                key={t.id}
                index={i}
                track={t}
                isCurrent={currentTrackId === t.id}
                isPlaying={isPlaying}
                liked={likedIds.has(t.id)}
                onPlay={() => onPlayTrack(t.id, playlist.trackIds)}
                onToggleLike={() => onToggleLike(t.id)}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
