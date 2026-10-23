package dev.mun0n.libreta.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateTest {
    @Test
    fun mulberry32MatchesTheWebGenerator() {
        val rng = mulberry32(42)
        assertEquals(0.601103751920, rng(), 1e-11)
        assertEquals(0.448290558998, rng(), 1e-11)
        assertEquals(0.852465793490, rng(), 1e-11)
    }

    @Test
    fun dropsAccentsAndKeepsNye() {
        assertEquals("CANCION", normalize("canción"))
        assertEquals("NIÑO", normalize("niño"))
        assertEquals("ÑANDU", normalize("ñandú"))
        assertEquals("PINGUINO", normalize("pingüino"))
        assertEquals("CAÑON", normalize("cañón"))
    }

    @Test
    fun rejectsACrossingWhenTheLetterDiffers() {
        val grid = Array(4) { arrayOfNulls<Char>(4) }
        grid[0][0] = 'C'
        grid[0][1] = 'A'
        grid[0][2] = 'S'
        grid[0][3] = 'A'
        val east = bookDirections.first { it.name == DirName.E }
        val south = bookDirections.first { it.name == DirName.S }
        assertTrue(fits(grid, "CASA", 0, 0, east, 4))
        assertFalse(fits(grid, "COLA", 0, 0, east, 4))
        assertTrue(fits(grid, "CAMA", 0, 0, south, 4))
    }

    @Test
    fun isDeterministicForTheSameSeed() {
        val words = bank()
        val first = generate(42, words, 10, DirectionMode.book, 8)
        val second = generate(42, words, 10, DirectionMode.book, 8)
        assertEquals(first, second)
    }

    @Test
    fun placesEveryChosenWordIncludingNye() {
        val puzzle = generate(11, bank(), 10, DirectionMode.book, 8)
        assertEquals(8, puzzle.placed.size)
        assertTrue(puzzle.grid.any { row -> row.any { it == 'Ñ' } })
        for (word in puzzle.placed) {
            val letters = word.cells.joinToString("") { puzzle.grid[it.r][it.c].toString() }
            assertEquals(word.word, letters)
        }
    }

    @Test
    fun discardsWordsLongerThanTheGrid() {
        val words = entries(listOf("sol", "mar", "pan", "luz", "supercalifragilistico"))
        val puzzle = generate(2, words, 8, DirectionMode.book, 4)
        assertFalse(puzzle.placed.any { it.word == "SUPERCALIFRAGILISTICO" })
        assertEquals(4, puzzle.placed.size)
    }

    @Test
    fun fillsEmptyCellsWithSpanishLetters() {
        val puzzle = generate(5, bank(), 8, DirectionMode.straight, 6)
        val filler = FILLER.toSet()
        val used = puzzle.placed.flatMap { word -> word.cells.map { "${it.r},${it.c}" } }.toSet()
        for (r in 0 until puzzle.size) {
            for (c in 0 until puzzle.size) {
                val letter = puzzle.grid[r][c]
                assertTrue(letter.toString().matches(Regex("^[A-ZÑ]$")))
                if ("$r,$c" !in used) {
                    assertTrue(letter in filler)
                    assertTrue(letter != 'K' && letter != 'W')
                }
            }
        }
    }

    @Test
    fun keepsStraightModeOnRowsAndColumns() {
        val puzzle = generate(9, bank(), 8, DirectionMode.straight, 6)
        for (word in puzzle.placed) {
            val dr = sign(word.cells[1].r - word.cells[0].r)
            val dc = sign(word.cells[1].c - word.cells[0].c)
            assertTrue(dr == 0 || dc == 0)
            assertTrue(dr != 0 || dc != 0)
        }
    }

    private fun bank() = entries(listOf("niño", "ñandú", "uña", "mañana", "pañuelo", "araña", "canción", "pingüino"))

    private fun sign(n: Int) = when {
        n > 0 -> 1
        n < 0 -> -1
        else -> 0
    }
}
