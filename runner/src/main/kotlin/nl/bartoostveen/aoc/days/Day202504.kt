package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.runner.puzzle
import nl.bartoostveen.aoc.util.toGrid

val day202504 = puzzle {
    val grid = lines.toGrid { it == '@' }.toMutableGrid()
    fun eval(remove: Boolean) = grid.points.count { point ->
        (grid[point] && grid.adjacent(point).count { it } < 4).also { if (it && remove) grid[point] = false }
    }

    partOne = eval(false)
    partTwo = generateSequence { eval(true).takeIf { it != 0 } }.sum()
}

//val day202504 = puzzle {
//    val sides = listOf(
//        -1 to -1,
//        -1 to 0,
//        -1 to 1,
//        0 to -1,
//        0 to 1,
//        1 to -1,
//        1 to 0,
//        1 to 1
//    )
//
//    val grid = lines.map {
//        it.toCharArray().map { c -> c == '@' }.toMutableList()
//    }
//    val xRange = 0..<grid.first().size
//    val yRange = 0..<grid.size
//
//    var rolls = 0
//    grid.forEachIndexed { i, row ->
//        row.forEachIndexed { j, col ->
//            if (!col) return@forEachIndexed
////            val adjacent = sides
////                .map { (dx, dy) -> (j + dx) to (i + dy) }
////                .filter { (x, y) -> x in 0..<width && y in 0..<height }
//
//            var adjacent = 0
//            for ((dx, dy) in sides) {
//                val x = j + dx
//                val y = i + dy
//                if (x in xRange && y in yRange && grid[y][x]) adjacent++
//            }
//            if (adjacent < 4) rolls++
//        }
//    }
//    partOne = rolls
//
//    rolls = 0
//    var done = true
//    loop@ while (done) {
//        done = false
//        grid.forEachIndexed { i, row ->
//            row.forEachIndexed { j, col ->
//                if (!col) return@forEachIndexed
////            val adjacent = sides
////                .map { (dx, dy) -> (j + dx) to (i + dy) }
////                .filter { (x, y) -> x in 0..<width && y in 0..<height }
//
//                var adjacent = 0
//                for ((dx, dy) in sides) {
//                    val x = j + dx
//                    val y = i + dy
//                    if (x in xRange && y in yRange && grid[y][x]) adjacent++
//                }
//                if (adjacent < 4) {
//                    rolls++
//                    done = true
//                    grid[i][j] = false
//                }
//            }
//        }
//    }
//    partTwo = rolls
//}
