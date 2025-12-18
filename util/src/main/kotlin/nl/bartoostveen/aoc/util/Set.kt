package nl.bartoostveen.aoc.util

import java.util.Collections

@ConsistentCopyVisibility
data class SetUnionFindContext<T> @PublishedApi internal constructor(
    internal val underlying: HashMap<T, Entry<T>> = hashMapOf()
) : Map<T, SetUnionFindContext.Entry<T>> by underlying {
    data class Entry<T>(
        val value: T,
        var rank: Int = 0,
        var parent: Entry<T>? = null
    ) {
        val root: Entry<T> get() = parent?.root?.also { parent = it } ?: this
    }

    fun contains(first: T, second: T): Boolean {
        return (root(first) ?: return false) == (root(second) ?: return false)
    }

    operator fun contains(pair: Pair<T, T>) = contains(pair.first, pair.second)
    operator fun contains(pair: SetTuple<T>) = contains(pair.first, pair.second)

    fun add(value: T): Boolean {
        if (value in this) return false
        underlying[value] = Entry(value)
        return true
    }

    fun addPair(pair: Pair<T, T>) = addPair(pair.first, pair.second)
    fun addPair(pair: SetTuple<T>) = addPair(pair.first, pair.second)

    fun addPair(first: T, second: T): Boolean {
        if (contains(first, second)) return false

        val firstRoot = rootOfOrNew(first)
        val secondRoot = rootOfOrNew(second)
        if (firstRoot == secondRoot) return false

        when {
            firstRoot.rank < secondRoot.rank -> firstRoot.parent = secondRoot
            firstRoot.rank > secondRoot.rank -> secondRoot.parent = firstRoot
            else -> {
                firstRoot.parent = secondRoot
                secondRoot.rank++
            }
        }

        return true
    }

    fun root(v: T) = underlying[v]?.root
    fun rootOfOrNew(v: T) = underlying.getOrPut(v) { Entry(v) }.root
    val roots get() = keys.filter { getValue(it).parent == null }.toSet()
    val islands get() = keys.groupBy { getValue(it).root }
}

inline fun <reified T> unionFind(block: SetUnionFindContext<T>.() -> Unit) =
    SetUnionFindContext<T>().apply(block)

// Thanks for the very efficient permutations impl (github:770grappenmaker)
fun <T> Iterable<T>.permutations() = toList().permutations()
fun <T> Iterable<T>.permutations(r: Int) = toList().permutations(r)

// Inspired by:
// https://docs.python.org/3/library/itertools.html#itertools.permutations
fun <T> List<T>.permutations(r: Int = size): Sequence<List<T>> = sequence {
    if (r > size || isEmpty()) return@sequence

    val ind = indices.toMutableList()
    val cyc = (size downTo size - r).toMutableList()
    yield(take(r))

    while (true) {
        for (i in r - 1 downTo 0) {
            if (--cyc[i] == 0) {
                ind.add(ind.removeAt(i))
                cyc[i] = size - i
            } else {
                Collections.swap(ind, i, size - cyc[i])
                yield(slice(ind.take(r)))
                break
            }

            if (i == 0) return@sequence
        }
    }
}

fun <T> Iterable<T>.combinations() = toList().combinations()
fun <T> Sequence<T>.combinations() = toList().combinations()
fun <T> Iterable<T>.combinations(r: Int) = toList().combinations(r)
fun <T> Sequence<T>.combinations(r: Int) = toList().combinations(r)
fun <T> List<T>.combinations(r: Int = size) =
    indices
        .permutations(r)
        .filter { it.sorted() == it }.map { p -> p.map { this[it] } }

fun <T> Iterable<T>.permutations2(): Sequence<Pair<T, T>> = sequence {
    forEachIndexed { idx, i ->
        forEachIndexed { jdx, j ->
            if (idx != jdx) yield(i to j)
        }
    }
}