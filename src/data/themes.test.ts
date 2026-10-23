import { describe, expect, it } from 'vitest'
import { entries, generate } from '../game/generate'
import { normalize } from '../game/normalize'
import { themes } from './themes'

describe('themes', () => {
  it('has fourteen everyday topics with enough placeable words', () => {
    expect(new Set(themes.map((theme) => theme.id)).size).toBe(themes.length)
    expect(themes).toHaveLength(14)

    for (const theme of themes) {
      expect(theme.name.length).toBeGreaterThan(0)
      for (const word of theme.words) expect(word).toMatch(/^[a-záéíóúüñ]+$/i)

      const list = entries(theme.words)
      const short = list.filter((word) => word.norm.length <= 8)
      expect(list.length, theme.id).toBeGreaterThanOrEqual(60)
      expect(short.length, `${theme.id} short`).toBeGreaterThanOrEqual(25)
      expect(Math.max(...list.map((word) => word.norm.length)), theme.id).toBeLessThanOrEqual(12)
      expect(list.every((word) => word.norm === normalize(word.display))).toBe(true)
    }
  })

  it('builds a full puzzle for every theme, size and direction', () => {
    for (const theme of themes) {
      const words = entries(theme.words)
      for (const size of [8, 10, 12]) {
        for (const directions of ['book', 'straight'] as const) {
          for (const seed of [1, 7]) {
            const puzzle = generate({ seed, words, size, directions, count: size })
            expect(puzzle.placed.length, `${theme.id} ${size} ${directions} ${seed}`).toBe(size)
            expect(puzzle.grid).toHaveLength(size)
          }
        }
      }
    }
  })
})
