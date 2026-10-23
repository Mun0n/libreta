export const copy = {
  appName: 'Sopa de letras',
  howTo: 'Pasa el dedo por las letras, como con el boli.',
  chooseTheme: 'Elige un tema',
  continueWith: (theme: string) => `Seguir con ${theme}`,
  found: (word: string) => `Has encontrado ${word}`,
  finished: 'Sopa terminada',
  veryWell: 'Muy bien',
  progress: (done: number, total: number) => `${done} de ${total}`,
  home: 'Inicio',
  hint: 'Pista',
  reveal: 'Enséñame una',
  another: 'Otra',
  anotherFull: 'Otra sopa',
  pickTheme: 'Elegir tema',
  settings: 'Ajustes',
  back: 'Volver',
  letterSize: 'Tamaño de las letras',
  sizes: {
    xlarge: 'Letras muy grandes',
    large: 'Letras grandes',
    more: 'Más palabras',
  },
  sizeHint: {
    xlarge: 'Pocas letras, muy grandes',
    large: 'El tamaño de siempre',
    more: 'Más letras y más palabras',
  },
  directions: 'Direcciones',
  book: 'Como en el libro',
  straight: 'Solo rectas',
  bookHint: 'Hacia todos los lados, también en diagonal',
  straightHint: 'Solo de lado a lado y de arriba abajo',
  sound: 'Sonido',
  soundOn: 'Sonido encendido',
  soundOff: 'Sonido apagado',
  doneCount: (n: number) => {
    if (n === 0) return 'Todavía no has terminado ninguna sopa.'
    if (n === 1) return 'Has terminado 1 sopa.'
    return `Has terminado ${n} sopas.`
  },
  wordsLabel: 'Palabras',
}
