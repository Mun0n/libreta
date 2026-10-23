import type { Cell, DirectionMode } from '../game/generate'
import { themes } from '../data/themes'

export type SizeMode = 'xlarge' | 'large' | 'more'

export const SIZE_SPECS: Record<SizeMode, { size: number; count: number }> = {
  xlarge: { size: 8, count: 8 },
  large: { size: 10, count: 10 },
  more: { size: 12, count: 12 },
}

export type FoundWord = {
  word: string
  revealed: boolean
  cells: Cell[]
}

export type CurrentPuzzle = {
  seed: number
  themeId: string
  size: number
  count: number
  directions: DirectionMode
  found: FoundWord[]
  completed: boolean
  counted: boolean
  total: number
}

export type Settings = {
  size: SizeMode
  directions: DirectionMode
  sound: boolean
}

export type Save = {
  version: 1
  settings: Settings
  current: CurrentPuzzle | null
  completedCount: number
}

const KEY = 'libreta.v1'

export const defaultSettings: Settings = {
  size: 'large',
  directions: 'book',
  sound: false,
}

export function emptySave(): Save {
  return { version: 1, settings: { ...defaultSettings }, current: null, completedCount: 0 }
}

function isCell(value: unknown, size: number): value is Cell {
  if (!value || typeof value !== 'object') return false
  const cell = value as Cell
  return Number.isInteger(cell.r) && Number.isInteger(cell.c) && cell.r >= 0 && cell.c >= 0 && cell.r < size && cell.c < size
}

function readCurrent(value: unknown): CurrentPuzzle | null {
  if (!value || typeof value !== 'object') return null
  const current = value as CurrentPuzzle
  const size = current.size
  if (size !== 8 && size !== 10 && size !== 12) return null
  if (!themes.some((theme) => theme.id === current.themeId)) return null
  if (!Array.isArray(current.found) || typeof current.total !== 'number' || current.total < 1) return null

  const found = current.found.flatMap((item) => {
    if (!item || typeof item.word !== 'string' || !Array.isArray(item.cells)) return []
    const cells = item.cells.filter((cell) => isCell(cell, size))
    if (cells.length < 2) return []
    return [{ word: item.word, revealed: item.revealed === true, cells }]
  })

  const completed = found.length >= current.total || current.completed === true
  return {
    seed: current.seed >>> 0,
    themeId: current.themeId,
    size,
    count: current.count === 8 || current.count === 10 || current.count === 12 ? current.count : size,
    directions: current.directions === 'straight' ? 'straight' : 'book',
    found,
    completed,
    counted: current.counted === true || completed,
    total: current.total,
  }
}

export function load(): Save {
  try {
    const raw = localStorage.getItem(KEY)
    if (!raw) return emptySave()
    const parsed = JSON.parse(raw) as Partial<Save>
    if (parsed.version !== 1) return emptySave()
    const settings = parsed.settings
    return {
      version: 1,
      settings: {
        size: settings?.size === 'xlarge' || settings?.size === 'more' ? settings.size : 'large',
        directions: settings?.directions === 'straight' ? 'straight' : 'book',
        sound: settings?.sound === true,
      },
      current: readCurrent(parsed.current),
      completedCount: Number.isInteger(parsed.completedCount) ? Math.max(0, parsed.completedCount as number) : 0,
    }
  } catch {
    return emptySave()
  }
}

export function persist(save: Save): void {
  try {
    localStorage.setItem(KEY, JSON.stringify(save))
  } catch {
    // Private mode can reject storage. The puzzle still works for this visit.
  }
}
