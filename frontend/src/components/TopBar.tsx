import { IconArrowLeft, IconArrowRight, IconSearch } from '../icons'
import type { SessionUser } from '../api'

interface TopBarProps {
  showSearch: boolean
  query: string
  onQueryChange: (q: string) => void
  canBack: boolean
  canForward: boolean
  onBack: () => void
  onForward: () => void
  user: SessionUser | null
  onLogin: () => void
  onLogout: () => void
}

export function TopBar({
  showSearch,
  query,
  onQueryChange,
  canBack,
  canForward,
  onBack,
  onForward,
  user,
  onLogin,
  onLogout,
}: TopBarProps) {
  return (
    <header className="topbar">
      <div className="topbar__nav">
        <button className="circle-btn" onClick={onBack} disabled={!canBack} aria-label="Voltar">
          <IconArrowLeft size={18} />
        </button>
        <button className="circle-btn" onClick={onForward} disabled={!canForward} aria-label="Avançar">
          <IconArrowRight size={18} />
        </button>
      </div>

      {showSearch && (
        <div className="topbar__search">
          <IconSearch size={18} className="topbar__search-icon" />
          <input
            className="topbar__search-input"
            placeholder="O que você quer ouvir?"
            value={query}
            onChange={(e) => onQueryChange(e.target.value)}
            autoFocus
          />
        </div>
      )}

      <div className="topbar__right">
        {user ? (
          <>
            <span className="user-chip" title={`Conectado como ${user.username}`}>
              <span className="avatar avatar--sm">{user.username[0]?.toUpperCase()}</span>
              {user.username}
            </span>
            <button className="pill-btn pill-btn--ghost" onClick={onLogout}>
              Sair
            </button>
          </>
        ) : (
          <button className="pill-btn pill-btn--light" onClick={onLogin}>
            Entrar
          </button>
        )}
        <button className="pill-btn" disabled title="Em breve">
          Instalar aplicativo
        </button>
      </div>
    </header>
  )
}
