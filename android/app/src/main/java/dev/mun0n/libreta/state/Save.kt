package dev.mun0n.libreta.state

import dev.mun0n.libreta.game.Cell
import dev.mun0n.libreta.game.DirectionMode
import kotlinx.serialization.Serializable

@Serializable
enum class SizeMode { xlarge, large, more }

data class SizeSpec(val size: Int, val count: Int)

val sizeSpecs: Map<SizeMode, SizeSpec> = mapOf(
    SizeMode.xlarge to SizeSpec(8, 8),
    SizeMode.large to SizeSpec(10, 10),
    SizeMode.more to SizeSpec(12, 12),
)

@Serializable
data class FoundWord(
    val word: String,
    val revealed: Boolean,
    val cells: List<Cell>,
)

@Serializable
data class CurrentPuzzle(
    val seed: Int,
    val themeId: String,
    val size: Int,
    val count: Int,
    val directions: DirectionMode,
    val found: List<FoundWord>,
    val completed: Boolean,
    val counted: Boolean,
    val total: Int,
)

@Serializable
data class Settings(
    val size: SizeMode = SizeMode.large,
    val directions: DirectionMode = DirectionMode.book,
    val sound: Boolean = false,
)

@Serializable
data class Save(
    val version: Int = 1,
    val settings: Settings = Settings(),
    val current: CurrentPuzzle? = null,
    val completedCount: Int = 0,
)

enum class Screen { Home, Puzzle, Settings }
