package dev.mun0n.libreta.game

import java.text.Collator
import java.util.Locale
import kotlin.math.floor
import kotlinx.serialization.Serializable

@Serializable
data class Cell(val r: Int, val c: Int)

enum class DirName { E, W, S, N, NE, NW, SE, SW }

data class Direction(val dr: Int, val dc: Int, val name: DirName)

val bookDirections: List<Direction> = listOf(
    Direction(0, 1, DirName.E),
    Direction(0, -1, DirName.W),
    Direction(1, 0, DirName.S),
    Direction(-1, 0, DirName.N),
    Direction(1, 1, DirName.SE),
    Direction(1, -1, DirName.SW),
    Direction(-1, 1, DirName.NE),
    Direction(-1, -1, DirName.NW),
)

val straightDirections: List<Direction> = bookDirections.filter { it.dr == 0 || it.dc == 0 }

@Serializable
enum class DirectionMode { book, straight }

fun directionsFor(mode: DirectionMode): List<Direction> =
    if (mode == DirectionMode.book) bookDirections else straightDirections

data class WordEntry(val display: String, val norm: String)

data class PlacedWord(
    val word: String,
    val display: String,
    val cells: List<Cell>,
    val dir: DirName,
)

data class Puzzle(
    val grid: List<List<Char>>,
    val placed: List<PlacedWord>,
    val size: Int,
)

/** Spanish letter frequencies. Filler never invents K or W. */
const val FILLER =
    "EEEEEEEEEEEEEEAAAAAAAAAAAAOOOOOOOOOOSSSSSSSSSRRRRRRRRNNNNNNNNIIIIIIILLLLLLDDDDDCCCCCTTTTTUUUUPPPPMMMMBBBGGVVFFJJZZHHQQYYÑX"

private val spanishOrder: Collator = Collator.getInstance(Locale.forLanguageTag("es"))

fun entries(words: List<String>): List<WordEntry> {
    val seen = HashSet<String>()
    val out = ArrayList<WordEntry>()
    for (raw in words) {
        val norm = normalize(raw)
        if (norm.length < 3 || !seen.add(norm)) continue
        out += WordEntry(displayWord(raw), norm)
    }
    return out
}

fun fits(
    grid: Array<Array<Char?>>,
    word: String,
    r: Int,
    c: Int,
    dir: Direction,
    size: Int,
): Boolean {
    for (i in word.indices) {
        val rr = r + dir.dr * i
        val cc = c + dir.dc * i
        if (rr < 0 || cc < 0 || rr >= size || cc >= size) return false
        val existing = grid[rr][cc]
        if (existing != null && existing != word[i]) return false
    }
    return true
}

private fun cellsFor(word: String, r: Int, c: Int, dir: Direction): List<Cell> =
    List(word.length) { i -> Cell(r + dir.dr * i, c + dir.dc * i) }

private fun emptyGrid(size: Int): Array<Array<Char?>> = Array(size) { arrayOfNulls(size) }

private fun orderWords(words: List<WordEntry>, size: Int, rng: () -> Double): List<WordEntry> =
    shuffle(words.filter { it.norm.length <= size }, rng)

private fun greedy(
    words: List<WordEntry>,
    size: Int,
    dirs: List<Direction>,
    count: Int,
    rng: () -> Double,
): Pair<Array<Array<Char?>>, List<PlacedWord>> {
    val grid = emptyGrid(size)
    val placed = ArrayList<PlacedWord>()
    for (word in words) {
        if (placed.size >= count) break
        val spots = ArrayList<Triple<Int, Int, Direction>>()
        for (dir in dirs) {
            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (fits(grid, word.norm, r, c, dir, size)) spots += Triple(r, c, dir)
                }
            }
        }
        if (spots.isEmpty()) continue
        val spot = spots[floor(rng() * spots.size).toInt()]
        val cells = cellsFor(word.norm, spot.first, spot.second, spot.third)
        for (i in cells.indices) grid[cells[i].r][cells[i].c] = word.norm[i]
        placed += PlacedWord(word.norm, word.display, cells, spot.third.name)
    }
    return grid to placed
}

private fun mixSeed(seed: Int, extra: Long): Int =
    ((seed.toLong() and 0xffffffffL) + extra).toInt()

fun generate(
    seed: Int,
    words: List<WordEntry>,
    size: Int,
    directions: DirectionMode,
    count: Int,
): Puzzle {
    val dirs = directionsFor(directions)
    val available = words.count { it.norm.length <= size }
    val target = count.coerceIn(0, available)
    var bestGrid = emptyGrid(size)
    var bestPlaced = emptyList<PlacedWord>()

    for (attempt in 0 until 40) {
        val rng = mulberry32(mixSeed(seed, attempt * 997L))
        val ordered = orderWords(words, size, rng)
        val (grid, placed) = greedy(ordered, size, dirs, target, rng)
        if (placed.size > bestPlaced.size) {
            bestGrid = grid
            bestPlaced = placed
        }
        if (placed.size >= target) break
    }

    val fillRng = mulberry32(seed xor 0x85ebca6b.toInt())
    val grid = List(size) { r ->
        List(size) { c ->
            bestGrid[r][c] ?: FILLER[floor(fillRng() * FILLER.length).toInt()]
        }
    }
    val placed = bestPlaced.sortedWith(compareBy(spanishOrder) { it.display })
    return Puzzle(grid, placed, size)
}
