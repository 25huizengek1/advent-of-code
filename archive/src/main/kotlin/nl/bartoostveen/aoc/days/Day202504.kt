package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.toGrid

val day202504 = puzzle {
    val grid = lines.toGrid { it == '@' }.toMutableGrid()
    fun eval(remove: Boolean) = grid.points.count { point ->
        (grid[point] && grid.adjacent(point).count { it } < 4).also { if (it && remove) grid[point] = false }
    }

    partOne = eval(false)
    partTwo = generateSequence { eval(true).takeIf { it != 0 } }.sum()
}
