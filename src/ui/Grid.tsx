import { useEffect, useRef, useState, type CSSProperties, type PointerEvent } from 'react'
import { directionsFor, type Cell, type DirectionMode } from '../game/generate'
import { lineBetween, snapDragMode } from '../game/select'

export type Stroke = {
  word: string
  cells: Cell[]
  color: string
  pencil: boolean
  pulse: boolean
}

type Drag = {
  id: number
  start: Cell
  x: number
  y: number
  dragging: boolean
}

type Props = {
  letters: string[][]
  strokes: Stroke[]
  hint: Cell | null
  mode: DirectionMode
  onCommit: (cells: Cell[]) => 'found' | 'already' | 'miss'
}

export function Grid({ letters, strokes, hint, mode, onCommit }: Props) {
  const n = letters.length
  const ref = useRef<HTMLDivElement>(null)
  const drag = useRef<Drag | null>(null)
  const anchorRef = useRef<Cell | null>(null)
  const previewRef = useRef<Cell[] | null>(null)
  const [anchor, setAnchor] = useState<Cell | null>(null)
  const [preview, setPreview] = useState<Cell[] | null>(null)
  const [ghost, setGhost] = useState<{ id: number; cells: Cell[] } | null>(null)
  const ghostId = useRef(0)

  useEffect(() => {
    if (!ghost) return
    const id = window.setTimeout(() => setGhost(null), 460)
    return () => window.clearTimeout(id)
  }, [ghost])

  function cellAt(clientX: number, clientY: number): Cell | null {
    const el = ref.current
    if (!el) return null
    const rect = el.getBoundingClientRect()
    if (rect.width <= 0 || rect.height <= 0) return null
    const x = Math.min(rect.width - 0.01, Math.max(0, clientX - rect.left))
    const y = Math.min(rect.height - 0.01, Math.max(0, clientY - rect.top))
    return {
      r: Math.min(n - 1, Math.floor((y / rect.height) * n)),
      c: Math.min(n - 1, Math.floor((x / rect.width) * n)),
    }
  }

  function finish(cells: Cell[]) {
    if (cells.length < 2) return
    if (onCommit(cells) === 'miss') {
      ghostId.current += 1
      setGhost({ id: ghostId.current, cells })
    }
  }

  function onPointerDown(event: PointerEvent<HTMLDivElement>) {
    if (event.button !== 0) return
    const cell = cellAt(event.clientX, event.clientY)
    if (!cell) return
    try {
      event.currentTarget.setPointerCapture(event.pointerId)
    } catch {
      // A pointer that is already gone should not cancel the gesture.
    }
    drag.current = { id: event.pointerId, start: cell, x: event.clientX, y: event.clientY, dragging: false }
    previewRef.current = null
    setPreview(null)
  }

  function onPointerMove(event: PointerEvent<HTMLDivElement>) {
    const current = drag.current
    if (!current || event.pointerId !== current.id) return
    if (!current.dragging && Math.hypot(event.clientX - current.x, event.clientY - current.y) > 12) {
      current.dragging = true
      anchorRef.current = null
      setAnchor(null)
    }
    if (!current.dragging) return
    const cell = cellAt(event.clientX, event.clientY)
    if (!cell) return
    const line = snapDragMode(current.start, cell, n, mode)
    previewRef.current = line
    setPreview(line)
  }

  function onPointerUp(event: PointerEvent<HTMLDivElement>) {
    const current = drag.current
    if (!current || event.pointerId !== current.id) return
    drag.current = null
    const cell = cellAt(event.clientX, event.clientY) ?? current.start
    if (!current.dragging) {
      tap(cell)
      return
    }
    const line = previewRef.current ?? snapDragMode(current.start, cell, n, mode)
    previewRef.current = null
    setPreview(null)
    finish(line)
  }

  function onPointerCancel() {
    drag.current = null
    previewRef.current = null
    setPreview(null)
  }

  function tap(cell: Cell) {
    const current = anchorRef.current
    if (!current) {
      anchorRef.current = cell
      setAnchor(cell)
      return
    }
    if (current.r === cell.r && current.c === cell.c) {
      anchorRef.current = null
      setAnchor(null)
      return
    }
    const line = lineBetween(current, cell, directionsFor(mode))
    if (!line) {
      anchorRef.current = cell
      setAnchor(cell)
      return
    }
    anchorRef.current = null
    setAnchor(null)
    finish(line)
  }

  return (
    <div className="grid-slot">
      <div
        ref={ref}
        className="grid"
        style={{ '--n': n } as CSSProperties}
        aria-hidden="true"
        onPointerDown={onPointerDown}
        onPointerMove={onPointerMove}
        onPointerUp={onPointerUp}
        onPointerCancel={onPointerCancel}
        onContextMenu={(event) => event.preventDefault()}
      >
        <svg className="marks" viewBox={`0 0 ${n} ${n}`}>
          {strokes.map((stroke) => (
            <Mark
              key={stroke.pulse ? `${stroke.word}-pulse` : stroke.word}
              cells={stroke.cells}
              color={stroke.color}
              width={stroke.pencil ? 0.34 : 0.72}
              className={stroke.pulse ? 'pulse' : undefined}
            />
          ))}
          {preview && preview.length > 1 ? (
            <Mark cells={preview} color="#ffdf4a" width={0.72} className="preview" />
          ) : null}
          {ghost ? <Mark key={ghost.id} cells={ghost.cells} color="#ffdf4a" width={0.72} className="ghost" /> : null}
          {anchor ? (
            <circle cx={anchor.c + 0.5} cy={anchor.r + 0.5} r="0.4" className="anchor" />
          ) : null}
          {hint ? <circle cx={hint.c + 0.5} cy={hint.r + 0.5} r="0.42" className="hint-ring" /> : null}
        </svg>
        {letters.map((row, r) =>
          row.map((letter, c) => (
            <span key={`${r}-${c}`} className="cell" data-row={r} data-col={c}>
              {letter}
            </span>
          )),
        )}
      </div>
    </div>
  )
}

function Mark({
  cells,
  color,
  width,
  className,
}: {
  cells: Cell[]
  color: string
  width: number
  className?: string
}) {
  const start = cells[0]
  const end = cells[cells.length - 1]
  if (!start || !end) return null
  return (
    <line
      x1={start.c + 0.5}
      y1={start.r + 0.5}
      x2={end.c + 0.5}
      y2={end.r + 0.5}
      stroke={color}
      strokeWidth={width}
      className={className}
    />
  )
}
