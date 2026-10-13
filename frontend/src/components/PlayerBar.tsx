import type { Track } from '../data/mock'
import { formatTime } from '../format'
import { IconHeart, IconNext, IconPause, IconPlay, IconPrev, IconQueue, IconRepeat, IconRepeatOne, IconShuffle, IconVolume } from '../icons'

export type RepeatMode = 'off' | 'all' | 'one'

interface PlayerBarProps {
  track: Track | null
  isPlaying: boolean
  progress: number
  volume: number
  shuffle: boolean
  repeat: RepeatMode
  liked: boolean
  onTogglePlay: () => void
  onNext: () => void
  onPrev: () => void
  onSeek: (seconds: number) => void
  onVolume: (v: number) => void
  onShuffle: () => void
  onRepeat: () => void
  onToggleLike: () => void
}

export function PlayerBar({
  track,
  isPlaying,
  progress,
  volume,
  shuffle,
  repeat,
  liked,
  onTogglePlay,
  onNext,
  onPrev,
  onSeek,
  onVolume,
  onShuffle,
  onRepeat,
  onToggleLike,
}: PlayerBarProps) {
  const duration = track?.duration ?? 0
  const pct = duration > 0 ? Math.min(100, (progress / duration) * 100) : 0

  return (
    <footer className="player" aria-label="Player">
      <div className="player__left">
        {track ? (
          <>
            <div className="cover cover--md" style={{ background: track.cover }} />
            <div className="player__meta">
              <span className="player__title">{track.title}</span>
              <span className="player__artist">{track.artist}</span>
            </div>
            <button
              className={`heart-btn${liked ? ' is-liked' : ''}`}
              onClick={onToggleLike}
              aria-label={liked ? 'Remover dos favoritos' : 'Adicionar aos favoritos'}
            >
              <IconHeart size={16} filled={liked} />
            </button>
          </>
        ) : (
          <span className="player__idle">Escolha uma música para começar</span>
        )}
      </div>

      <div className="player__center">
        <div className="player__controls">
          <button
            className={`ctrl-btn${shuffle ? ' is-on' : ''}`}
            onClick={onShuffle}
            aria-label="Aleatório"
          >
            <IconShuffle size={18} />
          </button>
          <button className="ctrl-btn" onClick={onPrev} disabled={!track} aria-label="Anterior">
            <IconPrev size={20} />
          </button>
          <button
            className="play-btn"
            onClick={onTogglePlay}
            disabled={!track}
            aria-label={isPlaying ? 'Pausar' : 'Tocar'}
          >
            {isPlaying ? <IconPause size={20} /> : <IconPlay size={20} />}
          </button>
          <button className="ctrl-btn" onClick={onNext} disabled={!track} aria-label="Próxima">
            <IconNext size={20} />
          </button>
          <button
            className={`ctrl-btn${repeat !== 'off' ? ' is-on' : ''}`}
            onClick={onRepeat}
            aria-label="Repetir"
          >
            {repeat === 'one' ? <IconRepeatOne size={18} /> : <IconRepeat size={18} />}
          </button>
        </div>

        <div className="player__progress">
          <span className="player__time">{formatTime(progress)}</span>
          <input
            className="slider"
            type="range"
            min={0}
            max={duration || 100}
            step={1}
            value={Math.min(progress, duration || 0)}
            onChange={(e) => onSeek(Number(e.target.value))}
            style={{ background: `linear-gradient(to right, #fff ${pct}%, #4d4d4d ${pct}%)` }}
            aria-label="Posição da música"
          />
          <span className="player__time">{formatTime(duration)}</span>
        </div>
      </div>

      <div className="player__right">
        <button className="ctrl-btn" disabled title="Fila em breve" aria-label="Fila">
          <IconQueue size={18} />
        </button>
        <IconVolume size={18} muted={volume === 0} className="player__vol-icon" />
        <input
          className="slider slider--vol"
          type="range"
          min={0}
          max={100}
          step={1}
          value={Math.round(volume * 100)}
          onChange={(e) => onVolume(Number(e.target.value) / 100)}
          style={{
            background: `linear-gradient(to right, #fff ${volume * 100}%, #4d4d4d ${volume * 100}%)`,
          }}
          aria-label="Volume"
        />
      </div>
    </footer>
  )
}
