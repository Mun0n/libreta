import { copy } from '../copy'
import type { DirectionMode } from '../game/generate'
import type { Settings as SettingsState, SizeMode } from '../state/storage'

type Props = {
  settings: SettingsState
  completedCount: number
  onSize: (size: SizeMode) => void
  onDirections: (directions: DirectionMode) => void
  onSound: (sound: boolean) => void
  onBack: () => void
}

const sizes: SizeMode[] = ['xlarge', 'large', 'more']

export function Settings({ settings, completedCount, onSize, onDirections, onSound, onBack }: Props) {
  return (
    <div className="settings-screen">
      <div className="settings-scroll">
        <h1>{copy.settings}</h1>
        <p className="done-count">{copy.doneCount(completedCount)}</p>
        <fieldset>
          <legend>{copy.letterSize}</legend>
          <div className="choices">
            {sizes.map((size) => (
              <button
                key={size}
                type="button"
                className="choice"
                aria-pressed={settings.size === size}
                onClick={() => onSize(size)}
              >
                <span>{copy.sizes[size]}</span>
                <small>{copy.sizeHint[size]}</small>
              </button>
            ))}
          </div>
        </fieldset>
        <fieldset>
          <legend>{copy.directions}</legend>
          <div className="choices">
            <button
              type="button"
              className="choice"
              aria-pressed={settings.directions === 'book'}
              onClick={() => onDirections('book')}
            >
              <span>{copy.book}</span>
              <small>{copy.bookHint}</small>
            </button>
            <button
              type="button"
              className="choice"
              aria-pressed={settings.directions === 'straight'}
              onClick={() => onDirections('straight')}
            >
              <span>{copy.straight}</span>
              <small>{copy.straightHint}</small>
            </button>
          </div>
        </fieldset>
        <fieldset>
          <legend>{copy.sound}</legend>
          <div className="choices">
            <button
              type="button"
              className="choice"
              aria-pressed={settings.sound}
              onClick={() => onSound(!settings.sound)}
            >
              <span>{settings.sound ? copy.soundOn : copy.soundOff}</span>
            </button>
          </div>
        </fieldset>
      </div>
      <div className="settings-bar">
        <button type="button" className="primary back" onClick={onBack}>
          {copy.back}
        </button>
      </div>
    </div>
  )
}
