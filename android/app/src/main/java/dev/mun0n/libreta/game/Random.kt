package dev.mun0n.libreta.game

/** Same mulberry32 the browser build uses, bit for bit, so a seed rebuilds the same sheet. */
fun mulberry32(seed: Int): () -> Double {
    var state = seed
    return {
        state += 0x6d2b79f5
        var t = imul(state xor (state ushr 15), 1 or state)
        t = (t + imul(t xor (t ushr 7), 61 or t)) xor t
        ((t xor (t ushr 14)).toLong() and 0xffffffffL).toDouble() / 4294967296.0
    }
}

private fun imul(a: Int, b: Int): Int = (a.toLong() * b.toLong()).toInt()

fun <T> shuffle(list: List<T>, rng: () -> Double): List<T> {
    val arr = list.toMutableList()
    for (i in arr.lastIndex downTo 1) {
        val j = (rng() * (i + 1)).toInt()
        val tmp = arr[i]
        arr[i] = arr[j]
        arr[j] = tmp
    }
    return arr
}
