package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle

val day202503 = puzzle {
    val banks = lines
        .map { line ->
            line
                .split("")
                .mapNotNull { it.toIntOrNull() }
        }

    fun solve(n: Int) = banks.sumOf { bank ->
        var bank = bank
        var joltage = 0L

        for (digit in n downTo 1) {
            val index = (0..bank.size - digit).maxBy(bank::get)
            joltage = 10 * joltage + bank[index]
            bank = bank.drop(index + 1)
        }

        joltage
    }

    partOne = solve(2)
    partTwo = solve(12)
}
