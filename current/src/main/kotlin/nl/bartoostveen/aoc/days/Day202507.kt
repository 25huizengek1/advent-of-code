package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.Direction
import nl.bartoostveen.aoc.util.Vec2i
import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.toGrid

val day202507 = puzzle {
    val diagram = lines.toGrid()
    val start = diagram.points.first { diagram[it] == 'S' }
    val seen = mutableSetOf<Vec2i>()
    var splits = 0

    fun solve(head: Vec2i) {
        if (head in seen || head !in diagram) return
        seen += head

        val down = head + Direction.DOWN.vec
        if (down !in diagram) return
        if (diagram[down] == '.') {
            return solve(down)
        } else {
            // otherwise, we assume it to be a splitter
            val left = down + Direction.LEFT.vec
            val right = down + Direction.RIGHT.vec
            splits++
            solve(left)
            solve(right)
        }
    }

    solve(start)
    partOne = splits
}
