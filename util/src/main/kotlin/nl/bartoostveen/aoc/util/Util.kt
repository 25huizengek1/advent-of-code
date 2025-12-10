package nl.bartoostveen.aoc.util

import java.util.Collections
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

fun List<Float>.product() = fold(1f) { acc, num -> acc * num }
fun List<Double>.product() = fold(1.0) { acc, num -> acc * num }
fun List<Int>.product() = fold(1) { acc, num -> acc * num }
fun List<Long>.product() = fold(1L) { acc, num -> acc * num }

fun <A, B> Pair<A, B>.swap() = second to first

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

// Thanks for the very efficient permutations impl (github:770grappenmaker)
fun <T> Iterable<T>.permutations() = toList().permutations()
fun <T> Iterable<T>.permutations(r: Int) = toList().permutations(r)

// Inspired by:
// https://docs.python.org/3/library/itertools.html#itertools.permutations
fun <T> List<T>.permutations(r: Int = size): Sequence<List<T>> = sequence {
    if (r > size || isEmpty()) return@sequence

    val ind = indices.toMutableList()
    val cyc = (size downTo size - r).toMutableList()
    yield(take(r))

    while (true) {
        for (i in r - 1 downTo 0) {
            if (--cyc[i] == 0) {
                ind.add(ind.removeAt(i))
                cyc[i] = size - i
            } else {
                Collections.swap(ind, i, size - cyc[i])
                yield(slice(ind.take(r)))
                break
            }

            if (i == 0) return@sequence
        }
    }
}

fun <T> Iterable<T>.combinations() = toList().combinations()
fun <T> Iterable<T>.combinations(r: Int) = toList().combinations(r)
fun <T> List<T>.combinations(r: Int = size) =
    indices
        .permutations()
        .take(r)
        .filter { it.sorted() == it }.map { p -> p.map { this[it] } }

fun <T> Iterable<T>.permutations2(): Sequence<Pair<T, T>> = sequence {
    forEachIndexed { idx, i ->
        forEachIndexed { jdx, j ->
            if (idx != jdx) yield(i to j)
        }
    }
}

data class TupleSet<T>(
    val first: T,
    val second: T
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TupleSet<*>) return false

        if (first == other.first && second == other.second) return true
        if (first == other.second && second == other.first) return true

        return false
    }

    override fun hashCode(): Int {
        val ah = first?.hashCode() ?: 0
        val bh = second?.hashCode() ?: 0
        return ah xor bh
    }
}

fun <T> Pair<T, T>.unordered() = TupleSet(first, second)
fun <T> TupleSet<T>.ordered() = first to second

infix fun <T> T.with(other: T) = TupleSet(this, other)
