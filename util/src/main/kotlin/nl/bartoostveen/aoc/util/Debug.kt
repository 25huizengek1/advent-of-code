package nl.bartoostveen.aoc.util

import kotlin.time.measureTimedValue

fun <T> Result<T>.printException() = also { it.exceptionOrNull()?.printStackTrace() }

inline fun <T> printMicros(
    name: String? = null,
    crossinline block: () -> T
) = measureTimedValue(block)
    .also { println("${name?.plus(" ") ?: ""}took ${it.duration.inWholeMicroseconds}μs") }
    .value