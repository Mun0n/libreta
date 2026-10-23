package dev.mun0n.libreta.game

import java.text.Normalizer
import java.util.Locale

private val spanish = Locale.forLanguageTag("es")
private val marks = Regex("\\p{M}")
private val notLetter = Regex("[^A-ZÑ]")

/** Uppercase Spanish letters for the grid. Accents drop; Ñ stays. */
fun normalize(input: String): String {
    val upper = input.uppercase(spanish).replace("Ñ", "\u0000")
    val stripped = Normalizer.normalize(upper, Normalizer.Form.NFD).replace(marks, "")
    return stripped.replace("\u0000", "Ñ").replace(notLetter, "")
}

fun displayWord(raw: String): String = raw.uppercase(spanish)
