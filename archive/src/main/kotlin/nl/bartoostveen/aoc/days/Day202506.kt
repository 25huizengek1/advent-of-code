package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.product
import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.split
import nl.bartoostveen.aoc.util.splitByWhitespace
import nl.bartoostveen.aoc.util.toGrid

val day202506 = puzzle {
    fun calculate(
        op: Char,
        numbers: List<Long>
    ) = when (op) {
        '+' -> numbers.sum()
        '*' -> numbers.product()
        else -> 0L
    }

    partOne = lines
        .map { it.splitByWhitespace() }
        .toGrid()
        .columns
        .sumOf { line ->
            calculate(
                op = line.last().trim().first(),
                numbers = line.dropLast(1).map { it.toLong() }
            )
        }

    partTwo = raw
        .lines()
        .filter { it.isNotBlank() }
        .dropLast(1)
        .map { it.toList() }
        .toGrid()
        .let { grid ->
            val operations = lines.last().splitByWhitespace().map { it.first() }

            grid
                .columns
                .split { col -> col.all { it == ' ' } }
                .map { nums -> nums.mapNotNull { it.toCharArray().concatToString().trim().toLongOrNull() } }
                .toList()
                .let { rows ->
                    val numbersPerOperation = rows.maxOf { it.size }

                    rows.mapIndexed { column, row ->
                        when {
                            row.size == numbersPerOperation -> row
                            row.size < numbersPerOperation -> List(numbersPerOperation - row.size) { null } + row
                            else -> error("Invalid column $column")
                        }
                    }
                }
                .toGrid()
                .rows
                .zip(operations.asSequence())
                .sumOf { (numbers, op) -> calculate(op, numbers.filterNotNull()) }
        }
}
