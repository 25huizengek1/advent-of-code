package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.*

val day202509 = puzzle {
    val points = lines
        .map { it.split(',').map(String::toInt).two }
        .asSequence()

    val combinations = points
        .combinations(2)
        .map { (a, b) -> a rect b }
        .sortedByDescending { it.area }

    partOne = combinations.first().area

    val greenLines = (points + points.first()).windowed(size = 2, transform = List<Vec2i>::rect)
    partTwo = combinations.first { rect -> greenLines.none { rect intersects it } }.area
}
