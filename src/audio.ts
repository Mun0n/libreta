let ctx: AudioContext | null = null

/** A short, quiet tick. Silent if the browser blocks audio. */
export function playFound(): void {
  try {
    const AudioCtx = window.AudioContext
    if (!AudioCtx) return
    ctx ??= new AudioCtx()
    if (ctx.state === 'suspended') void ctx.resume()
    const now = ctx.currentTime
    const osc = ctx.createOscillator()
    const gain = ctx.createGain()
    osc.type = 'sine'
    osc.frequency.setValueAtTime(740, now)
    osc.frequency.exponentialRampToValueAtTime(520, now + 0.09)
    gain.gain.setValueAtTime(0.0001, now)
    gain.gain.exponentialRampToValueAtTime(0.04, now + 0.015)
    gain.gain.exponentialRampToValueAtTime(0.0001, now + 0.14)
    osc.connect(gain).connect(ctx.destination)
    osc.start(now)
    osc.stop(now + 0.15)
  } catch {
    // A missing or blocked audio context must not interrupt the puzzle.
  }
}
