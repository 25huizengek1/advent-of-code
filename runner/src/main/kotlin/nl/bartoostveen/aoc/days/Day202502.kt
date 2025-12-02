package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.runner.puzzle

val day202502 = puzzle {
    val ranges = lines
        .first()
        .split(",")
        .map { range ->
            val (min, max) = range.split("-")
            min.toLong()..max.toLong()
        }

    partOne = ranges.sumOf { range ->
        range.sumOf { num ->
            val str = num.toString()
            val len = str.length
            val halfLength = len shr 1

            if (len % 2 == 0 && str.take(halfLength) == str.drop(halfLength)) num else 0
        }
    }

    partTwo = ranges.sumOf { range ->
        range.sumOf innerSum@{ num ->
            val str = num.toString()
            val len = str.length

            forLoop@ for (i in 1..(len shr 1)) {
                if (len % i != 0) continue

                var copy = str.drop(i)
                val substring = str.take(i)

                while (copy.isNotEmpty()) {
                    if (copy.startsWith(substring)) copy = copy.drop(i)
                    else continue@forLoop
                }

                return@innerSum num
            }

            0
        }
    }
}
