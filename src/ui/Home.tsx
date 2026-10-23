import { copy } from '../copy'
import { themes } from '../data/themes'
import { ThemeIcon } from './ThemeIcon'

type Pending = {
  name: string
  done: number
  total: number
}

type Props = {
  pending: Pending | null
  onResume: () => void
  onStart: (themeId: string) => void
  onSettings: () => void
}

export function Home({ pending, onResume, onStart, onSettings }: Props) {
  return (
    <div className="home-screen">
      <div className="home">
        <div className="home-top">
          <h1>{copy.appName}</h1>
          <p className="howto">{copy.howTo}</p>
          {pending ? (
            <button type="button" className="continue" onClick={onResume}>
              <span>{copy.continueWith(pending.name)}</span>
              <small>{copy.progress(pending.done, pending.total)}</small>
            </button>
          ) : null}
        </div>
        <h2>{copy.chooseTheme}</h2>
        <div className="themes">
          {themes.map((theme) => (
            <button key={theme.id} type="button" className="theme-card" onClick={() => onStart(theme.id)}>
              <ThemeIcon id={theme.id} />
              <span>{theme.name}</span>
            </button>
          ))}
        </div>
        <button type="button" className="settings-link" onClick={onSettings}>
          {copy.settings}
        </button>
      </div>
    </div>
  )
}
