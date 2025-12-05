package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle
import kotlin.math.max

val day202505 = puzzle {
    val (rangesText, ids) = raw.trim().split("\n\n")
    val ranges = rangesText
        .lines()
        .map { range ->
            val (a, b) = range.split('-').map { it.toLong() }
            a..b
        }
        .sortedBy { it.first }

    val simplifiedRanges = mutableListOf<LongRange>()
    ranges.forEach { range ->
        val prev = simplifiedRanges.lastOrNull()
        if (prev == null) {
            simplifiedRanges.add(range)
            return@forEach
        }

        when {
            prev.last < range.first -> simplifiedRanges.add(range)
            else -> simplifiedRanges[simplifiedRanges.size - 1] =
                prev.first..(max(prev.last, range.last))
        }
    }

    partOne = ids.lines().count { id -> simplifiedRanges.any { id.toLong() in it } }
    partTwo = simplifiedRanges.sumOf { it.last - it.first + 1 }
}
