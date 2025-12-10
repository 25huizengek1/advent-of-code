package nl.bartoostveen.aoc.util

import kotlin.math.max

private fun <T : Comparable<T>> unsafeMax(
    first: T,
    second: T
) = when {
    first < second -> second
    else -> first
}

private fun <T, P : Comparable<P>> List<T>.simplify(
    first: (T) -> P,
    last: (T) -> P,
    rangeOf: (P, P) -> T,
    max: (P, P) -> P = ::unsafeMax
): List<T> where T : ClosedRange<P>, T : OpenEndRange<P> {
    val simplifiedRanges = mutableListOf<T>()
    forEach { range ->
        val prev = simplifiedRanges.lastOrNull()
        if (prev == null) {
            simplifiedRanges.add(range)
            return@forEach
        }

        when {
            last(prev) < first(range) -> simplifiedRanges.add(range)
            else -> simplifiedRanges[simplifiedRanges.size - 1] =
                rangeOf(first(prev), max(last(prev), last(range)))
        }
    }
    return simplifiedRanges
}

@JvmName("simplifyIntRanges")
fun List<IntRange>.simplify() = simplify(
    first = IntRange::first,
    last = IntRange::last,
    rangeOf = Int::rangeTo,
    max = ::max
)

@JvmName("simplifyLongRanges")
fun List<LongRange>.simplify() = simplify(
    first = LongRange::first,
    last = LongRange::last,
    rangeOf = Long::rangeTo,
    max = ::max
)
