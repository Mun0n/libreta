package dev.mun0n.libreta.state

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import dev.mun0n.libreta.data.themes
import dev.mun0n.libreta.game.Cell
import dev.mun0n.libreta.game.DirectionMode
import dev.mun0n.libreta.game.entries
import dev.mun0n.libreta.game.generate

class LibretaViewModel(application: Application) : AndroidViewModel(application) {
    private val store = Store(application)

    var screen by mutableStateOf(Screen.Home)
        private set
    var settings by mutableStateOf(Settings())
        private set
    var current by mutableStateOf<CurrentPuzzle?>(null)
        private set
    var completedCount by mutableStateOf(0)
        private set

    val resumeAvailable: Boolean
        get() = current?.let { !it.completed } == true

    init {
        val save = store.load()
        settings = save.settings
        current = save.current
        completedCount = save.completedCount
        screen = if (save.current?.completed == true) Screen.Puzzle else Screen.Home
    }

    fun startTheme(themeId: String) {
        val next = begin(themeId, settings, null) ?: return
        current = next
        screen = Screen.Puzzle
        persist()
    }

    fun another() {
        val puzzle = current ?: return
        val next = begin(puzzle.themeId, settings, puzzle.seed) ?: return
        current = next
        screen = Screen.Puzzle
        persist()
    }

    fun resume() {
        val puzzle = current ?: return
        if (puzzle.completed) return
        screen = Screen.Puzzle
    }

    fun goHome() {
        screen = Screen.Home
    }

    fun goSettings() {
        screen = Screen.Settings
    }

    fun back() {
        screen = Screen.Home
    }

    fun pickTheme() {
        current = null
        screen = Screen.Home
        persist()
    }

    fun mark(word: String, cells: List<Cell>, revealed: Boolean) {
        val puzzle = current ?: return
        if (puzzle.found.any { it.word == word }) return
        val found = puzzle.found + FoundWord(word, revealed, cells)
        val completed = found.size >= puzzle.total
        val justFinished = completed && !puzzle.counted
        if (justFinished) completedCount += 1
        current = puzzle.copy(
            found = found,
            completed = completed,
            counted = puzzle.counted || completed,
        )
        persist()
    }

    fun setSize(size: SizeMode) {
        settings = settings.copy(size = size)
        persist()
    }

    fun setDirections(directions: DirectionMode) {
        settings = settings.copy(directions = directions)
        persist()
    }

    fun setSound(sound: Boolean) {
        settings = settings.copy(sound = sound)
        persist()
    }

    private fun persist() {
        store.write(
            Save(
                version = 1,
                settings = settings,
                current = current,
                completedCount = completedCount,
            ),
        )
    }

    private fun begin(themeId: String, settings: Settings, previousSeed: Int?): CurrentPuzzle? {
        val theme = themes.find { it.id == themeId } ?: return null
        val spec = sizeSpecs.getValue(settings.size)
        val mixed = System.currentTimeMillis().toInt() xor ((previousSeed ?: 0) + 1)
        val seed = if (mixed == 0) 1 else mixed
        val puzzle = generate(
            seed = seed,
            words = entries(theme.words),
            size = spec.size,
            directions = settings.directions,
            count = spec.count,
        )
        if (puzzle.placed.isEmpty()) return null
        return CurrentPuzzle(
            seed = seed,
            themeId = themeId,
            size = spec.size,
            count = spec.count,
            directions = settings.directions,
            found = emptyList(),
            completed = false,
            counted = false,
            total = puzzle.placed.size,
        )
    }
}
