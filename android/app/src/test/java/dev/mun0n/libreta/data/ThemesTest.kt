package dev.mun0n.libreta.data

import dev.mun0n.libreta.game.DirectionMode
import dev.mun0n.libreta.game.entries
import dev.mun0n.libreta.game.generate
import dev.mun0n.libreta.game.normalize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemesTest {
    @Test
    fun hasFourteenEverydayTopicsWithEnoughPlaceableWords() {
        assertEquals(themes.size, themes.map { it.id }.toSet().size)
        assertEquals(14, themes.size)
        val wordPattern = Regex("^[a-záéíóúüñ]+$", RegexOption.IGNORE_CASE)
        for (theme in themes) {
            assertTrue(theme.name.isNotEmpty())
            for (word in theme.words) assertTrue(word.matches(wordPattern))
            val list = entries(theme.words)
            val short = list.count { it.norm.length <= 8 }
            assertTrue("${theme.id} has ${list.size}", list.size >= 60)
            assertTrue("${theme.id} short $short", short >= 25)
            assertTrue(list.maxOf { it.norm.length } <= 12)
            assertTrue(list.all { it.norm == normalize(it.display) })
        }
    }

    @Test
    fun buildsAFullPuzzleForEveryThemeSizeAndDirection() {
        for (theme in themes) {
            val words = entries(theme.words)
            for (size in listOf(8, 10, 12)) {
                for (directions in listOf(DirectionMode.book, DirectionMode.straight)) {
                    for (seed in listOf(1, 7)) {
                        val puzzle = generate(seed, words, size, directions, size)
                        assertEquals("${theme.id} $size $directions $seed", size, puzzle.placed.size)
                        assertEquals(size, puzzle.grid.size)
                    }
                }
            }
        }
    }
}
