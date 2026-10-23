# Libreta

A word search that feels like pen on paper. No ads, no account, no timer.

Spanish puzzles, large type, and a board you can play with a finger, a mouse, or an Apple Pencil. After the first load it works offline.

![Home, with the unfinished puzzle one tap away](docs/home.jpg)

![A sheet in progress. The marker sits under the ink, and the list is crossed out in the same colour](docs/puzzle.jpg)

## Who it is for

Someone who already finishes paper word searches and should not have to learn a new game. The board is the book: a sheet, a word list, a marker. Around that, almost nothing.

The interface is built for older adults, including people living with Alzheimer's:

- One job on each screen. The unfinished puzzle is a single button, **Seguir**.
- Large type ([Atkinson Hyperlegible](https://www.brailleinstitute.org/freefont/)) and large buttons with words, not icons alone.
- A wrong stroke fades out. There is no buzzer, no red cross, no score, and no clock.
- Drag through the letters, or tap the first letter and then the last. The second path is there because a long drag is hard with a tremor.
- **Pista** circles the first letter of a missing word. **Enséñame una** draws that word in pencil. Neither costs anything.
- Progress stays on the device. Nothing is uploaded.

This is a pastime, not a treatment and not a medical device. There is no good evidence that word searches change the course of dementia, and Libreta does not claim that they do.

## What it leaves out

The big Spanish word-search apps (Blackout's *Sopas de Letras*, Vita Word Search) are the reference for themes, an endless supply of grids, and selecting a word by dragging. They also ship ads, coins, level gates, and a difficulty that climbs on its own. Those are the parts that break a calm session, so they are not in here.

Pinch-zoom is off on purpose. An accidental pinch leaves a person inside a zoomed page with no obvious way back.

## Play

1. Choose a theme. Everyday Spanish: animals, food, the kitchen, the house, family, and so on.
2. Drag through a word in a straight line, or tap its first letter and then its last.
3. The line has to be the whole word. A shorter piece inside a longer one does not count.
4. When the sheet is finished it stays on screen, markers and all. **Otra sopa** deals another of the same theme.

The default is the printed book: all eight directions, a 10×10 grid. In **Ajustes**:

- **Letras muy grandes** — 8×8
- **Letras grandes** — 10×10, the default
- **Más palabras** — 12×12
- **Como en el libro** — all eight directions
- **Solo rectas** — across and down, both ways

Sound is off until it is turned on. There is no account to configure.

## How a sheet is made

`generate` in `src/game/generate.ts` is deterministic. The same seed is the same sheet, which is what **Seguir** restores.

- A seeded shuffle picks words that fit the grid (length 3 to the grid size).
- Words may cross when the letter agrees. A different letter blocks the crossing.
- Empty cells are filled from Spanish letter frequencies. The filler does not invent K or W.
- Accents are removed in the grid (`canción` → `CANCION`). **Ñ stays Ñ.** The list keeps the accents.

Fourteen themes live in `src/data/themes.ts`, each with at least 60 ordinary words. Drag direction locking lives in `src/game/select.ts`.

## Project layout

```
src/game/generate.ts    placement, filler, seed
src/game/select.ts      straight-line drag and exact match
src/game/normalize.ts   accents and Ñ
src/data/themes.ts      word lists
src/state/storage.ts    localStorage, versioned
src/ui/                 sheet, grid, home, settings
```

There is no server. Vite, React, and TypeScript. Workbox precaches the build so the second open does not need a network.

## Develop

```bash
npm install
npm test
npm run dev
npm run build
npm run preview
```

Tests cover the generator (stable seed, every placed word is readable on its cells, Ñ, words longer than the grid are dropped, a bad crossing is rejected) and the selector (direction lock, exact match, reverse match).

## Install

A tablet is the right size. On an iPhone, **Ajustes → Letras muy grandes** keeps the letters usable.

The short Spanish steps for adding it to an iPad home screen are in [docs/ipad.md](docs/ipad.md).

`npm run build` writes a static site to `dist/`. Host that folder anywhere. The app is a PWA: once it has loaded, the service worker serves it offline.

## Privacy

No account, no analytics, no requests after the app is cached. The current sheet and the count of finished sheets stay in `localStorage` on that device.

## License

[MIT](LICENSE).
