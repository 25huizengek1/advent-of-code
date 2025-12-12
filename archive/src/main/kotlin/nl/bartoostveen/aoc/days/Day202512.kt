package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.splitByWhitespace
import nl.bartoostveen.aoc.util.two

val day202512 = puzzle {
    val sections = raw.split("\n\n")
    val presents = sections
        .dropLast(1)
        .map { lines ->
            lines
                .lines()
                .drop(1)
                .sumOf {
                    it.count { char -> char == '#' }
                }
        }

    val regions = sections
        .last()
        .lines()
        .filter { it.isNotEmpty() }
        .map { region ->
            val (sizeText, presents) = region.split(": ")

            sizeText
                .split('x')
                .map(String::toInt).two to presents
                .splitByWhitespace()
                .map(String::toInt)
        }

    partOne = regions.count { (size, presentIndices) ->
        val area = size.x * size.y
        area >= presentIndices.foldIndexed(0L) { index, acc, amount -> acc + amount * presents[index] }
    }

    partTwo = "Tap to decorate the North Pole!"
}
