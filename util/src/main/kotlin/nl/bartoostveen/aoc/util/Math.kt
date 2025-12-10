package nl.bartoostveen.aoc.util

fun List<Float>.product() = fold(1f) { acc, num -> acc * num }
fun List<Double>.product() = fold(1.0) { acc, num -> acc * num }
fun List<Int>.product() = fold(1) { acc, num -> acc * num }
fun List<Long>.product() = fold(1L) { acc, num -> acc * num }

fun <T : Comparable<Number>> T.sign() = when {
    this > 0 -> 1
    this < 0 -> -1
    else -> 0
}