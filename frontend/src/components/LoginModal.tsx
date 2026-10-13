import { useState, type FormEvent } from 'react'
import { api, getToken, setToken, type SessionUser } from '../api'

interface LoginModalProps {
  open: boolean
  onClose: () => void
  onLogin: (user: SessionUser) => void
}

export function LoginModal({ open, onClose, onLogin }: LoginModalProps) {
  const [mode, setMode] = useState<'login' | 'register'>('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)

  if (!open) return null

  const submit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    setBusy(true)
    try {
      if (mode === 'register') {
        await api.register(username, password)
      }
      const result = await api.login(username, password)
      setToken(result.token)
      onLogin({ id: result.userId, username: result.username })
      setUsername('')
      setPassword('')
      onClose()
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Falha na operação'
      setError(
        message.includes('409')
          ? 'Nome de usuário já existe'
          : message.includes('401')
            ? 'Usuário ou senha incorretos'
            : message.includes('400')
              ? 'Usuário: 3–32 caracteres (letras, números, _ . -) · Senha: mínimo 8 caracteres'
              : 'Backend indisponível — tente novamente',
      )
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2 className="modal__title">
          {mode === 'login' ? 'Entrar no tz’music' : 'Criar conta'}
        </h2>
        <p className="modal__subtitle">
          Contas ficam no seu servidor auto-hospedado. Sem telemetria.
        </p>
        <form className="modal__form" onSubmit={submit}>
          <input
            className="modal__input"
            placeholder="Usuário"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            autoFocus
          />
          <input
            className="modal__input"
            type="password"
            placeholder="Senha"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
          />
          {error && <p className="modal__error">{error}</p>}
          <button className="modal__submit" type="submit" disabled={busy || !username || !password}>
            {busy ? 'Aguarde…' : mode === 'login' ? 'Entrar' : 'Registrar'}
          </button>
        </form>
        <button
          className="modal__switch"
          onClick={() => {
            setMode(mode === 'login' ? 'register' : 'login')
            setError(null)
          }}
        >
          {mode === 'login' ? 'Não tem conta? Registre-se' : 'Já tem conta? Entre'}
        </button>
        <button className="modal__close" onClick={onClose} aria-label="Fechar">
          ×
        </button>
      </div>
    </div>
  )
}

/** Restores a previous session on startup; returns null when absent/invalid. */
export async function restoreSession(): Promise<SessionUser | null> {
  const token = getToken()
  if (!token) return null
  try {
    return await api.me(token)
  } catch {
    setToken(null)
    return null
  }
}
