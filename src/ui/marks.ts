import type { FoundWord } from '../state/storage'

export const MARKERS = [
  { wash: '#ffdf4a', strike: '#8d6a08' },
  { wash: '#ff9ec8', strike: '#a33d68' },
  { wash: '#9fe07a', strike: '#2f7a32' },
  { wash: '#8ecdf5', strike: '#1f6f9e' },
  { wash: '#ffb15e', strike: '#b35e12' },
  { wash: '#d2b0ff', strike: '#6d4c96' },
] as const

export const PENCIL = { wash: '#e4ddd0', strike: '#5c564c', stroke: '#5c564c' }

export type Mark = {
  wash: string
  strike: string
  stroke: string
  pencil: boolean
}

export function markFor(found: FoundWord[], word: string): Mark | null {
  const item = found.find((entry) => entry.word === word)
  if (!item) return null
  if (item.revealed) return { wash: PENCIL.wash, strike: PENCIL.strike, stroke: PENCIL.stroke, pencil: true }
  const index = found.filter((entry) => !entry.revealed).findIndex((entry) => entry.word === word)
  const marker = MARKERS[(index + MARKERS.length) % MARKERS.length]
  return { wash: marker.wash, strike: marker.strike, stroke: marker.wash, pencil: false }
}
