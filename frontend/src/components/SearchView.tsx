import { categories, type Playlist, type Track } from '../data/mock'
import { Cover, TrackRow } from './common'

interface SearchViewProps {
  query: string
  tracks: Track[]
  playlists: Playlist[]
  likedIds: Set<string>
  currentTrackId: string | null
  isPlaying: boolean
  onOpenPlaylist: (id: string) => void
  onPlayTrack: (trackId: string, contextIds: string[]) => void
  onToggleLike: (trackId: string) => void
}

const norm = (s: string) =>
  s.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '')

export function SearchView({
  query,
  tracks,
  playlists,
  likedIds,
  currentTrackId,
  isPlaying,
  onOpenPlaylist,
  onPlayTrack,
  onToggleLike,
}: SearchViewProps) {
  const q = norm(query.trim())

  if (q.length === 0) {
    return (
      <div className="search">
        <section className="shelf">
          <h2 className="shelf__title">Explore suas tendências</h2>
          <div className="category-grid">
            {categories.map((c) => (
              <div key={c.id} className="category-tile" style={{ background: c.color }}>
                <span>{c.name}</span>
              </div>
            ))}
          </div>
        </section>
      </div>
    )
  }

  const foundTracks = tracks.filter(
    (t) => norm(t.title).includes(q) || norm(t.artist).includes(q) || norm(t.album).includes(q),
  )
  const foundPlaylists = playlists.filter((p) => norm(p.name).includes(q))
  const firstPlaylist = foundPlaylists[0]

  if (foundTracks.length === 0 && foundPlaylists.length === 0) {
    return (
      <div className="search">
        <div className="empty-state">
          <h3>Nenhum resultado para “{query}”</h3>
          <p>Verifique a grafia ou tente outro termo. O catálogo deste protótipo é de demonstração.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="search">
      {firstPlaylist && (
        <section className="shelf search__top">
          <h2 className="shelf__title">Melhor resultado</h2>
          <div className="search__best" onClick={() => onOpenPlaylist(firstPlaylist.id)}>
            <Cover gradient={firstPlaylist.cover} size={192} />
            <div>
              <span className="search__best-kind">Playlist</span>
              <h3 className="search__best-name">{firstPlaylist.name}</h3>
              <p className="search__best-desc">{firstPlaylist.description}</p>
            </div>
          </div>
        </section>
      )}

      {foundTracks.length > 0 && (
        <section className="shelf">
          <h2 className="shelf__title">Faixas</h2>
          <div className="track-table track-table--full">
            {foundTracks.map((t, i) => (
              <TrackRow
                key={t.id}
                index={i}
                track={t}
                isCurrent={currentTrackId === t.id}
                isPlaying={isPlaying}
                liked={likedIds.has(t.id)}
                onPlay={() => onPlayTrack(t.id, foundTracks.map((x) => x.id))}
                onToggleLike={() => onToggleLike(t.id)}
              />
            ))}
          </div>
        </section>
      )}

      {foundPlaylists.length > 1 && (
        <section className="shelf">
          <h2 className="shelf__title">Playlists</h2>
          <div className="card-grid">
            {foundPlaylists.map((p) => (
              <div key={p.id} className="card" onClick={() => onOpenPlaylist(p.id)}>
                <div className="card__cover-wrap">
                  <Cover gradient={p.cover} size="100%" />
                </div>
                <span className="card__title">{p.name}</span>
                <span className="card__sub">{p.description}</span>
              </div>
            ))}
          </div>
        </section>
      )}
    </div>
  )
}
