import { normalize } from './normalize'
import { mulberry32, shuffle } from './random'

export type Cell = { r: number; c: number }

export type DirName = 'E' | 'W' | 'S' | 'N' | 'NE' | 'NW' | 'SE' | 'SW'

export type Direction = {
  dr: -1 | 0 | 1
  dc: -1 | 0 | 1
  name: DirName
}

export const BOOK_DIRECTIONS: Direction[] = [
  { dr: 0, dc: 1, name: 'E' },
  { dr: 0, dc: -1, name: 'W' },
  { dr: 1, dc: 0, name: 'S' },
  { dr: -1, dc: 0, name: 'N' },
  { dr: 1, dc: 1, name: 'SE' },
  { dr: 1, dc: -1, name: 'SW' },
  { dr: -1, dc: 1, name: 'NE' },
  { dr: -1, dc: -1, name: 'NW' },
]

export const STRAIGHT_DIRECTIONS: Direction[] = BOOK_DIRECTIONS.filter(
  (dir) => dir.dr === 0 || dir.dc === 0,
)

export type DirectionMode = 'book' | 'straight'

export function directionsFor(mode: DirectionMode): Direction[] {
  return mode === 'book' ? BOOK_DIRECTIONS : STRAIGHT_DIRECTIONS
}

export type WordEntry = { display: string; norm: string }

export type PlacedWord = {
  word: string
  display: string
  cells: Cell[]
  dir: DirName
}

export type Puzzle = {
  grid: string[][]
  placed: PlacedWord[]
  size: number
}

type Grid = (string | null)[][]

/** Spanish letter frequencies. Filler never invents K or W. */
export const FILLER =
  'EEEEEEEEEEEEEEAAAAAAAAAAAAOOOOOOOOOOSSSSSSSSSRRRRRRRRNNNNNNNNIIIIIIILLLLLLDDDDDCCCCCTTTTTUUUUPPPPMMMMBBBGGVVFFJJZZHHQQYYÑX'

export function entries(words: readonly string[]): WordEntry[] {
  const seen = new Set<string>()
  const out: WordEntry[] = []
  for (const raw of words) {
    const norm = normalize(raw)
    if (norm.length < 3 || seen.has(norm)) continue
    seen.add(norm)
    out.push({ display: raw.toLocaleUpperCase('es'), norm })
  }
  return out
}

export function fits(
  grid: Grid,
  word: string,
  r: number,
  c: number,
  dir: Direction,
  size: number,
): boolean {
  for (let i = 0; i < word.length; i++) {
    const rr = r + dir.dr * i
    const cc = c + dir.dc * i
    if (rr < 0 || cc < 0 || rr >= size || cc >= size) return false
    const existing = grid[rr][cc]
    if (existing !== null && existing !== word[i]) return false
  }
  return true
}

function cellsFor(word: string, r: number, c: number, dir: Direction): Cell[] {
  const cells: Cell[] = []
  for (let i = 0; i < word.length; i++) {
    cells.push({ r: r + dir.dr * i, c: c + dir.dc * i })
  }
  return cells
}

function emptyGrid(size: number): Grid {
  return Array.from({ length: size }, () => Array.from({ length: size }, () => null))
}

function orderWords(words: WordEntry[], size: number, rng: () => number): WordEntry[] {
  return shuffle(
    words.filter((word) => word.norm.length <= size),
    rng,
  )
}

function greedy(
  words: WordEntry[],
  size: number,
  dirs: Direction[],
  count: number,
  rng: () => number,
): { grid: Grid; placed: PlacedWord[] } {
  const grid = emptyGrid(size)
  const placed: PlacedWord[] = []

  for (const word of words) {
    if (placed.length >= count) break
    const spots: { r: number; c: number; dir: Direction }[] = []
    for (const dir of dirs) {
      for (let r = 0; r < size; r++) {
        for (let c = 0; c < size; c++) {
          if (fits(grid, word.norm, r, c, dir, size)) spots.push({ r, c, dir })
        }
      }
    }
    if (spots.length === 0) continue
    const spot = spots[Math.floor(rng() * spots.length)]
    const cells = cellsFor(word.norm, spot.r, spot.c, spot.dir)
    for (let i = 0; i < cells.length; i++) grid[cells[i].r][cells[i].c] = word.norm[i]
    placed.push({ word: word.norm, display: word.display, cells, dir: spot.dir.name })
  }

  return { grid, placed }
}

export function generate(opts: {
  seed: number
  words: WordEntry[]
  size: number
  directions: DirectionMode
  count: number
}): Puzzle {
  const dirs = directionsFor(opts.directions)
  const available = opts.words.filter((word) => word.norm.length <= opts.size).length
  const target = Math.max(0, Math.min(opts.count, available))
  let best: { grid: Grid; placed: PlacedWord[] } = {
    grid: emptyGrid(opts.size),
    placed: [],
  }

  for (let attempt = 0; attempt < 40; attempt++) {
    const rng = mulberry32((opts.seed + attempt * 997) >>> 0)
    const words = orderWords(opts.words, opts.size, rng)
    const result = greedy(words, opts.size, dirs, target, rng)
    if (result.placed.length > best.placed.length) best = result
    if (result.placed.length >= target) break
  }

  const fillRng = mulberry32((opts.seed ^ 0x85ebca6b) >>> 0)
  const grid = best.grid.map((row) => row.slice())
  for (let r = 0; r < opts.size; r++) {
    for (let c = 0; c < opts.size; c++) {
      if (grid[r][c] === null) {
        grid[r][c] = FILLER[Math.floor(fillRng() * FILLER.length)] ?? 'E'
      }
    }
  }

  const placed = best.placed.slice().sort((a, b) => a.display.localeCompare(b.display, 'es'))

  return { grid: grid as string[][], placed, size: opts.size }
}
