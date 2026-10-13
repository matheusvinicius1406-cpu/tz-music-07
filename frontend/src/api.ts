import type { Playlist, Track } from './data/mock'

// The Vite dev server proxies /api to the Ktor backend on :8080
// (see vite.config.ts). TrackDto/PlaylistDto mirror these shapes.

const BASE = '/api'

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const { headers, ...rest } = init ?? {}
  const res = await fetch(`${BASE}${path}`, {
    ...rest,
    headers: { 'Content-Type': 'application/json', ...(headers ?? {}) },
  })
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  if (res.status === 204) return undefined as T
  return (await res.json()) as T
}

export interface HealthInfo {
  status: string
  version: string
  musicDir: string
  localTracks: number
}

export interface SessionUser {
  id: string
  username: string
}

export interface LoginResult {
  token: string
  tokenType: string
  expiresIn: number
  userId: string
  username: string
}

const TOKEN_KEY = 'tzmusic.token'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token)
  else localStorage.removeItem(TOKEN_KEY)
}

export const api = {
  health: () => request<HealthInfo>('/health'),

  tracks: () => request<{ tracks: Track[] }>('/v1/tracks').then((r) => r.tracks),

  playlists: (token?: string | null) =>
    request<{ playlists: Playlist[] }>('/v1/playlists', {
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    }).then((r) => r.playlists),

  register: (username: string, password: string) =>
    request<SessionUser>('/v1/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),

  login: (username: string, password: string) =>
    request<LoginResult>('/v1/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),

  me: (token: string) =>
    request<SessionUser>('/v1/me', { headers: { Authorization: `Bearer ${token}` } }),

  createPlaylist: (name: string, description = '', token?: string | null) =>
    request<Playlist>('/v1/playlists', {
      method: 'POST',
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      body: JSON.stringify({ name, description }),
    }),

  deletePlaylist: (id: string, token?: string | null) =>
    request<void>(`/v1/playlists/${id}`, {
      method: 'DELETE',
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    }),

  addTrack: (playlistId: string, trackId: string, token?: string | null) =>
    request<Playlist>(`/v1/playlists/${playlistId}/tracks`, {
      method: 'POST',
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      body: JSON.stringify({ trackId }),
    }),
}
