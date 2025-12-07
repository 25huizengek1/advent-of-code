package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.Direction
import nl.bartoostveen.aoc.util.Vec2i
import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.toGrid

val day202507 = puzzle {
    val diagram = lines.toGrid()
    val start = diagram.points.first { diagram[it] == 'S' }
    val seen = hashMapOf<Vec2i, Long>()
    var splits = 0

    fun solve(head: Vec2i): Long = seen.getOrPut(head) {
        val down = head + Direction.DOWN

        if (down !in diagram) return@getOrPut 1L
        if (diagram[down] == '.') return@getOrPut solve(down)

        splits++
        solve(down + Direction.LEFT) + solve(down + Direction.RIGHT)
    }

    partTwo = solve(start)
    partOne = splits
}
