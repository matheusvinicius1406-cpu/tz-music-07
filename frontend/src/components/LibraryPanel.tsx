import type { Playlist } from '../data/mock'
import { IconPlus } from '../icons'
import { Cover } from './common'

interface LibraryPanelProps {
  playlists: Playlist[]
  activeId: string | null
  likedCount: number
  onSelectPlaylist: (id: string) => void
  onSelectLiked: () => void
  onCreate: () => void
}

export function LibraryPanel({
  playlists,
  activeId,
  likedCount,
  onSelectPlaylist,
  onSelectLiked,
  onCreate,
}: LibraryPanelProps) {
  const likedActive = activeId === 'liked'
  return (
    <aside className="library" aria-label="Sua biblioteca">
      <header className="library__header">
        <h2 className="library__title">Sua Biblioteca</h2>
        <button className="icon-btn" onClick={onCreate} aria-label="Criar playlist">
          <IconPlus size={18} />
        </button>
      </header>

      <div className="library__chips">
        <span className="chip">Playlists</span>
        <span className="chip">Álbuns</span>
        <span className="chip">Artistas</span>
      </div>

      <div className="library__list">
        <button
          className={`library__item${likedActive ? ' is-active' : ''}`}
          onClick={onSelectLiked}
        >
          <Cover gradient="linear-gradient(135deg, #450af5 0%, #c4efd9 100%)" size={48} />
          <span className="library__item-meta">
            <span className="library__item-name">Músicas Curtidas</span>
            <span className="library__item-sub">Playlist · {likedCount} músicas</span>
          </span>
        </button>

        {playlists.map((p) => (
          <button
            key={p.id}
            className={`library__item${activeId === p.id ? ' is-active' : ''}`}
            onClick={() => onSelectPlaylist(p.id)}
          >
            <Cover gradient={p.cover} size={48} />
            <span className="library__item-meta">
              <span className="library__item-name">{p.name}</span>
              <span className="library__item-sub">Playlist · {p.owner}</span>
            </span>
          </button>
        ))}
      </div>
    </aside>
  )
}
