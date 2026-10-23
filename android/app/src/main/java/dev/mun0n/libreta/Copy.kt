package dev.mun0n.libreta

object Copy {
    const val appName = "Sopa de letras"
    const val howTo = "Pasa el dedo por las letras, como con el boli."
    const val chooseTheme = "Elige un tema"
    const val veryWell = "Muy bien"
    const val finished = "Sopa terminada"
    const val home = "Inicio"
    const val hint = "Pista"
    const val reveal = "Enséñame una"
    const val another = "Otra"
    const val anotherFull = "Otra sopa"
    const val pickTheme = "Elegir tema"
    const val settings = "Ajustes"
    const val back = "Volver"
    const val letterSize = "Tamaño de las letras"
    const val directions = "Direcciones"
    const val book = "Como en el libro"
    const val straight = "Solo rectas"
    const val bookHint = "Hacia todos los lados, también en diagonal"
    const val straightHint = "Solo de lado a lado y de arriba abajo"
    const val soundOn = "Sonido encendido"
    const val soundOff = "Sonido apagado"
    const val wordsLabel = "Palabras"

    fun continueWith(theme: String) = "Seguir con $theme"
    fun found(word: String) = "Has encontrado $word"
    fun progress(done: Int, total: Int) = "$done de $total"
    fun hintFor(word: String) = "Pista: $word"

    val sizes = mapOf(
        "xlarge" to "Letras muy grandes",
        "large" to "Letras grandes",
        "more" to "Más palabras",
    )
    val sizeHint = mapOf(
        "xlarge" to "Pocas letras, muy grandes",
        "large" to "El tamaño de siempre",
        "more" to "Más letras y más palabras",
    )

    fun doneCount(n: Int): String = when (n) {
        0 -> "Todavía no has terminado ninguna sopa."
        1 -> "Has terminado 1 sopa."
        else -> "Has terminado $n sopas."
    }
}
