package dev.mun0n.libreta.state

import android.content.Context
import dev.mun0n.libreta.data.themes
import dev.mun0n.libreta.game.Cell
import dev.mun0n.libreta.game.DirectionMode
import kotlinx.serialization.json.Json

private const val PREFS = "libreta"
private const val KEY = "libreta.v1"

private val json = Json { ignoreUnknownKeys = true }

class Store(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): Save {
        val raw = prefs.getString(KEY, null) ?: return Save()
        return try {
            sanitize(json.decodeFromString<Save>(raw))
        } catch (_: Exception) {
            Save()
        }
    }

    fun write(save: Save) {
        prefs.edit().putString(KEY, json.encodeToString(Save.serializer(), save)).apply()
    }
}

private fun sanitize(save: Save): Save {
    if (save.version != 1) return Save()
    return Save(
        version = 1,
        settings = Settings(
            size = save.settings.size,
            directions = save.settings.directions,
            sound = save.settings.sound,
        ),
        current = sanitizeCurrent(save.current),
        completedCount = save.completedCount.coerceAtLeast(0),
    )
}

private fun sanitizeCurrent(current: CurrentPuzzle?): CurrentPuzzle? {
    current ?: return null
    if (current.size != 8 && current.size != 10 && current.size != 12) return null
    if (themes.none { it.id == current.themeId }) return null
    if (current.total < 1) return null
    val found = current.found.mapNotNull { item ->
        if (item.word.isEmpty()) return@mapNotNull null
        val cells = item.cells.filter { cellIn(it, current.size) }
        if (cells.size < 2) null else item.copy(cells = cells)
    }
    val completed = found.size >= current.total || current.completed
    val count = if (current.count == 8 || current.count == 10 || current.count == 12) current.count else current.size
    return current.copy(
        count = count,
        directions = if (current.directions == DirectionMode.straight) DirectionMode.straight else DirectionMode.book,
        found = found,
        completed = completed,
        counted = current.counted || completed,
    )
}

private fun cellIn(cell: Cell, size: Int): Boolean =
    cell.r in 0 until size && cell.c in 0 until size
