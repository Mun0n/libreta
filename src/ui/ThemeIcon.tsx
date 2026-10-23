type Props = { id: string }

export function ThemeIcon({ id }: Props) {
  return (
    <svg viewBox="0 0 64 64" aria-hidden="true">
      <Icon id={id} />
    </svg>
  )
}

function Icon({ id }: Props) {
  switch (id) {
    case 'animales':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M14 38c6-1 12-8 18-8 5 0 8 2 11 5-5 1-7 5-5 9 5 1 9 5 8 9H20c-7 0-12-5-6-15z" />
          <path d="M43 34c5-7 12-8 16-5" />
          <path d="M22 40c2 3 5 3 7 0" />
          <circle cx="47" cy="33" r="1.1" fill="currentColor" stroke="none" />
        </g>
      )
    case 'comida':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M32 16c2-6 8-8 11-6-3 5-7 7-9 7" />
          <path d="M32 18c-12 2-18 12-18 22 0 8 7 14 18 14s18-6 18-14c0-10-6-20-18-22z" />
          <path d="M32 18c2 8 2 16 0 24" />
        </g>
      )
    case 'cocina':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M18 30h28v4c0 10-6 16-14 16s-14-6-14-16z" />
          <path d="M14 30h36" />
          <path d="M24 30c1-8 15-8 16 0" />
          <path d="M16 36H10M48 36h6" />
        </g>
      )
    case 'casa':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M10 30 32 12l22 18" />
          <path d="M16 28v24h32V28" />
          <path d="M28 52V38h8v14" />
        </g>
      )
    case 'familia':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="12" y="14" width="40" height="32" rx="2" />
          <circle cx="26" cy="28" r="5" />
          <circle cx="40" cy="29" r="4" />
          <path d="M16 44c2-6 6-8 10-8 3 2 6 4 8 4 4-4 8-6 14-4" />
        </g>
      )
    case 'ropa':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M24 14 32 22 40 14l10 8-6 6v22H20V28l-6-6z" />
        </g>
      )
    case 'cuerpo':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M8 32c8-12 40-12 48 0-8 12-40 12-48 0z" />
          <circle cx="32" cy="32" r="7" />
          <circle cx="32" cy="32" r="2.2" fill="currentColor" stroke="none" />
        </g>
      )
    case 'jardin':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M18 50c14-2 18-16 18-34" />
          <path d="M36 28c10-1 14 8 8 14-8 1-12-6-8-14z" />
          <path d="M34 38c-12 2-14 12-6 15 8-1 10-8 6-15z" />
          <path d="M36 20c6-8 14-6 14 0-6 2-12 0-14 0z" />
        </g>
      )
    case 'ciudad':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M10 52V30h12v22M22 52V16h16v36M38 52V32h14v20" />
          <path d="M26 24h3M32 24h3M26 32h3M32 32h3M14 38h4M42 40h4" />
        </g>
      )
    case 'campo':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M32 54V16" />
          <path d="M32 20c-7-3-9-10-5-12 4 3 6 8 5 12zM32 20c7-3 9-10 5-12-4 3-6 8-5 12z" />
          <path d="M32 30c-7-3-9-10-5-12 4 3 6 8 5 12zM32 30c7-3 9-10 5-12-4 3-6 8-5 12z" />
          <path d="M32 40c-6-2-8-8-4-10 3 3 5 7 4 10zM32 40c6-2 8-8 4-10-3 3-5 7-4 10z" />
        </g>
      )
    case 'tiempo':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M20 40h28a9 9 0 0 0 1-18 12 12 0 0 0-23-3 8 8 0 0 0-6 21z" />
          <path d="M24 48v6M32 50v6M40 48v6" />
        </g>
      )
    case 'mar':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M8 26c6 6 10 6 16 0s10-6 16 0 10 6 16 0" />
          <path d="M8 40c6 6 10 6 16 0s10-6 16 0 10 6 16 0" />
        </g>
      )
    case 'oficios':
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M24 14h16l4 12H20z" />
          <path d="M18 26h28l-2 26H20z" />
          <path d="M28 14c0-4 8-4 8 0" />
          <path d="M32 32v12" />
        </g>
      )
    default:
      return (
        <g fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M12 20c8 4 12 4 20 0v26c-8 4-12 4-20 0z" />
          <path d="M32 20c8 4 12 4 20 0v26c-8 4-12 4-20 0z" />
          <path d="M32 20v26" />
          <path d="M44 42 54 28" />
        </g>
      )
  }
}
