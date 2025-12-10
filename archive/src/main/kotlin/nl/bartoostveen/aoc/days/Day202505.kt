package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.simplify

val day202505 = puzzle {
    val (rangesText, ids) = raw.trim().split("\n\n")
    val ranges = rangesText
        .lines()
        .map { range ->
            val (a, b) = range.split('-').map { it.toLong() }
            a..b
        }
        .sortedBy { it.first }
        .simplify()

    partOne = ids.lines().map { it.toLong() }.count { id -> ranges.any { id in it } }
    partTwo = ranges.sumOf { it.last - it.first + 1 }
}
