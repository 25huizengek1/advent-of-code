@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package nl.bartoostveen.aoc.util

private fun <T> Grid<T>.assertValidSize() = require(elements.size == width * height) {
    "Invalid grid! Expected: ${elements.size}, actual: width ($width) * height ($height) = ${width * height}"
}

abstract class Grid<T> : List<T> {
    abstract val elements: List<T>
    abstract val width: Int
    abstract val height: Int

    private val yRange by lazy { 0..<height }
    private val xRange by lazy { 0..<width }

    fun index(
        x: Int,
        y: Int
    ) = y * width + x
    fun index(point: Vec2i) = point.y * width + point.x
    operator fun get(
        x: Int,
        y: Int
    ) = elements[index(x, y)]
    operator fun get(point: Vec2i) = elements[index(point)]
    operator fun contains(point: Vec2i) = point.x in xRange && point.y in yRange
    fun getOrNull(
        x: Int,
        y: Int
    ) = elements.getOrNull(index(x, y))
    fun getOrNull(point: Vec2i) = elements.getOrNull(index(point))
    fun row(row: Int): List<T> {
        val start = row * width
        return elements.subList(start, start + width)
    }

    fun column(col: Int) = yRange.map { elements[it * width + col] }
    fun toMutableGrid() = MutableGrid(elements.toMutableList(), width, height)

    fun adjacentPoints(
        point: Vec2i,
        sides: List<Direction> = Direction.all
    ) = sides
        .asSequence()
        .map { side -> point + side }
        .filter { it.x in 0..<width && it.y in 0..<height }

    fun adjacent(
        point: Vec2i,
        sides: List<Direction> = Direction.all
    ) = adjacentPoints(point, sides).map(::get)

    val points: Sequence<Vec2i>
        get() = yRange
            .asSequence()
            .flatMap { y -> xRange.map { x -> x point y } }

    val rows get() = yRange.asSequence().map { row(it) }
    val columns get() = xRange.asSequence().map { column(it) }
}

data class ImmutableGrid<T>(override val elements: List<T>, override val width: Int, override val height: Int) :
    Grid<T>(),
    List<T> by elements {
    init {
        assertValidSize()
    }
}

data class MutableGrid<T>(override val elements: MutableList<T>, override val width: Int, override val height: Int) :
    Grid<T>(),
    MutableList<T> by elements {
    init {
        assertValidSize()
    }

    operator fun set(
        x: Int,
        y: Int,
        value: T
    ) {
        elements[index(x, y)] = value
    }

    operator fun set(
        point: Vec2i,
        value: T
    ) {
        elements[index(point)] = value
    }
}

fun <T> List<String>.toGrid(map: (Char) -> T): Grid<T> = ImmutableGrid(
    elements = flatMap { row -> row.toCharArray().map(map) },
    width = first().length,
    height = size
)

fun <T> List<List<T>>.toGrid() = flatten().toGrid(first().size, size)
fun <T> List<T>.toGrid(
    width: Int,
    height: Int
) = ImmutableGrid(this, width, height)
fun List<String>.toGrid() = toGrid { it }
