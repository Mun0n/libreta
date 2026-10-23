import sharp from 'sharp'

const icon = (inset) => `<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 512 512">
  <rect width="512" height="512" fill="#e4d2bc"/>
  <rect x="${inset}" y="${inset}" width="${512 - inset * 2}" height="${512 - inset * 2}" rx="28" fill="#f6f1e4"/>
  <line x1="${inset + 70}" y1="${inset + 118}" x2="${512 - inset - 70}" y2="${inset + 118}" stroke="#ffdf4a" stroke-width="46" stroke-linecap="round"/>
  <g fill="none" stroke="#1c1915" stroke-width="10" stroke-linecap="round">
    ${ticks(inset)}
  </g>
</svg>`

function ticks(inset) {
  const cols = [inset + 108, inset + 196, inset + 284]
  const rows = [inset + 108, inset + 210, inset + 312]
  return rows
    .flatMap((y) => cols.map((x) => `<path d="M${x} ${y}h36M${x} ${y + 22}h24"/>`))
    .join('')
}

const files = [
  ['public/icon-192.png', 192, 36],
  ['public/icon-512.png', 512, 36],
  ['public/apple-touch-icon.png', 180, 28],
  ['public/icon-512-maskable.png', 512, 78],
]

for (const [file, size, inset] of files) {
  await sharp(Buffer.from(icon(inset))).resize(size, size).png().toFile(file)
}
