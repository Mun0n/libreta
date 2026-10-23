package dev.mun0n.libreta.game

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sign

/** Exact line between two taps. Null when the cells are not on an allowed direction. */
fun lineBetween(a: Cell, b: Cell, allowed: List<Direction>): List<Cell>? {
    val dr = b.r - a.r
    val dc = b.c - a.c
    if (dr == 0 && dc == 0) return null
    val stepR = sign(dr.toDouble()).toInt()
    val stepC = sign(dc.toDouble()).toInt()
    val distR = abs(dr)
    val distC = abs(dc)
    if (distR != 0 && distC != 0 && distR != distC) return null
    if (allowed.none { it.dr == stepR && it.dc == stepC }) return null
    val steps = maxOf(distR, distC)
    return List(steps + 1) { i -> Cell(a.r + stepR * i, a.c + stepC * i) }
}

/**
 * Lock a drag to the allowed direction whose ray passes closest to the finger,
 * then extend that ray to the projected length. Ties prefer the longer stroke.
 */
fun snapDrag(start: Cell, current: Cell, size: Int, allowed: List<Direction>): List<Cell> {
    val dr = current.r - start.r
    val dc = current.c - start.c
    if (dr == 0 && dc == 0) return listOf(start)

    var bestDir: Direction? = null
    var bestDist = Int.MAX_VALUE
    var bestSteps = 0

    for (dir in allowed) {
        val denom = dir.dr * dir.dr + dir.dc * dir.dc
        val dot = dr * dir.dr + dc * dir.dc
        val steps = jsRound(dot.toDouble() / denom)
        if (steps <= 0) continue
        val dist = abs(dr - dir.dr * steps) + abs(dc - dir.dc * steps)
        if (dist < bestDist || (dist == bestDist && steps > bestSteps)) {
            bestDist = dist
            bestDir = dir
            bestSteps = steps
        }
    }

    val dir = bestDir
    if (dir == null || bestSteps <= 0) return listOf(start)
    val cells = ArrayList<Cell>(bestSteps + 1)
    cells += start
    for (i in 1..bestSteps) {
        val r = start.r + dir.dr * i
        val c = start.c + dir.dc * i
        if (r < 0 || c < 0 || r >= size || c >= size) break
        cells += Cell(r, c)
    }
    return cells
}

fun snapDragMode(start: Cell, current: Cell, size: Int, mode: DirectionMode): List<Cell> =
    snapDrag(start, current, size, directionsFor(mode))

/** The whole span must be the word, forwards or backwards. A shorter piece inside it does not count. */
fun findHit(grid: List<List<Char>>, cells: List<Cell>, placed: List<PlacedWord>): PlacedWord? {
    if (cells.size < 2) return null
    val forward = buildString {
        for (cell in cells) {
            val letter = grid.getOrNull(cell.r)?.getOrNull(cell.c)
            if (letter != null) append(letter)
        }
    }
    val backward = forward.reversed()
    val hits = placed.filter { it.word == forward || it.word == backward }
    if (hits.isEmpty()) return null
    return hits.find { samePath(it.cells, cells) }
        ?: hits.find { it.word == forward }
        ?: hits.first()
}

private fun samePath(a: List<Cell>, b: List<Cell>): Boolean {
    if (a.size != b.size) return false
    if (a.indices.all { a[it].r == b[it].r && a[it].c == b[it].c }) return true
    return a.indices.all { i ->
        val other = b[b.lastIndex - i]
        a[i].r == other.r && a[i].c == other.c
    }
}

/** JS Math.round: floor(x + 0.5). Steps we keep are positive, so this matches. */
private fun jsRound(x: Double): Int = floor(x + 0.5).toInt()
