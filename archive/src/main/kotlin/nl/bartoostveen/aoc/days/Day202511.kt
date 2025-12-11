package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.splitAtIndex
import nl.bartoostveen.aoc.util.splitByWhitespace

val day202511 = puzzle {
    val devices = lines.associate { line ->
        val (name, connections) = line.splitByWhitespace().splitAtIndex(1)
        name.first().dropLast(1) to connections
    }.filter { it.value.isNotEmpty() }

    val seen = hashMapOf<Pair<String, Set<String>>, Long>()

    fun paths(
        start: String = "you",
        goal: String = "out",
        cur: Set<String> = setOf(),
        contains: Set<String> = setOf()
    ): Long = if (goal == start) {
        if (cur == contains) 1L else 0L
    } else seen.getOrPut(start to cur) {
        devices[start]!!.sumOf {
            paths(
                start = it,
                goal = goal,
                cur = (cur + it) intersect contains,
                contains = contains
            )
        }
    }

    partOne = paths()
    seen.clear()
    partTwo = paths(start = "svr", contains = setOf("dac", "fft"))
}
