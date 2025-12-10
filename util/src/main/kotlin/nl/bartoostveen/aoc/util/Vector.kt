package nl.bartoostveen.aoc.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

data class Vec2i(val x: Int, val y: Int) {
    companion object {
        val unit = Vec2i(0, 0)
    }

    operator fun plus(other: Vec2i) = Vec2i(x + other.x, y + other.y)
    operator fun plus(other: Direction) = plus(other.vec)
    operator fun minus(other: Vec2i) = plus(-other)
    operator fun unaryMinus() = Vec2i(-x, -y)
    operator fun times(other: Vec2i) = Vec2i(x * other.x, y * other.y)
    infix fun dot(other: Vec2i) = (this * other).let { it.x + it.y }
    operator fun div(other: Vec2i) = Vec2i(x / other.x, y / other.y)
    infix fun distanceTo(other: Vec2i) = sqrt(
        (other.x - x).toDouble().pow(2) +
                (other.y - y).toDouble().pow(2)
    )

    val length get() = distanceTo(unit)
    fun square(sideLength: Int) = this rect (this + Vec2i(sideLength, sideLength))
}

infix fun Int.point(other: Int) = Vec2i(this, other)
fun Pair<Int, Int>.toPoint() = first point second
val List<Int>.two: Vec2i
    get() {
        val (x, y) = take(2)
        return Vec2i(x, y)
    }

@ConsistentCopyVisibility
data class Rect internal constructor(val min: Vec2i, val max: Vec2i) {
    val width get() = max.x - min.x + 1
    val height get() = max.y - min.y + 1
    val xRange get() = min.x..max.x
    val yRange get() = min.y..max.y
    val area get() = width.toLong() * height.toLong()

    val isHorizontalLine get() = min.y == max.y
    val isVerticalLine get() = min.x == max.x

    fun shrink(amount: Int) = Rect(
        Vec2i(min.x + amount, min.y + amount),
        Vec2i(max.x - amount, max.y - amount),
    )

    infix fun overlaps(other: Rect) = xRange overlaps other.xRange && yRange overlaps other.xRange
    infix fun intersects(other: Rect) = when {
        other.isVerticalLine -> intersectsStraight(
            line = other,
            coordinate = Vec2i::x,
            opposite = Vec2i::y,
            range = Rect::xRange
        )

        other.isHorizontalLine -> intersectsStraight(
            line = other,
            coordinate = Vec2i::y,
            opposite = Vec2i::x,
            range = Rect::yRange
        )

        else -> error("Impossible")
    }
}

infix fun IntProgression.overlaps(other: IntProgression): Boolean {
    val max = maxOf(first, last)
    val min = minOf(first, last)

    return (other.first in min..max) || (other.last in min..max)
}

private fun Rect.intersectsStraight(
    line: Rect,
    coordinate: (Vec2i) -> Int,
    opposite: (Vec2i) -> Int,
    range: (Rect) -> IntRange
) = coordinate(line.min) in range(shrink(1)) && maxOf(opposite(min), opposite(line.min)) < minOf(
    opposite(max),
    opposite(line.max)
)

infix fun Vec2i.rect(other: Vec2i) = Rect(
    min = Vec2i(
        x = min(x, other.x),
        y = min(y, other.y)
    ),
    max = Vec2i(
        x = max(x, other.x),
        y = max(y, other.y)
    )
)

val List<Vec2i>.rect: Rect
    get() {
        val (a, b) = take(2)
        return a rect b
    }

data class Vec3i(val x: Int, val y: Int, val z: Int) {
    companion object {
        val unit = Vec3i(0, 0, 0)
    }

    constructor(point: Vec2i, z: Int) : this(point.x, point.y, z)

    operator fun plus(other: Vec3i) = Vec3i(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3i) = plus(-other)
    operator fun unaryMinus() = Vec3i(-x, -y, -z)
    operator fun times(other: Vec3i) = Vec3i(x * other.x, y * other.y, z * other.z)
    infix fun dot(other: Vec3i) = (this * other).let { it.x + it.y + it.z }
    infix fun cross(other: Vec3i) = Vec3i(
        x = y * other.z - z * other.y,
        y = z * other.x - x * other.z,
        z = x * other.y - y * other.x
    )

    operator fun div(other: Vec3i) = Vec3i(x / other.x, y / other.y, z / other.z)
    infix fun distanceTo(other: Vec3i) = sqrt(
        (other.x - x).toDouble().let { it * it } +
                (other.y - y).toDouble().let { it * it } +
                (other.z - z).toDouble().let { it * it }
    )

    infix fun euclidTo(other: Vec3i) =
        (x - other.x).toLong().let { it * it } +
                (y - other.y).toLong().let { it * it } +
                (z - other.z).toLong().let { it * it }

    val length get() = distanceTo(unit)
}

infix fun Vec2i.point(other: Int) = Vec3i(this, other)
fun Triple<Int, Int, Int>.toVector() = first point second point third
val List<Int>.three: Vec3i
    get() {
        val (x, y, z) = take(3)
        return Vec3i(x, y, z)
    }

@ConsistentCopyVisibility
data class Cube internal constructor(val lowerBound: Vec3i, val upperBound: Vec3i) {
    val volume
        get() = (upperBound.x - lowerBound.x) *
                (upperBound.y - lowerBound.y) *
                (upperBound.z - lowerBound.z)
}

infix fun Vec3i.cube(other: Vec3i) = Cube(
    lowerBound = Vec3i(
        x = min(x, other.x),
        y = min(y, other.y),
        z = min(z, other.z)
    ),
    upperBound = Vec3i(
        x = max(x, other.x),
        y = max(y, other.y),
        z = max(z, other.z)
    )
)
