import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { api, getToken, setToken } from './api'
import { initialPlaylists, tracks as demoTracks, type Playlist, type Track } from './data/mock'
import { NavRail } from './components/NavRail'
import { LibraryPanel } from './components/LibraryPanel'
import { TopBar } from './components/TopBar'
import { HomeView } from './components/HomeView'
import { PlaylistView } from './components/PlaylistView'
import { SearchView } from './components/SearchView'
import { LoginModal, restoreSession } from './components/LoginModal'
import { PlayerBar, type RepeatMode } from './components/PlayerBar'
import type { SessionUser } from './api'

type View =
  | { kind: 'home' }
  | { kind: 'search' }
  | { kind: 'playlist'; id: string }
  | { kind: 'liked' }

const LIKED_ID = 'liked'

export default function App() {
  // ---- navigation ----
  const [view, setView] = useState<View>({ kind: 'home' })
  const [backStack, setBackStack] = useState<View[]>([])
  const [forwardStack, setForwardStack] = useState<View[]>([])

  const navigate = useCallback(
    (next: View) => {
      setBackStack((s) => [...s, view])
      setForwardStack([])
      setView(next)
    },
    [view],
  )

  const goBack = useCallback(() => {
    const prev = backStack[backStack.length - 1]
    if (!prev) return
    setForwardStack((s) => [...s, view])
    setBackStack((s) => s.slice(0, -1))
    setView(prev)
  }, [backStack, view])

  const goForward = useCallback(() => {
    const next = forwardStack[forwardStack.length - 1]
    if (!next) return
    setBackStack((s) => [...s, view])
    setForwardStack((s) => s.slice(0, -1))
    setView(next)
  }, [forwardStack, view])

  // ---- catalog from the backend API (demo fallback when offline) ----
  const [tracks, setTracks] = useState<Track[]>(demoTracks)
  const [playlists, setPlaylists] = useState<Playlist[]>(initialPlaylists)
  const [apiOnline, setApiOnline] = useState(false)
  const [localTrackCount, setLocalTrackCount] = useState(0)
  const [user, setUser] = useState<SessionUser | null>(null)
  const [authOpen, setAuthOpen] = useState(false)

  const loadPlaylists = useCallback(async (token: string | null) => {
    try {
      const fetched = await api.playlists(token)
      if (fetched.length > 0) setPlaylists(fetched)
      return true
    } catch {
      return false
    }
  }, [])

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      const session = await restoreSession()
      if (cancelled) return
      if (session) setUser(session)
      const token = getToken()
      try {
        const [fetchedTracks, fetchedPlaylists, health] = await Promise.all([
          api.tracks(),
          api.playlists(token),
          api.health(),
        ])
        if (cancelled) return
        if (fetchedTracks.length > 0) setTracks(fetchedTracks)
        if (fetchedPlaylists.length > 0) setPlaylists(fetchedPlaylists)
        setLocalTrackCount(health.localTracks)
        setApiOnline(true)
      } catch {
        if (!cancelled) {
          setApiOnline(false)
          setTracks(demoTracks)
          setPlaylists(initialPlaylists)
        }
      }
    })()
    return () => {
      cancelled = true
    }
  }, [])

  const tracksById = useMemo(
    () => Object.fromEntries(tracks.map((t) => [t.id, t])),
    [tracks],
  )

  const [likedIds, setLikedIds] = useState<Set<string>>(
    () => new Set(['t1', 't5', 't11', 't17']),
  )
  const [query, setQuery] = useState('')

  const createPlaylist = useCallback(async () => {
    const name = `Nova playlist ${playlists.length + 1}`
    if (apiOnline && !user) {
      setAuthOpen(true)
      return
    }
    if (apiOnline && user) {
      try {
        const created = await api.createPlaylist(name, 'Criada por você.', getToken())
        setPlaylists((list) => [created, ...list])
        navigate({ kind: 'playlist', id: created.id })
        return
      } catch {
        setAuthOpen(true)
        return
      }
    }
    const palettes = [
      'linear-gradient(135deg, #1a2b6d 0%, #4b8bbe 100%)',
      'linear-gradient(135deg, #6d1a4b 0%, #bb4b8b 100%)',
      'linear-gradient(135deg, #1a6d3a 0%, #4bbb7a 100%)',
    ]
    const local: Playlist = {
      id: `p-local-${Date.now()}`,
      name,
      description: 'Criada por você (offline).',
      owner: 'Você',
      cover: palettes[playlists.length % palettes.length],
      trackIds: [],
    }
    setPlaylists((list) => [local, ...list])
    navigate({ kind: 'playlist', id: local.id })
  }, [playlists.length, navigate, apiOnline])

  const toggleLike = useCallback((trackId: string) => {
    setLikedIds((prev) => {
      const next = new Set(prev)
      if (next.has(trackId)) next.delete(trackId)
      else next.add(trackId)
      return next
    })
  }, [])

  // ---- player ----
  const [currentTrackId, setCurrentTrackId] = useState<string | null>(null)
  const [isPlaying, setIsPlaying] = useState(false)
  const [progress, setProgress] = useState(0)
  const [volume, setVolume] = useState(0.7)
  const [shuffle, setShuffle] = useState(false)
  const [repeat, setRepeat] = useState<RepeatMode>('off')
  const [contextIds, setContextIds] = useState<string[]>([])

  const currentTrack = useMemo(
    () => (currentTrackId ? tracksById[currentTrackId] ?? null : null),
    [currentTrackId, tracksById],
  )
  const isStreaming = Boolean(currentTrack?.streamUrl)

  // real <audio> element for local library tracks
  const audioRef = useRef<HTMLAudioElement | null>(null)
  useEffect(() => {
    const audio = new Audio()
    audio.preload = 'metadata'
    audioRef.current = audio
    const onTime = () => setProgress(audio.currentTime)
    const onPlay = () => setIsPlaying(true)
    const onPause = () => setIsPlaying(false)
    audio.addEventListener('timeupdate', onTime)
    audio.addEventListener('play', onPlay)
    audio.addEventListener('pause', onPause)
    return () => {
      audio.pause()
      audio.removeEventListener('timeupdate', onTime)
      audio.removeEventListener('play', onPlay)
      audio.removeEventListener('pause', onPause)
      audioRef.current = null
    }
  }, [])

  // load the source whenever the current track changes
  useEffect(() => {
    const audio = audioRef.current
    if (!audio) return
    const url = currentTrack?.streamUrl ?? null
    if (url) {
      const absolute = new URL(url, window.location.origin).href
      if (audio.src !== absolute) {
        audio.src = absolute
        audio.load()
      }
    } else if (audio.src) {
      audio.pause()
      audio.removeAttribute('src')
      audio.load()
      setProgress(0)
    }
  }, [currentTrack])

  // state -> audio element
  useEffect(() => {
    const audio = audioRef.current
    if (!audio || !currentTrack?.streamUrl) return
    if (isPlaying) void audio.play().catch(() => setIsPlaying(false))
    else audio.pause()
  }, [isPlaying, currentTrack?.streamUrl])

  useEffect(() => {
    if (audioRef.current) audioRef.current.volume = volume
  }, [volume])

  const playTrack = useCallback(
    (trackId: string, ctx: string[]) => {
      if (currentTrackId === trackId) {
        setIsPlaying((p) => !p)
        return
      }
      setCurrentTrackId(trackId)
      setContextIds(ctx)
      setProgress(0)
      setIsPlaying(true)
    },
    [currentTrackId],
  )

  const playCollection = useCallback(
    (ids: string[]) => {
      const playing = ids.includes(currentTrackId ?? '')
      if (playing && isPlaying) {
        setIsPlaying(false)
        return
      }
      if (playing && currentTrackId) {
        setIsPlaying(true)
        return
      }
      const first = ids[0]
      if (!first) return
      playTrack(first, ids)
    },
    [currentTrackId, isPlaying, playTrack],
  )

  const step = useCallback(
    (dir: 1 | -1) => {
      if (!currentTrackId || contextIds.length === 0) return
      if (dir === -1 && progress > 3 && !isStreaming) {
        setProgress(0)
        return
      }
      const i = contextIds.indexOf(currentTrackId)
      let nextIndex: number
      if (shuffle && contextIds.length > 1) {
        do {
          nextIndex = Math.floor(Math.random() * contextIds.length)
        } while (nextIndex === i)
      } else {
        nextIndex = (i + dir + contextIds.length) % contextIds.length
      }
      setCurrentTrackId(contextIds[nextIndex])
      setProgress(0)
    },
    [contextIds, currentTrackId, shuffle, progress, isStreaming],
  )

  const next = useCallback(() => step(1), [step])
  const prev = useCallback(() => {
    const audio = audioRef.current
    if (audio && isStreaming && audio.currentTime > 3) {
      audio.currentTime = 0
      setProgress(0)
      return
    }
    step(-1)
  }, [step, isStreaming])

  // simulated playback clock for demo tracks (local tracks use <audio>)
  useEffect(() => {
    if (!isPlaying || !currentTrack || currentTrack.streamUrl) return
    const id = window.setInterval(() => setProgress((p) => p + 0.5), 500)
    return () => window.clearInterval(id)
  }, [isPlaying, currentTrack])

  // auto-advance (demo tracks only; <audio> 'ended' flows through progress)
  useEffect(() => {
    if (!currentTrack || currentTrack.streamUrl) return
    if (progress < currentTrack.duration) return
    if (repeat === 'one') {
      setProgress(0)
      return
    }
    const isLast = contextIds.length > 0 && contextIds[contextIds.length - 1] === currentTrack.id
    if (repeat === 'off' && isLast) {
      setProgress(0)
      setIsPlaying(false)
      return
    }
    next()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [progress])

  const seek = useCallback(
    (seconds: number) => {
      const audio = audioRef.current
      if (audio && currentTrack?.streamUrl) audio.currentTime = seconds
      setProgress(seconds)
    },
    [currentTrack],
  )

  // ---- derived views ----
  const activePlaylistId =
    view.kind === 'playlist' ? view.id : view.kind === 'liked' ? LIKED_ID : null

  const likedPlaylist: Playlist = useMemo(
    () => ({
      id: LIKED_ID,
      name: 'Músicas Curtidas',
      description: 'Todas as faixas que você marcou com coração.',
      owner: 'Você',
      cover: 'linear-gradient(135deg, #450af5 0%, #c4efd9 100%)',
      trackIds: tracks.filter((t) => likedIds.has(t.id)).map((t) => t.id),
    }),
    [likedIds, tracks],
  )

  const openPlaylist = useCallback((id: string) => navigate({ kind: 'playlist', id }), [navigate])
  const openSearch = useCallback(() => navigate({ kind: 'search' }), [navigate])
  const openHome = useCallback(() => navigate({ kind: 'home' }), [navigate])

  const shownPlaylist =
    view.kind === 'liked'
      ? likedPlaylist
      : view.kind === 'playlist'
        ? playlists.find((p) => p.id === view.id) ?? null
        : null

  return (
    <div className="app">
      <NavRail
        active={view.kind === 'home' ? 'home' : view.kind === 'search' ? 'search' : 'none'}
        onHome={openHome}
        onSearch={openSearch}
      />

      <LibraryPanel
        playlists={playlists}
        activeId={activePlaylistId}
        likedCount={likedIds.size}
        onSelectPlaylist={openPlaylist}
        onSelectLiked={() => navigate({ kind: 'liked' })}
        onCreate={createPlaylist}
      />

      <main className="main">
        <TopBar
          showSearch={view.kind === 'search'}
          query={query}
          onQueryChange={setQuery}
          canBack={backStack.length > 0}
          canForward={forwardStack.length > 0}
          onBack={goBack}
          onForward={goForward}
          user={user}
          onLogin={() => setAuthOpen(true)}
          onLogout={() => {
            setToken(null)
            setUser(null)
            void loadPlaylists(null)
          }}
        />
        <div className="main__scroll">
          {view.kind === 'home' && (
            <HomeView
              playlists={playlists}
              tracks={tracks}
              likedIds={likedIds}
              currentTrackId={currentTrackId}
              isPlaying={isPlaying}
              apiOnline={apiOnline}
              localTrackCount={localTrackCount}
              onOpenPlaylist={openPlaylist}
              onPlayTrack={playTrack}
              onPlayPlaylist={(p) => playCollection(p.trackIds)}
              onToggleLike={toggleLike}
            />
          )}
          {view.kind === 'search' && (
            <SearchView
              query={query}
              tracks={tracks}
              playlists={playlists}
              likedIds={likedIds}
              currentTrackId={currentTrackId}
              isPlaying={isPlaying}
              onOpenPlaylist={openPlaylist}
              onPlayTrack={playTrack}
              onToggleLike={toggleLike}
            />
          )}
          {shownPlaylist && (
            <PlaylistView
              playlist={shownPlaylist}
              tracksById={tracksById}
              isPlaying={isPlaying}
              currentTrackId={currentTrackId}
              likedIds={likedIds}
              onPlayAll={(p) => playCollection(p.trackIds)}
              onPlayTrack={playTrack}
              onToggleLike={toggleLike}
            />
          )}
        </div>
      </main>

      <PlayerBar
        track={currentTrack}
        isPlaying={isPlaying}
        progress={progress}
        volume={volume}
        shuffle={shuffle}
        repeat={repeat}
        liked={currentTrackId ? likedIds.has(currentTrackId) : false}
        onTogglePlay={() => currentTrack && setIsPlaying((p) => !p)}
        onNext={next}
        onPrev={prev}
        onSeek={seek}
        onVolume={setVolume}
        onShuffle={() => setShuffle((s) => !s)}
        onRepeat={() => setRepeat((r) => (r === 'off' ? 'all' : r === 'all' ? 'one' : 'off'))}
        onToggleLike={() => currentTrackId && toggleLike(currentTrackId)}
      />

      <LoginModal
        open={authOpen}
        onClose={() => setAuthOpen(false)}
        onLogin={(sessionUser) => {
          setUser(sessionUser)
          void loadPlaylists(getToken())
        }}
      />
    </div>
  )
}
