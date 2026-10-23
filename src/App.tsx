import { useLibreta } from './state/useLibreta'
import { Home } from './ui/Home'
import { Puzzle } from './ui/Puzzle'
import { Settings } from './ui/Settings'

export function App() {
  const game = useLibreta()

  if (game.screen === 'settings') {
    return (
      <Settings
        settings={game.settings}
        completedCount={game.completedCount}
        onSize={game.setSize}
        onDirections={game.setDirections}
        onSound={game.setSound}
        onBack={game.back}
      />
    )
  }

  if (game.screen === 'puzzle' && game.current && game.theme && game.puzzle) {
    return (
      <Puzzle
        key={`${game.current.themeId}-${game.current.seed}`}
        name={game.theme.name}
        puzzle={game.puzzle}
        current={game.current}
        settings={game.settings}
        onHome={game.goHome}
        onAnother={game.another}
        onPickTheme={game.pickTheme}
        onMark={game.mark}
      />
    )
  }

  const pending =
    game.resumeAvailable && game.current && game.theme
      ? { name: game.theme.name, done: game.current.found.length, total: game.current.total }
      : null

  return (
    <Home pending={pending} onResume={game.resume} onStart={game.startTheme} onSettings={game.goSettings} />
  )
}
