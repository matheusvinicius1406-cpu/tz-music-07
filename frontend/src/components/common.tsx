import type { Track } from '../data/mock'
import { IconMusicNote } from '../icons'

interface CoverProps {
  gradient: string
  label?: string
  size?: number | string
  round?: boolean
}

export function Cover({ gradient, label, size = 48, round = false }: CoverProps) {
  return (
    <div
      className={`cover${round ? ' cover--round' : ''}`}
      style={{ background: gradient, width: size, height: size }}
    >
      {label ? <span className="cover__label">{label}</span> : <IconMusicNote size={20} className="cover__icon" />}
    </div>
  )
}

interface PlayFabProps {
  visible: boolean
  playing: boolean
  onClick: () => void
}

export function PlayFab({ visible, playing, onClick }: PlayFabProps) {
  return (
    <button
      className={`play-fab${visible ? ' play-fab--visible' : ''}`}
      onClick={(e) => {
        e.stopPropagation()
        onClick()
      }}
      aria-label={playing ? 'Pausar' : 'Tocar'}
    >
      {playing ? (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" aria-hidden>
          <rect x="6" y="4.5" width="4" height="15" rx="1" />
          <rect x="14" y="4.5" width="4" height="15" rx="1" />
        </svg>
      ) : (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" aria-hidden>
          <path d="M7.5 4.8 19 12 7.5 19.2V4.8Z" />
        </svg>
      )}
    </button>
  )
}

export function TrackRow({
  index,
  track,
  isCurrent,
  isPlaying,
  liked,
  onPlay,
  onToggleLike,
}: {
  index: number
  track: Track
  isCurrent: boolean
  isPlaying: boolean
  liked: boolean
  onPlay: () => void
  onToggleLike: () => void
}) {
  return (
    <div
      className={`track-row${isCurrent ? ' track-row--current' : ''}`}
      onDoubleClick={onPlay}
      role="row"
    >
      <div className="track-row__idx">
        <span className="track-row__num">{index + 1}</span>
        <button className="track-row__play" onClick={onPlay} aria-label={`Tocar ${track.title}`}>
          {isCurrent && isPlaying ? (
            <span className="eq-bars" aria-hidden>
              <i /><i /><i />
            </span>
          ) : (
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" aria-hidden>
              <path d="M7.5 4.8 19 12 7.5 19.2V4.8Z" />
            </svg>
          )}
        </button>
      </div>
      <div className="track-row__main">
        <div className="cover cover--sm" style={{ background: track.cover }} />
        <div className="track-row__meta">
          <span className={`track-row__title${isCurrent ? ' is-green' : ''}`}>{track.title}</span>
          <span className="track-row__artist">{track.artist}</span>
        </div>
      </div>
      <div className="track-row__album">{track.album}</div>
      <div className="track-row__end">
        <button
          className={`heart-btn${liked ? ' is-liked' : ''}`}
          onClick={onToggleLike}
          aria-label={liked ? 'Remover dos favoritos' : 'Adicionar aos favoritos'}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill={liked ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="1.8" aria-hidden>
            <path d="M12 20.7c-.3 0-.6-.1-.8-.3C7.4 17.5 3 14 3 9.4 3 6.7 5 4.7 7.6 4.7c1.6 0 3.1.8 4.4 2.3 1.3-1.5 2.8-2.3 4.4-2.3C19 4.7 21 6.7 21 9.4c0 4.6-4.4 8.1-8.2 11-.2.2-.5.3-.8.3Z" />
          </svg>
        </button>
        <span className="track-row__dur">{formatDur(track.duration)}</span>
      </div>
    </div>
  )
}

function formatDur(seconds: number): string {
  const m = Math.floor(seconds / 60)
  const s = (seconds % 60).toString().padStart(2, '0')
  return `${m}:${s}`
}
