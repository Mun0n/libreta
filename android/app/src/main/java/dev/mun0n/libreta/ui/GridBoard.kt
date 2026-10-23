package dev.mun0n.libreta.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import dev.mun0n.libreta.game.Cell
import dev.mun0n.libreta.game.DirectionMode
import dev.mun0n.libreta.game.directionsFor
import dev.mun0n.libreta.game.lineBetween
import dev.mun0n.libreta.game.snapDrag
import kotlin.math.hypot

enum class CommitResult { Found, Already, Miss }

data class DrawnStroke(
    val word: String,
    val cells: List<Cell>,
    val color: Color,
    val pencil: Boolean,
)

@Composable
fun GridBoard(
    letters: List<List<Char>>,
    strokes: List<DrawnStroke>,
    hint: Cell?,
    mode: DirectionMode,
    onCommit: (List<Cell>) -> CommitResult,
    modifier: Modifier = Modifier,
) {
    val n = letters.size
    val commit by rememberUpdatedState(onCommit)
    val allowed = remember(mode) { directionsFor(mode) }
    var anchor by remember { mutableStateOf<Cell?>(null) }
    var preview by remember { mutableStateOf<List<Cell>?>(null) }
    var ghost by remember { mutableStateOf<List<Cell>?>(null) }
    var ghostToken by remember { mutableIntStateOf(0) }
    val ghostAlpha = remember { Animatable(0f) }
    LaunchedEffect(ghostToken) {
        if (ghost == null) return@LaunchedEffect
        ghostAlpha.snapTo(0.55f)
        ghostAlpha.animateTo(0f, tween(420))
        ghost = null
    }

    Box(modifier.clearAndSetSemantics { }) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Paper)
            val cell = size.minDimension / n
            fun center(c: Cell) = Offset((c.c + 0.5f) * cell, (c.r + 0.5f) * cell)
            fun line(cells: List<Cell>, color: Color, width: Float, alpha: Float = 1f) {
                if (cells.size < 2) return
                drawLine(
                    color = color.copy(alpha = alpha),
                    start = center(cells.first()),
                    end = center(cells.last()),
                    strokeWidth = cell * width,
                    cap = StrokeCap.Round,
                    blendMode = BlendMode.Multiply,
                )
            }
            for (stroke in strokes) line(stroke.cells, stroke.color, if (stroke.pencil) 0.34f else 0.72f)
            preview?.let { line(it, MarkerYellow, 0.72f, 0.55f) }
            ghost?.let { line(it, MarkerYellow, 0.72f, ghostAlpha.value) }
            fun ring(cellAt: Cell, radius: Float) {
                drawCircle(
                    color = Ink,
                    radius = cell * radius,
                    center = center(cellAt),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = cell * 0.07f),
                    blendMode = BlendMode.Multiply,
                )
            }
            anchor?.let { ring(it, 0.4f) }
            hint?.let { ring(it, 0.42f) }
        }
        Letters(letters, Modifier.fillMaxSize())
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(n, mode) {
                    val slop = viewConfiguration.touchSlop * 1.5f
                    val side = size.width.toFloat()
                    fun cellAt(offset: Offset): Cell {
                        val x = offset.x.coerceIn(0f, side - 0.01f)
                        val y = offset.y.coerceIn(0f, side - 0.01f)
                        return Cell(
                            r = ((y / side) * n).toInt().coerceIn(0, n - 1),
                            c = ((x / side) * n).toInt().coerceIn(0, n - 1),
                        )
                    }
                    fun finish(cells: List<Cell>) {
                        if (cells.size < 2) return
                        if (commit(cells) == CommitResult.Miss) {
                            ghost = cells
                            ghostToken += 1
                        }
                    }
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val start = cellAt(down.position)
                        val origin = down.position
                        var dragging = false
                        var line = listOf(start)
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                if (dragging) line = snapDrag(start, cellAt(change.position), n, allowed)
                                break
                            }
                            val moved = hypot(
                                (change.position.x - origin.x).toDouble(),
                                (change.position.y - origin.y).toDouble(),
                            )
                            if (!dragging && moved > slop) {
                                dragging = true
                                anchor = null
                            }
                            if (dragging) {
                                line = snapDrag(start, cellAt(change.position), n, allowed)
                                preview = line
                            }
                            if (change.positionChange() != Offset.Zero) change.consume()
                        }
                        preview = null
                        if (dragging) {
                            finish(line)
                        } else {
                            val held = anchor
                            when {
                                held == null -> anchor = start
                                held == start -> anchor = null
                                else -> {
                                    val tapped = lineBetween(held, start, allowed)
                                    if (tapped == null) {
                                        anchor = start
                                    } else {
                                        anchor = null
                                        finish(tapped)
                                    }
                                }
                            }
                        }
                    }
                },
        )
    }
}

@Composable
private fun Letters(letters: List<List<Char>>, modifier: Modifier) {
    val n = letters.size.coerceAtLeast(1)
    BoxWithConstraints(modifier) {
        val fontSize = with(LocalDensity.current) { (maxWidth.toPx() / n * 0.52f).toSp() }
        Column(Modifier.fillMaxSize()) {
            for (row in letters) {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    for (letter in row) {
                        Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                            Text(
                                text = letter.toString(),
                                color = Ink,
                                fontFamily = Atkinson,
                                fontSize = fontSize,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}
