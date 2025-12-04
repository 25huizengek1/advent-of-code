package nl.bartoostveen.aoc.util

import kotlin.math.abs

@JvmInline
value class Direction private constructor(val vec: Vec2i) {
    constructor(x: Int, y: Int) : this(x point y)

    init {
        require(vec.x == 0 || vec.y == 0 || abs(vec.x) == abs(vec.y)) {
            "Direction invalid! |vec.x| != |vec.y|, got vec $vec"
        }
    }

    companion object {
        val UP = Direction(0, 1)
        val DOWN = Direction(0, -1)
        val LEFT = Direction(-1, 0)
        val RIGHT = Direction(1, 0)
        val TOP_LEFT = UP + LEFT
        val TOP_RIGHT = UP + RIGHT
        val BOTTOM_LEFT = DOWN + LEFT
        val BOTTOM_RIGHT = DOWN + RIGHT

        val horizontal = listOf(LEFT, RIGHT)
        val vertical = listOf(UP, DOWN)
        val diagonal = listOf(TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT)
        val all = horizontal + vertical + diagonal
    }

    val opposite get() = Direction(-vec)

    private operator fun plus(other: Direction) = Direction(vec + other.vec)
}

operator fun Vec2i.plus(direction: Direction) = plus(direction.vec)
