package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.*

val day202508 = puzzle {
    unionFind {
        val points = lines.map {
            it
                .split(',')
                .take(3)
                .map(String::toInt)
                .three
        }

        var combinedPairs = 0
        val seen = hashSetOf<TupleSet<Vec3i>>()
        points
            .permutations2()
            .sortedBy { (a, b) -> a euclidTo b }
            .map { it.unordered() }
            .forEach { pair ->
                if (!seen.add(pair)) return@forEach

                if (++combinedPairs == 1000) partOne = islands
                    .values
                    .map { it.size }
                    .sortedDescending()
                    .take(3)
                    .product()

                addPair(pair)

                if (roots.size == 1 && size == points.size) {
                    partTwo = pair.first.x.toULong() * pair.second.x.toULong()
                    return@puzzle
                }
            }
    }
}
