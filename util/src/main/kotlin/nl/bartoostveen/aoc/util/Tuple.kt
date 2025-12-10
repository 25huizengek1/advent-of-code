package nl.bartoostveen.aoc.util

data class SetTuple<T>(val first: T, val second: T) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SetTuple<*>) return false

        if (first == other.first && second == other.second) return true
        if (first == other.second && second == other.first) return true

        return false
    }

    override fun hashCode(): Int {
        val ah = first?.hashCode() ?: 0
        val bh = second?.hashCode() ?: 0
        return ah xor bh
    }
}

fun <A, B> Pair<A, B>.swap() = second to first
fun <T> Pair<T, T>.unordered() = SetTuple(first, second)
fun <T> SetTuple<T>.ordered() = first to second

infix fun <T> T.with(other: T) = SetTuple(this, other)
