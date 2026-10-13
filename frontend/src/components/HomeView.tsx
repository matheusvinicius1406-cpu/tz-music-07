import { albums, artists, type Playlist, type Track } from '../data/mock'
import { formatLongTime } from '../format'
import { Cover, PlayFab, TrackRow } from './common'

function greeting(): string {
  const h = new Date().getHours()
  if (h < 6) return 'Boa madrugada'
  if (h < 12) return 'Bom dia'
  if (h < 18) return 'Boa tarde'
  return 'Boa noite'
}

interface HomeViewProps {
  playlists: Playlist[]
  tracks: Track[]
  likedIds: Set<string>
  currentTrackId: string | null
  isPlaying: boolean
  apiOnline: boolean
  localTrackCount: number
  onOpenPlaylist: (id: string) => void
  onPlayTrack: (trackId: string, contextIds: string[]) => void
  onPlayPlaylist: (playlist: Playlist) => void
  onToggleLike: (trackId: string) => void
}

export function HomeView({
  playlists,
  tracks,
  likedIds,
  currentTrackId,
  isPlaying,
  apiOnline,
  localTrackCount,
  onOpenPlaylist,
  onPlayTrack,
  onPlayPlaylist,
  onToggleLike,
}: HomeViewProps) {
  const popular = tracks.slice(0, 5)
  const popularIds = popular.map((t) => t.id)

  return (
    <div className="home">
      <section className="shelf">
        <h2 className="shelf__title">{greeting()}</h2>
        <div className="card-grid card-grid--wide">
          {playlists.slice(0, 6).map((p) => (
            <div key={p.id} className="card card--playlist" onClick={() => onOpenPlaylist(p.id)}>
              <div className="card__cover-wrap">
                <Cover gradient={p.cover} size="100%" />
                <PlayFab
                  visible
                  playing={isPlaying && p.trackIds.includes(currentTrackId ?? '')}
                  onClick={() => onPlayPlaylist(p)}
                />
              </div>
              <span className="card__title">{p.name}</span>
              <span className="card__sub">{p.description}</span>
            </div>
          ))}
        </div>
      </section>

      <section className="shelf">
        <div className="shelf__head">
          <h2 className="shelf__title">Artistas em destaque</h2>
        </div>
        <div className="card-grid">
          {artists.map((a) => (
            <div key={a.id} className="card card--artist">
              <div className="card__cover-wrap">
                <Cover gradient={a.cover} size="100%" round />
              </div>
              <span className="card__title">{a.name}</span>
              <span className="card__sub">
                {a.monthlyListeners.toLocaleString('pt-BR')} ouvintes mensais
              </span>
            </div>
          ))}
        </div>
      </section>

      <section className="shelf">
        <div className="shelf__head">
          <h2 className="shelf__title">Lançamentos recentes</h2>
        </div>
        <div className="card-grid">
          {albums.map((al) => (
            <div key={al.id} className="card">
              <div className="card__cover-wrap">
                <Cover gradient={al.cover} size="100%" />
              </div>
              <span className="card__title">{al.title}</span>
              <span className="card__sub">{`${al.year} · ${al.artist}`}</span>
            </div>
          ))}
        </div>
      </section>

      <section className="shelf">
        <div className="shelf__head">
          <h2 className="shelf__title">Suas músicas</h2>
          <span className="shelf__hint">
            {popular.reduce((acc, t) => acc + t.duration, 0) > 0 &&
              `${popular.length} faixas · ${formatLongTime(
                popular.reduce((acc, t) => acc + t.duration, 0),
              )}`}
          </span>
        </div>
        <div className="track-table">
          {popular.map((t, i) => (
            <TrackRow
              key={t.id}
              index={i}
              track={t}
              isCurrent={currentTrackId === t.id}
              isPlaying={isPlaying}
              liked={likedIds.has(t.id)}
              onPlay={() => onPlayTrack(t.id, popularIds)}
              onToggleLike={() => onToggleLike(t.id)}
            />
          ))}
        </div>
      </section>

      <footer className="home__footer">
        <p>
          {apiOnline
            ? `Conectado ao backend tz'music · ${localTrackCount} arquivo(s) local(is) indexado(s)`
            : 'Backend offline — exibindo catálogo de demonstração'}
        </p>
      </footer>
    </div>
  )
}
