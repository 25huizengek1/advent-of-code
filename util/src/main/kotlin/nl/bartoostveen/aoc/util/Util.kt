package nl.bartoostveen.aoc.util

import kotlin.time.measureTimedValue

fun String.splitAtIndex(index: Int) = require(index in 0..length).let {
    take(index) to substring(index + 1)
}

fun <T> Result<T>.printException() = also { it.exceptionOrNull()?.printStackTrace() }

fun String.splitByWhitespace() = split("\\s+".toRegex())

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

fun List<Long>.product() = fold(1L) { acc, num -> acc * num }

fun <T> Sequence<T>.split(predicate: (T) -> Boolean): Sequence<List<T>> = sequence {
    var acc = mutableListOf<T>()

    forEach { p ->
        if (predicate(p)) {
            if (acc.isNotEmpty()) yield(acc)
            acc = mutableListOf()
        } else acc += p
    }

    if (acc.isNotEmpty()) yield(acc)
}
