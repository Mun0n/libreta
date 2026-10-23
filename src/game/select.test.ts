import { describe, expect, it } from 'vitest'
import { BOOK_DIRECTIONS, STRAIGHT_DIRECTIONS, type PlacedWord } from './generate'
import { findHit, lineBetween, snapDrag } from './select'

const grid = [
  ['G', 'A', 'T', 'O', 'S'],
  ['R', 'M', 'A', 'R', 'O'],
  ['L', 'U', 'Z', 'N', 'L'],
  ['A', 'R', 'O', 'M', 'A'],
  ['P', 'A', 'N', 'E', 'S'],
]

const gato: PlacedWord = {
  word: 'GATO',
  display: 'GATO',
  dir: 'E',
  cells: [
    { r: 0, c: 0 },
    { r: 0, c: 1 },
    { r: 0, c: 2 },
    { r: 0, c: 3 },
  ],
}

describe('lineBetween', () => {
  it('draws a straight or exact diagonal line', () => {
    expect(lineBetween({ r: 0, c: 0 }, { r: 0, c: 3 }, BOOK_DIRECTIONS)).toHaveLength(4)
    expect(lineBetween({ r: 0, c: 0 }, { r: 3, c: 3 }, BOOK_DIRECTIONS)?.at(-1)).toEqual({ r: 3, c: 3 })
    expect(lineBetween({ r: 0, c: 0 }, { r: 1, c: 2 }, BOOK_DIRECTIONS)).toBeNull()
  })

  it('refuses diagonals when only straight directions are allowed', () => {
    expect(lineBetween({ r: 0, c: 0 }, { r: 3, c: 3 }, STRAIGHT_DIRECTIONS)).toBeNull()
    expect(lineBetween({ r: 2, c: 0 }, { r: 2, c: 4 }, STRAIGHT_DIRECTIONS)).toHaveLength(5)
  })
})

describe('snapDrag', () => {
  it('locks a mostly horizontal drag to the row', () => {
    const cells = snapDrag({ r: 0, c: 0 }, { r: 2, c: 4 }, 8, BOOK_DIRECTIONS)
    expect(cells[0]).toEqual({ r: 0, c: 0 })
    expect(cells.at(-1)).toEqual({ r: 0, c: 4 })
    expect(cells.every((cell) => cell.r === 0)).toBe(true)
  })

  it('locks an even diagonal to the diagonal', () => {
    const cells = snapDrag({ r: 0, c: 0 }, { r: 3, c: 3 }, 8, BOOK_DIRECTIONS)
    expect(cells.map((cell) => [cell.r, cell.c])).toEqual([
      [0, 0],
      [1, 1],
      [2, 2],
      [3, 3],
    ])
  })

  it('stays inside the grid', () => {
    const cells = snapDrag({ r: 6, c: 6 }, { r: 0, c: 6 }, 8, BOOK_DIRECTIONS)
    expect(cells.at(-1)?.r).toBeGreaterThanOrEqual(0)
    expect(cells[0]).toEqual({ r: 6, c: 6 })
    expect(cells.every((cell) => cell.c === 6)).toBe(true)
  })
})

describe('findHit', () => {
  it('matches the whole word forwards or backwards and ignores a shorter piece', () => {
    expect(findHit(grid, gato.cells, [gato])?.word).toBe('GATO')
    expect(findHit(grid, [...gato.cells].reverse(), [gato])?.word).toBe('GATO')
    expect(findHit(grid, gato.cells.slice(0, 3), [gato])).toBeNull()
  })
})
