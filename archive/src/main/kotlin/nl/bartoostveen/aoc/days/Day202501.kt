package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle
import kotlin.math.abs

val day202501 = puzzle {
    var turns = 0
    var passes = 0
    var dial = 50

    for (line in lines) {
        val op = line.take(1)
        val amount = line.drop(1).toIntOrNull() ?: 0
        val turn = amount * if (op == "L") -1 else 1

        val new = (dial + turn).mod(100)
        if (new == 0) turns++
        if ((dial + turn) <= 0 && dial != 0) passes++

        passes += abs(dial + turn) / 100
        dial = new
    }

    partOne = turns
    partTwo = passes
}
