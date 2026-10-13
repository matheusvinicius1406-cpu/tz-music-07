export interface Track {
  id: string
  title: string
  artist: string
  album: string
  duration: number // seconds
  cover: string // css gradient
  streamUrl?: string | null // set for local library tracks
  local?: boolean
}

export interface Playlist {
  id: string
  name: string
  description: string
  owner: string
  cover: string
  trackIds: string[]
  canEdit?: boolean
}

export interface Artist {
  id: string
  name: string
  monthlyListeners: number
  cover: string
}

export interface Album {
  id: string
  title: string
  artist: string
  year: number
  cover: string
}

export const GRADIENTS = [
  'linear-gradient(135deg, #4b0f2e 0%, #e8115b 100%)',
  'linear-gradient(135deg, #1e3264 0%, #8d67ab 100%)',
  'linear-gradient(135deg, #0d73ec 0%, #1ed760 100%)',
  'linear-gradient(135deg, #7358ff 0%, #b49bff 100%)',
  'linear-gradient(135deg, #ba5d07 0%, #ffd264 100%)',
  'linear-gradient(135deg, #148a08 0%, #66d15f 100%)',
  'linear-gradient(135deg, #8c1932 0%, #e61e32 100%)',
  'linear-gradient(135deg, #056952 0%, #2dd4a7 100%)',
  'linear-gradient(135deg, #27856a 0%, #8bb369 100%)',
  'linear-gradient(135deg, #503750 0%, #a476b4 100%)',
]

const grad = (i: number) => GRADIENTS[i % GRADIENTS.length]

export const tracks: Track[] = [
  { id: 't1', title: 'Neon na Bruma', artist: 'Aurora Vale', album: 'Cidades Acesas', duration: 222, cover: grad(0) },
  { id: 't2', title: 'Selva de Concreto', artist: 'Aurora Vale', album: 'Cidades Acesas', duration: 245, cover: grad(0) },
  { id: 't3', title: 'Maré Baixa', artist: 'Coletivo Maré', album: 'Água Morna', duration: 198, cover: grad(7) },
  { id: 't4', title: 'Vento Norte', artist: 'Coletivo Maré', album: 'Água Morna', duration: 236, cover: grad(7) },
  { id: 't5', title: 'Pulso', artist: 'Kaya Sol', album: 'Frequência', duration: 178, cover: grad(1) },
  { id: 't6', title: 'Insônia', artist: 'Kaya Sol', album: 'Frequência', duration: 213, cover: grad(1) },
  { id: 't7', title: 'Ruído Branco', artist: 'Duo Ruído', album: 'Sinal', duration: 261, cover: grad(3) },
  { id: 't8', title: 'Estática', artist: 'Duo Ruído', album: 'Sinal', duration: 227, cover: grad(3) },
  { id: 't9', title: 'Café das Seis', artist: 'João Mirante', album: 'Manhãs Lentas', duration: 164, cover: grad(4) },
  { id: 't10', title: 'Rua Vazia', artist: 'João Mirante', album: 'Manhãs Lentas', duration: 192, cover: grad(4) },
  { id: 't11', title: 'Órbita', artist: 'Luma Prisma', album: 'Satélites', duration: 242, cover: grad(2) },
  { id: 't12', title: 'Gravidade Zero', artist: 'Luma Prisma', album: 'Satélites', duration: 209, cover: grad(2) },
  { id: 't13', title: 'Fogo Fátuo', artist: 'Vagalume', album: 'Fogo Fátuo', duration: 231, cover: grad(6) },
  { id: 't14', title: 'Campo Aberto', artist: 'Vagalume', album: 'Fogo Fátuo', duration: 254, cover: grad(6) },
  { id: 't15', title: 'Batida Antiga', artist: 'DJ Sereno', album: 'Cassette Club', duration: 307, cover: grad(9) },
  { id: 't16', title: 'Fita Rebobinada', artist: 'DJ Sereno', album: 'Cassette Club', duration: 273, cover: grad(9) },
  { id: 't17', title: 'Cordas de Fevereiro', artist: 'Orquestra Pequena', album: 'Sessões', duration: 206, cover: grad(8) },
  { id: 't18', title: 'Sessão da Tarde', artist: 'Orquestra Pequena', album: 'Sessões', duration: 288, cover: grad(8) },
]

export const tracksById: Record<string, Track> = Object.fromEntries(
  tracks.map((t) => [t.id, t]),
)

export const initialPlaylists: Playlist[] = [
  {
    id: 'p1',
    name: 'Mix Diário',
    description: 'Seu mix diário de favoritos. Atualizado hoje.',
    owner: "tz'music",
    cover: grad(1),
    trackIds: ['t1', 't5', 't11', 't13', 't3', 't15'],
  },
  {
    id: 'p2',
    name: 'Brasil Indie',
    description: 'O melhor do indie brasileiro, atualizado toda semana.',
    owner: "tz'music",
    cover: grad(6),
    trackIds: ['t2', 't4', 't9', 't13', 't17'],
  },
  {
    id: 'p3',
    name: 'Foco Profundo',
    description: 'Batidas calmas e constantes para concentrar.',
    owner: 'Você',
    cover: grad(3),
    trackIds: ['t7', 't8', 't11', 't12', 't16'],
  },
  {
    id: 'p4',
    name: 'Chilltarde',
    description: 'Vibe leve para o fim da tarde.',
    owner: "tz'music",
    cover: grad(7),
    trackIds: ['t3', 't10', 't14', 't17', 't6'],
  },
  {
    id: 'p5',
    name: 'Pop de Meia-Noite',
    description: 'Sintetizadores, luzes e madrugada.',
    owner: "tz'music",
    cover: grad(0),
    trackIds: ['t5', 't6', 't1', 't12'],
  },
  {
    id: 'p6',
    name: 'Clássicos da Semana',
    description: 'Os mais tocados dos últimos sete dias.',
    owner: "tz'music",
    cover: grad(9),
    trackIds: ['t15', 't16', 't18', 't7', 't8'],
  },
  {
    id: 'p7',
    name: 'Descobertas da Semana',
    description: 'Faixas novas escolhidas para você.',
    owner: "tz'music",
    cover: grad(5),
    trackIds: ['t4', 't8', 't10', 't12', 't14', 't16'],
  },
  {
    id: 'p8',
    name: 'Em Casa',
    description: 'Trilhas para dias lentos dentro de casa.',
    owner: 'Você',
    cover: grad(4),
    trackIds: ['t9', 't10', 't17', 't18'],
  },
  {
    id: 'p9',
    name: 'Treino Total',
    description: 'Energia alta do começo ao fim.',
    owner: "tz'music",
    cover: grad(2),
    trackIds: ['t15', 't5', 't2', 't13', 't1'],
  },
  {
    id: 'p10',
    name: 'Sessões Acústicas',
    description: 'Versões íntimas e cordas ao vivo.',
    owner: 'Você',
    cover: grad(8),
    trackIds: ['t17', 't18', 't9', 't3'],
  },
]

export const artists: Artist[] = [
  { id: 'a1', name: 'Aurora Vale', monthlyListeners: 1_842_301, cover: grad(0) },
  { id: 'a2', name: 'Kaya Sol', monthlyListeners: 953_118, cover: grad(1) },
  { id: 'a3', name: 'Luma Prisma', monthlyListeners: 1_204_776, cover: grad(2) },
  { id: 'a4', name: 'DJ Sereno', monthlyListeners: 687_540, cover: grad(9) },
  { id: 'a5', name: 'Vagalume', monthlyListeners: 431_905, cover: grad(6) },
  { id: 'a6', name: 'João Mirante', monthlyListeners: 218_664, cover: grad(4) },
]

export const albums: Album[] = [
  { id: 'al1', title: 'Cidades Acesas', artist: 'Aurora Vale', year: 2024, cover: grad(0) },
  { id: 'al2', title: 'Frequência', artist: 'Kaya Sol', year: 2025, cover: grad(1) },
  { id: 'al3', title: 'Satélites', artist: 'Luma Prisma', year: 2023, cover: grad(2) },
  { id: 'al4', title: 'Cassette Club', artist: 'DJ Sereno', year: 2025, cover: grad(9) },
  { id: 'al5', title: 'Fogo Fátuo', artist: 'Vagalume', year: 2024, cover: grad(6) },
  { id: 'al6', title: 'Sessões', artist: 'Orquestra Pequena', year: 2022, cover: grad(8) },
]

export const categories = [
  { id: 'c1', name: 'Pop', color: '#8d67ab' },
  { id: 'c2', name: 'Indie', color: '#e8115b' },
  { id: 'c3', name: 'Eletrônica', color: '#0d73ec' },
  { id: 'c4', name: 'Rock', color: '#e91429' },
  { id: 'c5', name: 'Hip-Hop', color: '#ba5d07' },
  { id: 'c6', name: 'Jazz', color: '#148a08' },
  { id: 'c7', name: 'Clássica', color: '#503750' },
  { id: 'c8', name: 'Podcasts', color: '#006450' },
]
