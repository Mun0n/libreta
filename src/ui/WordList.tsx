import { copy } from '../copy'
import type { Mark } from './marks'

type Word = { word: string; display: string }

type Props = {
  words: Word[]
  marks: Map<string, Mark>
}

export function WordList({ words, marks }: Props) {
  return (
    <ul className="words" aria-label={copy.wordsLabel}>
      {words.map((word) => {
        const mark = marks.get(word.word)
        return (
          <li
            key={word.word}
            className={mark ? 'found' : undefined}
            style={
              mark
                ? { background: mark.wash, textDecorationColor: mark.strike }
                : undefined
            }
          >
            {word.display}
            {mark ? <span className="sr-only">, encontrada</span> : null}
          </li>
        )
      })}
    </ul>
  )
}
