/** Uppercase Spanish letters for the grid. Accents drop; Ñ stays. */
export function normalize(input: string): string {
  const upper = input.toLocaleUpperCase('es').replaceAll('Ñ', '\u0000')
  return upper
    .normalize('NFD')
    .replace(/\p{M}/gu, '')
    .replaceAll('\u0000', 'Ñ')
    .replace(/[^A-ZÑ]/g, '')
}
