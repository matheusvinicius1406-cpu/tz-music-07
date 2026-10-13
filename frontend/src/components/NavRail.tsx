import { IconHome, IconLibrary, IconPlus, IconSearch } from '../icons'

interface NavRailProps {
  active: 'home' | 'search' | 'none'
  onHome: () => void
  onSearch: () => void
}

export function NavRail({ active, onHome, onSearch }: NavRailProps) {
  return (
    <nav className="rail" aria-label="Navegação principal">
      <div className="rail__logo">
        <span className="rail__logo-mark" aria-hidden>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 17.5V6.2l10-2v10.6" fill="none" stroke="#000" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
            <circle cx="6.6" cy="17.6" r="2.6" />
            <circle cx="16.6" cy="15.4" r="2.6" />
          </svg>
        </span>
        <span className="rail__logo-text">tz'music</span>
      </div>

      <button className={`rail__btn${active === 'home' ? ' is-active' : ''}`} onClick={onHome}>
        <IconHome size={24} />
        <span>Início</span>
      </button>
      <button className={`rail__btn${active === 'search' ? ' is-active' : ''}`} onClick={onSearch}>
        <IconSearch size={24} />
        <span>Buscar</span>
      </button>

      <div className="rail__footer">
        <button className="rail__btn rail__btn--muted" disabled title="Em breve">
          <IconPlus size={20} />
          <span>Instalar app</span>
        </button>
        <button className="rail__btn rail__btn--muted" disabled title="Em breve">
          <IconLibrary size={20} />
          <span>Sincronizar</span>
        </button>
      </div>
    </nav>
  )
}
