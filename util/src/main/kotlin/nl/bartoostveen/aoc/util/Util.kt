package nl.bartoostveen.aoc.util

import kotlin.time.measureTimedValue

fun String.splitAtIndex(index: Int) = require(index in 0..length).let {
    take(index) to substring(index + 1)
}

fun <T> Result<T>.printException() = also { it.exceptionOrNull()?.printStackTrace() }

fun String.splitByWhitespace() = split("\\s+")

fun String.startsWithFromIndex(i: Int, other: String): Boolean {
    if (length < other.length + i) return false

    for (j in 0..<other.length) {
        if (this[i + j] != other[j]) return false
    }

    return true
}

inline fun <T> printMicros(
    name: String? = null,
    crossinline block: () -> T
) = measureTimedValue(block)
    .also { println("${name?.plus(" ") ?: ""}took ${it.duration.inWholeMicroseconds}μs") }
    .value