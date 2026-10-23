import { describe, expect, it } from 'vitest'
import { BOOK_DIRECTIONS, FILLER, entries, fits, generate } from './generate'
import { normalize } from './normalize'

const fillerSet = new Set([...FILLER])

describe('normalize', () => {
  it('drops accents and keeps ñ', () => {
    expect(normalize('canción')).toBe('CANCION')
    expect(normalize('niño')).toBe('NIÑO')
    expect(normalize('ñandú')).toBe('ÑANDU')
    expect(normalize('pingüino')).toBe('PINGUINO')
    expect(normalize('cañón')).toBe('CAÑON')
  })
})

describe('fits', () => {
  it('rejects a crossing when the letter differs', () => {
    const grid: (string | null)[][] = [
      ['C', 'A', 'S', 'A'],
      [null, null, null, null],
      [null, null, null, null],
      [null, null, null, null],
    ]
    const east = BOOK_DIRECTIONS.find((dir) => dir.name === 'E')!
    const south = BOOK_DIRECTIONS.find((dir) => dir.name === 'S')!
    expect(fits(grid, 'CASA', 0, 0, east, 4)).toBe(true)
    expect(fits(grid, 'COLA', 0, 0, east, 4)).toBe(false)
    expect(fits(grid, 'CAMA', 0, 0, south, 4)).toBe(true)
  })
})

describe('generate', () => {
  const bank = entries(['niño', 'ñandú', 'uña', 'mañana', 'pañuelo', 'araña', 'canción', 'pingüino'])

  it('is deterministic for the same seed', () => {
    const opts = { seed: 42, words: bank, size: 10, directions: 'book' as const, count: 8 }
    expect(generate(opts)).toEqual(generate(opts))
  })

  it('places every chosen word on its cells, including ñ', () => {
    const puzzle = generate({ seed: 11, words: bank, size: 10, directions: 'book', count: 8 })
    expect(puzzle.placed).toHaveLength(8)
    expect(puzzle.grid.some((row) => row.includes('Ñ'))).toBe(true)
    for (const word of puzzle.placed) {
      const letters = word.cells.map((cell) => puzzle.grid[cell.r][cell.c]).join('')
      expect(letters).toBe(word.word)
      expect(word.display.normalize('NFC')).toBe(word.display)
    }
  })

  it('discards words longer than the grid', () => {
    const words = entries(['sol', 'mar', 'pan', 'luz', 'supercalifragilistico'])
    const puzzle = generate({ seed: 2, words, size: 8, directions: 'book', count: 4 })
    expect(puzzle.placed.map((word) => word.word)).not.toContain('SUPERCALIFRAGILISTICO')
    expect(puzzle.placed).toHaveLength(4)
  })

  it('fills empty cells with Spanish letters and never K or W', () => {
    const puzzle = generate({ seed: 5, words: bank, size: 8, directions: 'straight', count: 6 })
    const used = new Set(puzzle.placed.flatMap((word) => word.cells.map((cell) => `${cell.r},${cell.c}`)))
    for (let r = 0; r < puzzle.size; r++) {
      for (let c = 0; c < puzzle.size; c++) {
        const letter = puzzle.grid[r][c]
        expect(letter).toMatch(/^[A-ZÑ]$/)
        if (!used.has(`${r},${c}`)) {
          expect(fillerSet.has(letter)).toBe(true)
          expect(letter === 'K' || letter === 'W').toBe(false)
        }
      }
    }
  })

  it('keeps straight mode on rows and columns', () => {
    const puzzle = generate({ seed: 9, words: bank, size: 8, directions: 'straight', count: 6 })
    for (const word of puzzle.placed) {
      const dr = Math.sign(word.cells[1].r - word.cells[0].r)
      const dc = Math.sign(word.cells[1].c - word.cells[0].c)
      expect(dr === 0 || dc === 0).toBe(true)
      expect(dr !== 0 || dc !== 0).toBe(true)
    }
  })
})
