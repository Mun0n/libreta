import { useEffect, useMemo, useReducer } from 'react'
import { themes } from '../data/themes'
import { entries, generate, type Cell, type DirectionMode } from '../game/generate'
import {
  SIZE_SPECS,
  load,
  persist,
  type CurrentPuzzle,
  type Save,
  type Settings,
  type SizeMode,
} from './storage'

export type Screen = 'home' | 'puzzle' | 'settings'

type State = Save & { screen: Screen }

type Action =
  | { type: 'start'; current: CurrentPuzzle }
  | { type: 'resume' }
  | { type: 'home' }
  | { type: 'settings' }
  | { type: 'back' }
  | { type: 'pick' }
  | { type: 'mark'; word: string; cells: Cell[]; revealed: boolean }
  | { type: 'size'; size: SizeMode }
  | { type: 'directions'; directions: DirectionMode }
  | { type: 'sound'; sound: boolean }

function init(): State {
  const save = load()
  return { ...save, screen: save.current?.completed ? 'puzzle' : 'home' }
}

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'start':
      return { ...state, screen: 'puzzle', current: action.current }
    case 'resume':
      if (!state.current || state.current.completed) return state
      return { ...state, screen: 'puzzle' }
    case 'home':
      return { ...state, screen: 'home' }
    case 'settings':
      return { ...state, screen: 'settings' }
    case 'back':
      return { ...state, screen: 'home' }
    case 'pick':
      return { ...state, screen: 'home', current: null }
    case 'mark': {
      if (!state.current) return state
      if (state.current.found.some((item) => item.word === action.word)) return state
      const found = [
        ...state.current.found,
        { word: action.word, revealed: action.revealed, cells: action.cells },
      ]
      const completed = found.length >= state.current.total
      const justFinished = completed && !state.current.counted
      return {
        ...state,
        completedCount: state.completedCount + (justFinished ? 1 : 0),
        current: { ...state.current, found, completed, counted: state.current.counted || completed },
      }
    }
    case 'size':
      return { ...state, settings: { ...state.settings, size: action.size } }
    case 'directions':
      return { ...state, settings: { ...state.settings, directions: action.directions } }
    case 'sound':
      return { ...state, settings: { ...state.settings, sound: action.sound } }
    default:
      return state
  }
}

function begin(themeId: string, settings: Settings, previousSeed?: number): CurrentPuzzle | null {
  const theme = themes.find((item) => item.id === themeId)
  if (!theme) return null
  const spec = SIZE_SPECS[settings.size]
  const seed = ((Date.now() ^ ((previousSeed ?? 0) + 1)) >>> 0) || 1
  const puzzle = generate({
    seed,
    words: entries(theme.words),
    size: spec.size,
    directions: settings.directions,
    count: spec.count,
  })
  if (puzzle.placed.length === 0) return null
  return {
    seed,
    themeId,
    size: spec.size,
    count: spec.count,
    directions: settings.directions,
    found: [],
    completed: false,
    counted: false,
    total: puzzle.placed.length,
  }
}

export function useLibreta() {
  const [state, dispatch] = useReducer(reducer, undefined, init)

  useEffect(() => {
    const save: Save = {
      version: 1,
      settings: state.settings,
      current: state.current,
      completedCount: state.completedCount,
    }
    persist(save)
  }, [state.settings, state.current, state.completedCount])

  const theme = state.current ? (themes.find((item) => item.id === state.current?.themeId) ?? null) : null

  const puzzle = useMemo(() => {
    if (!state.current || !theme) return null
    return generate({
      seed: state.current.seed,
      words: entries(theme.words),
      size: state.current.size,
      directions: state.current.directions,
      count: state.current.count,
    })
  }, [state.current, theme])

  return {
    screen: state.screen,
    settings: state.settings,
    current: state.current,
    theme,
    puzzle,
    completedCount: state.completedCount,
    resumeAvailable: Boolean(state.current && !state.current.completed),
    startTheme: (themeId: string) => {
      const current = begin(themeId, state.settings)
      if (current) dispatch({ type: 'start', current })
    },
    another: () => {
      if (!state.current) return
      const current = begin(state.current.themeId, state.settings, state.current.seed)
      if (current) dispatch({ type: 'start', current })
    },
    resume: () => dispatch({ type: 'resume' }),
    goHome: () => dispatch({ type: 'home' }),
    goSettings: () => dispatch({ type: 'settings' }),
    back: () => dispatch({ type: 'back' }),
    pickTheme: () => dispatch({ type: 'pick' }),
    mark: (word: string, cells: Cell[], revealed: boolean) => dispatch({ type: 'mark', word, cells, revealed }),
    setSize: (size: SizeMode) => dispatch({ type: 'size', size }),
    setDirections: (directions: DirectionMode) => dispatch({ type: 'directions', directions }),
    setSound: (sound: boolean) => dispatch({ type: 'sound', sound }),
  }
}
