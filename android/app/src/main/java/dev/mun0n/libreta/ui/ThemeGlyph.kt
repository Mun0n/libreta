package dev.mun0n.libreta.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser

private data class Dot(val x: Float, val y: Float, val r: Float)

private data class Glyph(val strokes: List<String>, val dots: List<Dot> = emptyList())

private val book = Glyph(
    strokes = listOf(
        "M12 20c8 4 12 4 20 0v26c-8 4-12 4-20 0z",
        "M32 20c8 4 12 4 20 0v26c-8 4-12 4-20 0z",
        "M32 20v26",
        "M44 42 54 28",
    ),
)

private val glyphs = mapOf(
    "animales" to Glyph(
        strokes = listOf(
            "M14 38c6-1 12-8 18-8 5 0 8 2 11 5-5 1-7 5-5 9 5 1 9 5 8 9H20c-7 0-12-5-6-15z",
            "M43 34c5-7 12-8 16-5",
            "M22 40c2 3 5 3 7 0",
        ),
        dots = listOf(Dot(47f, 33f, 1.1f)),
    ),
    "comida" to Glyph(
        strokes = listOf(
            "M32 16c2-6 8-8 11-6-3 5-7 7-9 7",
            "M32 18c-12 2-18 12-18 22 0 8 7 14 18 14s18-6 18-14c0-10-6-20-18-22z",
            "M32 18c2 8 2 16 0 24",
        ),
    ),
    "cocina" to Glyph(
        strokes = listOf(
            "M18 30h28v4c0 10-6 16-14 16s-14-6-14-16z",
            "M14 30h36",
            "M24 30c1-8 15-8 16 0",
            "M16 36H10M48 36h6",
        ),
    ),
    "casa" to Glyph(
        strokes = listOf(
            "M10 30 32 12l22 18",
            "M16 28v24h32V28",
            "M28 52V38h8v14",
        ),
    ),
    "familia" to Glyph(
        strokes = listOf(
            "M14 14h36a2 2 0 0 1 2 2v28a2 2 0 0 1-2 2H14a2 2 0 0 1-2-2V16a2 2 0 0 1 2-2z",
            "M21 28a5 5 0 1 0 10 0a5 5 0 1 0-10 0",
            "M36 29a4 4 0 1 0 8 0a4 4 0 1 0-8 0",
            "M16 44c2-6 6-8 10-8 3 2 6 4 8 4 4-4 8-6 14-4",
        ),
    ),
    "ropa" to Glyph(
        strokes = listOf("M24 14 32 22 40 14l10 8-6 6v22H20V28l-6-6z"),
    ),
    "cuerpo" to Glyph(
        strokes = listOf(
            "M8 32c8-12 40-12 48 0-8 12-40 12-48 0z",
            "M25 32a7 7 0 1 0 14 0a7 7 0 1 0-14 0",
        ),
        dots = listOf(Dot(32f, 32f, 2.2f)),
    ),
    "jardin" to Glyph(
        strokes = listOf(
            "M18 50c14-2 18-16 18-34",
            "M36 28c10-1 14 8 8 14-8 1-12-6-8-14z",
            "M34 38c-12 2-14 12-6 15 8-1 10-8 6-15z",
            "M36 20c6-8 14-6 14 0-6 2-12 0-14 0z",
        ),
    ),
    "ciudad" to Glyph(
        strokes = listOf(
            "M10 52V30h12v22M22 52V16h16v36M38 52V32h14v20",
            "M26 24h3M32 24h3M26 32h3M32 32h3M14 38h4M42 40h4",
        ),
    ),
    "campo" to Glyph(
        strokes = listOf(
            "M32 54V16",
            "M32 20c-7-3-9-10-5-12 4 3 6 8 5 12zM32 20c7-3 9-10 5-12-4 3-6 8-5 12z",
            "M32 30c-7-3-9-10-5-12 4 3 6 8 5 12zM32 30c7-3 9-10 5-12-4 3-6 8-5 12z",
            "M32 40c-6-2-8-8-4-10 3 3 5 7 4 10zM32 40c6-2 8-8 4-10-3 3-5 7-4 10z",
        ),
    ),
    "tiempo" to Glyph(
        strokes = listOf(
            "M20 40h28a9 9 0 0 0 1-18 12 12 0 0 0-23-3 8 8 0 0 0-6 21z",
            "M24 48v6M32 50v6M40 48v6",
        ),
    ),
    "mar" to Glyph(
        strokes = listOf(
            "M8 26c6 6 10 6 16 0s10-6 16 0 10 6 16 0",
            "M8 40c6 6 10 6 16 0s10-6 16 0 10 6 16 0",
        ),
    ),
    "oficios" to Glyph(
        strokes = listOf(
            "M24 14h16l4 12H20z",
            "M18 26h28l-2 26H20z",
            "M28 14c0-4 8-4 8 0",
            "M32 32v12",
        ),
    ),
    "escuela" to book,
)

@Composable
fun ThemeGlyph(id: String, modifier: Modifier = Modifier) {
    val glyph = glyphs[id] ?: book
    val strokes = remember(id) { glyph.strokes.map { PathParser().parsePathString(it).toPath() } }
    Canvas(modifier) {
        val scale = size.minDimension / 64f
        val stroke = Stroke(width = 2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        withTransform({ scale(scale, scale, pivot = Offset.Zero) }) {
            for (path in strokes) drawPath(path, color = Ink, style = stroke)
            for (dot in glyph.dots) drawCircle(Ink, radius = dot.r, center = Offset(dot.x, dot.y))
        }
    }
}
