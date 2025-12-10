package nl.bartoostveen.aoc.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

data class Puzzle(val raw: String) {
    val lines: List<String> by lazy {
        raw.lines().filter { it.isNotBlank() }.map { it.trim() }
    }

    private val values = mutableListOf<Any?>()

    private fun <T : Any> part() = object : ReadWriteProperty<Puzzle, T?> {
        private val index = values.size

        init {
            values.add(null)
        }

        @Suppress("UNCHECKED_CAST")
        override fun getValue(
            thisRef: Puzzle,
            property: KProperty<*>
        ) = values[index] as? T

        override fun setValue(
            thisRef: Puzzle,
            property: KProperty<*>,
            value: T?
        ) {
            values[index] = value
        }
    }

    var partOne: Any? by part()
    var partTwo: Any? by part()

    fun print() {
        println("Solved AOC puzzle:")
        values.forEachIndexed { i, value ->
            println("Part ${i + 1}: ${value ?: "Unimplemented"}")
        }
    }
}

typealias Solution = Puzzle.() -> Unit

// marker function
fun puzzle(block: Solution) = block
