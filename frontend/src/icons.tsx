interface IconProps {
  size?: number
  className?: string
}

const base = (size: number) => ({
  width: size,
  height: size,
  viewBox: '0 0 24 24',
  'aria-hidden': true as const,
})

export function IconHome({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <path d="M12 3.1 3 10.6V21h6.4v-6.2h5.2V21H21V10.6L12 3.1Z" />
    </svg>
  )
}

export function IconSearch({ size = 24, className }: IconProps) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
    >
      <circle cx="10.5" cy="10.5" r="6.5" />
      <line x1="15.5" y1="15.5" x2="21" y2="21" />
    </svg>
  )
}

export function IconLibrary({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <rect x="3" y="4" width="2.4" height="16" rx="1.2" />
      <rect x="8" y="4" width="2.4" height="16" rx="1.2" />
      <path d="M14.6 5.1 18.9 6.3 15.4 20.2 11.1 19 14.6 5.1Z" />
    </svg>
  )
}

export function IconPlus({ size = 24, className }: IconProps) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
    >
      <line x1="12" y1="5" x2="12" y2="19" />
      <line x1="5" y1="12" x2="19" y2="12" />
    </svg>
  )
}

export function IconPlay({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <path d="M7.5 4.8 19 12 7.5 19.2V4.8Z" />
    </svg>
  )
}

export function IconPause({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <rect x="6" y="4.5" width="4" height="15" rx="1" />
      <rect x="14" y="4.5" width="4" height="15" rx="1" />
    </svg>
  )
}

export function IconNext({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <path d="M5.5 5 15 12 5.5 19V5Z" />
      <rect x="16.5" y="5" width="2.5" height="14" rx="1.2" />
    </svg>
  )
}

export function IconPrev({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <path d="M18.5 5 9 12l9.5 7V5Z" />
      <rect x="5" y="5" width="2.5" height="14" rx="1.2" />
    </svg>
  )
}

export function IconShuffle({ size = 24, className }: IconProps) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M16 3h5v5" />
      <path d="M4 20 21 3" />
      <path d="M21 16v5h-5" />
      <path d="M15 15l6 6" />
      <path d="M4 4l5 5" />
    </svg>
  )
}

export function IconRepeat({ size = 24, className }: IconProps) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <path d="M17 2l4 4-4 4" />
      <path d="M3 11V9a4 4 0 0 1 4-4h14" />
      <path d="M7 22l-4-4 4-4" />
      <path d="M21 13v2a4 4 0 0 1-4 4H3" />
    </svg>
  )
}

export function IconRepeatOne({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="none" stroke="currentColor">
      <g strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M17 2l4 4-4 4" />
        <path d="M3 11V9a4 4 0 0 1 4-4h14" />
        <path d="M7 22l-4-4 4-4" />
        <path d="M21 13v2a4 4 0 0 1-4 4H3" />
      </g>
      <path d="M11.2 10.4 13 9.6V15" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" fill="none" />
    </svg>
  )
}

export function IconHeart({ size = 24, className, filled = false }: IconProps & { filled?: boolean }) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill={filled ? 'currentColor' : 'none'}
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinejoin="round"
    >
      <path d="M12 20.7c-.3 0-.6-.1-.8-.3C7.4 17.5 3 14 3 9.4 3 6.7 5 4.7 7.6 4.7c1.6 0 3.1.8 4.4 2.3 1.3-1.5 2.8-2.3 4.4-2.3C19 4.7 21 6.7 21 9.4c0 4.6-4.4 8.1-8.2 11-.2.2-.5.3-.8.3Z" />
    </svg>
  )
}

export function IconVolume({ size = 24, className, muted = false }: IconProps & { muted?: boolean }) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <path d="M4 9.5h3.4L12 5.6v12.8L7.4 14.5H4v-5Z" />
      {muted ? (
        <g stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" fill="none">
          <path d="M16 9.5l4.5 5" />
          <path d="M20.5 9.5l-4.5 5" />
        </g>
      ) : (
        <g stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" fill="none">
          <path d="M15.5 9a4.2 4.2 0 0 1 0 6" />
          <path d="M18 6.8a7.6 7.6 0 0 1 0 10.4" />
        </g>
      )}
    </svg>
  )
}

export function IconQueue({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <rect x="3" y="5" width="12" height="1.8" rx="0.9" />
      <rect x="3" y="10" width="12" height="1.8" rx="0.9" />
      <rect x="3" y="15" width="8" height="1.8" rx="0.9" />
      <path d="M16.5 12.5 22 15.5l-5.5 3v-6Z" />
    </svg>
  )
}

export function IconMusicNote({ size = 24, className }: IconProps) {
  return (
    <svg {...base(size)} className={className} fill="currentColor">
      <path d="M9 17.5V6.2l10-2v10.6" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
      <circle cx="6.6" cy="17.6" r="2.6" />
      <circle cx="16.6" cy="15.4" r="2.6" />
    </svg>
  )
}

export function IconArrowLeft({ size = 24, className }: IconProps) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill="none"
      stroke="currentColor"
      strokeWidth="2.2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <polyline points="14.5 5 7.5 12 14.5 19" />
    </svg>
  )
}

export function IconArrowRight({ size = 24, className }: IconProps) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill="none"
      stroke="currentColor"
      strokeWidth="2.2"
      strokeLinecap="round"
      strokeLinejoin="round"
    >
      <polyline points="9.5 5 16.5 12 9.5 19" />
    </svg>
  )
}

export function IconClock({ size = 16, className }: IconProps) {
  return (
    <svg
      {...base(size)}
      className={className}
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
    >
      <circle cx="12" cy="12" r="8.5" />
      <polyline points="12 7.5 12 12 15 13.8" strokeLinejoin="round" />
    </svg>
  )
}
