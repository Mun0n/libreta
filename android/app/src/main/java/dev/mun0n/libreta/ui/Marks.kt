package dev.mun0n.libreta.ui

import androidx.compose.ui.graphics.Color
import dev.mun0n.libreta.state.FoundWord

private data class Marker(val wash: Color, val strike: Color)

private val markers = listOf(
    Marker(Color(0xFFFFDF4A), Color(0xFF8D6A08)),
    Marker(Color(0xFFFF9EC8), Color(0xFFA33D68)),
    Marker(Color(0xFF9FE07A), Color(0xFF2F7A32)),
    Marker(Color(0xFF8ECDF5), Color(0xFF1F6F9E)),
    Marker(Color(0xFFFFB15E), Color(0xFFB35E12)),
    Marker(Color(0xFFD2B0FF), Color(0xFF6D4C96)),
)

private val pencilWash = Color(0xFFE4DDD0)
private val pencilInk = Color(0xFF5C564C)

data class Mark(
    val wash: Color,
    val strike: Color,
    val stroke: Color,
    val pencil: Boolean,
)

fun markFor(found: List<FoundWord>, word: String): Mark? {
    val item = found.find { it.word == word } ?: return null
    if (item.revealed) return Mark(pencilWash, pencilInk, pencilInk, pencil = true)
    val index = found.filter { !it.revealed }.indexOfFirst { it.word == word }
    val marker = markers[(index + markers.size) % markers.size]
    return Mark(marker.wash, marker.strike, marker.wash, pencil = false)
}
