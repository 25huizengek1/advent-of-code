package nl.bartoostveen.aoc.util

fun <T> List<T>.splitAtIndex(
    index: Int,
    dismissAtSplit: Boolean = false
) = require(index in 0..size).let {
    take(index) to drop(index + if (dismissAtSplit) 1 else 0)
}

fun String.splitAtIndex(index: Int): Pair<String, String> {
    val (fst, snd) = toList().splitAtIndex(index, true)
    return fst.toCharArray().concatToString() to snd.toCharArray().concatToString()
}

fun String.splitByWhitespace() = split("\\s+".toRegex())

fun String.startsWithFromIndex(i: Int, other: String): Boolean {
    if (length < other.length + i) return false

    for (j in 0..<other.length) {
        if (this[i + j] != other[j]) return false
    }

    return true
}