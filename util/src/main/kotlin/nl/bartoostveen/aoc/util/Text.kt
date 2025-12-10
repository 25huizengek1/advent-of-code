package nl.bartoostveen.aoc.util

fun String.splitAtIndex(index: Int) = require(index in 0..length).let {
    take(index) to substring(index + 1)
}

fun String.splitByWhitespace() = split("\\s+".toRegex())

fun String.startsWithFromIndex(i: Int, other: String): Boolean {
    if (length < other.length + i) return false

    for (j in 0..<other.length) {
        if (this[i + j] != other[j]) return false
    }

    return true
}