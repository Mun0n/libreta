import { useEffect, useState } from 'react'
import { playFound } from '../audio'
import { copy } from '../copy'
import type { Cell, Puzzle as Generated } from '../game/generate'
import { findHit } from '../game/select'
import type { CurrentPuzzle, Settings } from '../state/storage'
import { Grid } from './Grid'
import { markFor } from './marks'
import { WordList } from './WordList'

type Props = {
  name: string
  puzzle: Generated
  current: CurrentPuzzle
  settings: Settings
  onHome: () => void
  onAnother: () => void
  onPickTheme: () => void
  onMark: (word: string, cells: Cell[], revealed: boolean) => void
}

export function Puzzle({ name, puzzle, current, settings, onHome, onAnother, onPickTheme, onMark }: Props) {
  const [announce, setAnnounce] = useState('')
  const [pulse, setPulse] = useState<string | null>(null)
  const [hint, setHint] = useState<{ word: string; cell: Cell } | null>(null)
  const [cursor, setCursor] = useState(0)
  const done = current.completed

  useEffect(() => {
    if (!pulse) return
    const id = window.setTimeout(() => setPulse(null), 420)
    return () => window.clearTimeout(id)
  }, [pulse])

  useEffect(() => {
    if (!hint) return
    const id = window.setTimeout(() => setHint(null), 2600)
    return () => window.clearTimeout(id)
  }, [hint])

  const marks = new Map(
    current.found.flatMap((item) => {
      const mark = markFor(current.found, item.word)
      return mark ? [[item.word, mark] as const] : []
    }),
  )

  const strokes = current.found.flatMap((item) => {
    const mark = marks.get(item.word)
    if (!mark) return []
    return [
      {
        word: item.word,
        cells: item.cells,
        color: mark.stroke,
        pencil: mark.pencil,
        pulse: pulse === item.word,
      },
    ]
  })

  function unfound() {
    const known = new Set(current.found.map((item) => item.word))
    return puzzle.placed.filter((word) => !known.has(word.word))
  }

  function commit(cells: Cell[]): 'found' | 'already' | 'miss' {
    const hit = findHit(puzzle.grid, cells, puzzle.placed)
    if (!hit) return 'miss'
    if (current.found.some((item) => item.word === hit.word)) {
      setPulse(hit.word)
      return 'already'
    }
    const finishing = current.found.length + 1 >= current.total
    setAnnounce(finishing ? `${copy.found(hit.display)}. ${copy.finished}` : copy.found(hit.display))
    if (settings.sound) playFound()
    onMark(hit.word, cells, false)
    return 'found'
  }

  function showHint() {
    const pending = unfound()
    if (pending.length === 0) return
    const word = pending[cursor % pending.length]
    if (!word) return
    const cell = word.cells[0]
    if (!cell) return
    setCursor((value) => value + 1)
    setHint({ word: word.word, cell })
    setAnnounce(`Pista: ${word.display}`)
  }

  function revealOne() {
    const pending = unfound()
    if (pending.length === 0) return
    const hinted = hint ? pending.find((word) => word.word === hint.word) : undefined
    const word = hinted ?? pending[0]
    if (!word) return
    const finishing = current.found.length + 1 >= current.total
    setAnnounce(finishing ? `${copy.found(word.display)}. ${copy.finished}` : copy.found(word.display))
    setHint(null)
    onMark(word.word, word.cells, true)
  }

  return (
    <div className="puzzle-screen">
      <div className="sheet">
        <header className="sheet-head">
          <div>
            <h1 className="theme-title">{name}</h1>
            <p className="howto">{copy.howTo}</p>
          </div>
          <p className="count">{done ? copy.veryWell : copy.progress(current.found.length, current.total)}</p>
        </header>
        <div className="sheet-body">
          <Grid
            letters={puzzle.grid}
            strokes={strokes}
            hint={hint?.cell ?? null}
            mode={current.directions}
            onCommit={commit}
          />
          <WordList words={puzzle.placed} marks={marks} />
        </div>
      </div>
      {done ? (
        <div className="bar done-bar">
          <button type="button" className="primary" onClick={onAnother}>
            {copy.anotherFull}
          </button>
          <button type="button" onClick={onPickTheme}>
            {copy.pickTheme}
          </button>
        </div>
      ) : (
        <div className="bar">
          <button type="button" onClick={onHome}>
            {copy.home}
          </button>
          <button type="button" onClick={showHint}>
            {copy.hint}
          </button>
          <button type="button" onClick={revealOne}>
            {copy.reveal}
          </button>
          <button type="button" onClick={onAnother}>
            {copy.another}
          </button>
        </div>
      )}
      <div className="sr-only" aria-live="polite">
        {announce}
      </div>
    </div>
  )
}
