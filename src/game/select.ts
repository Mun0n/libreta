import { directionsFor, type Cell, type Direction, type DirectionMode, type PlacedWord } from './generate'

/** Exact line between two taps. Null when the cells are not on an allowed direction. */
export function lineBetween(a: Cell, b: Cell, allowed: Direction[]): Cell[] | null {
  const dr = b.r - a.r
  const dc = b.c - a.c
  if (dr === 0 && dc === 0) return null
  const stepR = Math.sign(dr) as -1 | 0 | 1
  const stepC = Math.sign(dc) as -1 | 0 | 1
  const distR = Math.abs(dr)
  const distC = Math.abs(dc)
  if (distR !== 0 && distC !== 0 && distR !== distC) return null
  if (!allowed.some((dir) => dir.dr === stepR && dir.dc === stepC)) return null
  const steps = Math.max(distR, distC)
  const cells: Cell[] = []
  for (let i = 0; i <= steps; i++) cells.push({ r: a.r + stepR * i, c: a.c + stepC * i })
  return cells
}

/**
 * Lock a drag to the allowed direction whose ray passes closest to the finger,
 * then extend that ray to the projected length. Ties prefer the longer stroke.
 */
export function snapDrag(start: Cell, current: Cell, size: number, allowed: Direction[]): Cell[] {
  const dr = current.r - start.r
  const dc = current.c - start.c
  if (dr === 0 && dc === 0) return [start]

  let bestDir = allowed[0]
  let bestDist = Infinity
  let bestSteps = 0

  for (const dir of allowed) {
    const denom = dir.dr * dir.dr + dir.dc * dir.dc
    const dot = dr * dir.dr + dc * dir.dc
    const steps = Math.round(dot / denom)
    if (steps <= 0) continue
    const dist = Math.abs(dr - dir.dr * steps) + Math.abs(dc - dir.dc * steps)
    if (dist < bestDist || (dist === bestDist && steps > bestSteps)) {
      bestDist = dist
      bestDir = dir
      bestSteps = steps
    }
  }

  if (!bestDir || bestSteps <= 0) return [start]
  const cells: Cell[] = [start]
  for (let i = 1; i <= bestSteps; i++) {
    const r = start.r + bestDir.dr * i
    const c = start.c + bestDir.dc * i
    if (r < 0 || c < 0 || r >= size || c >= size) break
    cells.push({ r, c })
  }
  return cells
}

export function snapDragMode(start: Cell, current: Cell, size: number, mode: DirectionMode): Cell[] {
  return snapDrag(start, current, size, directionsFor(mode))
}

/** The whole span must be the word, forwards or backwards. A shorter piece inside it does not count. */
export function findHit(grid: string[][], cells: Cell[], placed: PlacedWord[]): PlacedWord | null {
  if (cells.length < 2) return null
  const forward = cells.map((cell) => grid[cell.r]?.[cell.c] ?? '').join('')
  const backward = [...forward].reverse().join('')
  const hits = placed.filter((word) => word.word === forward || word.word === backward)
  if (hits.length === 0) return null
  return (
    hits.find((word) => samePath(word.cells, cells)) ??
    hits.find((word) => word.word === forward) ??
    hits[0]
  )
}

function samePath(a: Cell[], b: Cell[]): boolean {
  if (a.length !== b.length) return false
  const forward = a.every((cell, i) => cell.r === b[i].r && cell.c === b[i].c)
  if (forward) return true
  return a.every((cell, i) => {
    const other = b[b.length - 1 - i]
    return cell.r === other.r && cell.c === other.c
  })
}
