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

@ConsistentCopyVisibility
data class Rect internal constructor(val topLeft: Vec2i, val bottomRight: Vec2i) {
    val area get() = (bottomRight.x - topLeft.x) * (topLeft.y - bottomRight.y)
    val square get() = (topLeft - bottomRight).let { it.x == it.y }
}

infix fun Vec2i.rect(other: Vec2i) = Rect(
    topLeft = Vec2i(
        x = min(x, other.x),
        y = max(y, other.y)
    ),
    bottomRight = Vec2i(
        x = max(x, other.x),
        y = min(y, other.y)
    )
)

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
        (other.x - x).toDouble().pow(2) +
                (other.y - y).toDouble().pow(2) +
                (other.z - z).toDouble().pow(2)
    )

    val length get() = distanceTo(unit)
}

infix fun Vec2i.point(other: Int) = Vec3i(this, other)
fun Triple<Int, Int, Int>.toVector() = first point second point third

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
