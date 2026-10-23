package dev.mun0n.libreta.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SelectTest {
    private val grid = listOf(
        listOf('G', 'A', 'T', 'O', 'S'),
        listOf('R', 'M', 'A', 'R', 'O'),
        listOf('L', 'U', 'Z', 'N', 'L'),
        listOf('A', 'R', 'O', 'M', 'A'),
        listOf('P', 'A', 'N', 'E', 'S'),
    )

    private val gato = PlacedWord(
        word = "GATO",
        display = "GATO",
        dir = DirName.E,
        cells = listOf(Cell(0, 0), Cell(0, 1), Cell(0, 2), Cell(0, 3)),
    )

    @Test
    fun drawsAStraightOrExactDiagonalLine() {
        assertEquals(4, lineBetween(Cell(0, 0), Cell(0, 3), bookDirections)!!.size)
        assertEquals(Cell(3, 3), lineBetween(Cell(0, 0), Cell(3, 3), bookDirections)!!.last())
        assertNull(lineBetween(Cell(0, 0), Cell(1, 2), bookDirections))
    }

    @Test
    fun refusesDiagonalsWhenOnlyStraightDirectionsAreAllowed() {
        assertNull(lineBetween(Cell(0, 0), Cell(3, 3), straightDirections))
        assertEquals(5, lineBetween(Cell(2, 0), Cell(2, 4), straightDirections)!!.size)
    }

    @Test
    fun locksAMostlyHorizontalDragToTheRow() {
        val cells = snapDrag(Cell(0, 0), Cell(2, 4), 8, bookDirections)
        assertEquals(Cell(0, 0), cells.first())
        assertEquals(Cell(0, 4), cells.last())
        assertTrueRows(cells)
    }

    @Test
    fun locksAnEvenDiagonalToTheDiagonal() {
        val cells = snapDrag(Cell(0, 0), Cell(3, 3), 8, bookDirections)
        assertEquals(
            listOf(Cell(0, 0), Cell(1, 1), Cell(2, 2), Cell(3, 3)),
            cells,
        )
    }

    @Test
    fun staysInsideTheGrid() {
        val cells = snapDrag(Cell(6, 6), Cell(0, 6), 8, bookDirections)
        assertTrue(cells.last().r >= 0)
        assertEquals(Cell(6, 6), cells.first())
        assertTrue(cells.all { it.c == 6 })
    }

    @Test
    fun matchesTheWholeWordForwardsOrBackwards() {
        assertEquals("GATO", findHit(grid, gato.cells, listOf(gato))?.word)
        assertEquals("GATO", findHit(grid, gato.cells.reversed(), listOf(gato))?.word)
        assertNull(findHit(grid, gato.cells.take(3), listOf(gato)))
    }

    private fun assertTrueRows(cells: List<Cell>) {
        assertTrue(cells.all { it.r == 0 })
    }

    private fun assertTrue(value: Boolean) = org.junit.Assert.assertTrue(value)
}
